package com.example.demo.service;

import com.example.demo.common.APIList;
import com.example.demo.constants.StringConstants;
import com.example.demo.model.*;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.VisitRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static com.example.demo.common.ResponseHelper.prepareHeader;
import static com.example.demo.common.ResponseHelper.serverSideError;
import static com.example.demo.utility.TokenUtil.getAccessToken;
import static com.example.demo.helper.Service.VisitServiceHelper.prepareAddContextRequest;

@Service
public class VisitService {
    Logger logger = LoggerFactory.getLogger(VisitService.class);
    @Autowired
    VisitRepository visitRepository;

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    EmployeeRepository employeeRepository;
    public Visit createNewVisit(Patient patient) {
        logger.info("entering create new visit with data:" + patient.getPatientJSONObject());

        List<Visit> list = visitRepository.findVisitByPatient_Id(patient.getId());

        Visit visit = new Visit(LocalDate.now(), "Visit-" + patient.getId() + "-" + (list.size() + 1), "Consultation on : " + LocalDate.now());
        visit.setPatient(patient);

        logger.info("created a visit with data: " + visit);

        // Created a visit, saved it to repository,
        // Added that visit to patient and linked patient in that visit
        patient.addVisits(visitRepository.save(visit));
        patientRepository.save(patient);

        logger.info("exiting create new visit with data: "+ visit);

        return visit;
    }

    public String addCareContext(Patient patient, String patientAuthToken) {
        logger.info("entering add care context with data: " + patientAuthToken + " and patient = " + patient);

        String authToken = getAccessToken();
        if (authToken.equals("-1")) return null;

        Visit visit = createNewVisit(patient);

        // Prepare requestBody to send to ABDM.
        JSONObject request = prepareAddContextRequest(patientAuthToken, visit, "" + patient.getId(), patient.getName());
        visit.setRequestId(request.get("requestId").toString());
        visit.setVisitDate(LocalDate.now());
        visitRepository.save(visit);

        // Send request to ABDM
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = prepareHeader(authToken);
        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
        HttpEntity<String> response = restTemplate.exchange(APIList.ADD_CARE_CONTEXT, HttpMethod.POST, entity, String.class);

        logger.info("exiting add care context with response = " + response);
        return request.get("requestId").toString();
    }

    public String[] createOnAddContextResponse(String res) {
        logger.info("Entering createOnAddContextResponse with data: " + res );

        JSONObject obj = new JSONObject(res);
        JSONObject response = new JSONObject();
        JSONObject resp = obj.getJSONObject("resp");
        String requestId = resp.get("requestId").toString();

        Visit visit = visitRepository.findVisitByRequestId(requestId);
        if (obj.isNull("error")) {
            response.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
            response.put(StringConstants.MSG, "New visit created with id " + visit.getId());
            response.put(StringConstants.DATA, visit.getJSONObject());
        }
        else {
            response.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
            response.put(StringConstants.MSG, obj.getJSONObject("error").getString("message"));
            visitRepository.delete(visit);
        }
        logger.info("exiting create on add care context response with data" + response);
        return new String[]{requestId, response.toString()};
    }

    public JSONObject updatePrescription(String req) {
        logger.info("entering updatePescription with req:" + req);
        JSONObject obj = new JSONObject(req);
        Visit visit = visitRepository.findVisitById(Long.parseLong(obj.getString("visitId")));
        if (visit == null)  return serverSideError("Visit not found with id: " + obj.getString("visitId"));

        visit.setPrescription(obj.getString("prescription"));
        visit.setDiagnosis(obj.getString("diagnosis"));
        visit.setDosageInstruction(obj.getString("dosageInstruction"));
        visit.setDisabled(true);

        Employee employee = employeeRepository.findEmployeeById(Long.parseLong(obj.getString("doctorId")));
        if (employee == null) return serverSideError("No employee found with id: " + obj.getString("doctorId") + ". Please contact Administrator");

        visit.setDoctor(employee);

        visitRepository.save(visit);

        JSONObject response = new JSONObject();
        response.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
        response.put(StringConstants.MSG, "Prescription added successfully");
        response.put(StringConstants.DATA, visit.getJSONObject());
        logger.info("exiting update visit with request" + response);
        return response;
    }

    public String getVisitById(String req) {
        logger.info("entered getVisitById with data: " + req);
        JSONObject obj = new JSONObject(req);
        JSONObject response = new JSONObject();
        Visit visit = visitRepository.findVisitById(obj.getLong("visitId"));

        if (visit == null) {
            JSONObject res = new JSONObject();
            res.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
            res.put(StringConstants.MSG, "Visit with id : " + obj.getString("visitId") + " not found");
            return res.toString();
        }

        if (visit.getDoctor() != null && visit.getDoctor().getId() != Long.parseLong(obj.getString("doctorId"))) {
            JSONObject res = new JSONObject();
            res.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
            res.put(StringConstants.MSG, "Not authorized to view this visit");
            return res.toString();
        }

        Patient patient = visit.getPatient();
        Employee doctor = employeeRepository.findEmployeeById(obj.getLong("doctorId"));
        response.put("visit", visit.getJSONObject());
        response.put("patient", patient.getPatientJSONObject());
        response.put("otherVisit", prepareOldVisit(patient, doctor));
        response.put("consentRequests", prepareConsentRequest(visit));
        logger.info("exiting getVisitById with data: " + response);

        JSONObject object = new JSONObject();
        object.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
        object.put(StringConstants.MSG, "Visit Fetched Successfully");
        object.put(StringConstants.DATA, response);


        return object.toString();
    }

    public JSONArray prepareOldVisit(Patient patient, Employee doctor) {
        JSONArray arr = new JSONArray();
        List<Visit> listOfVisits = visitRepository.findVisitByDoctor_IdAndPatient_Id(doctor.getId(), patient.getId());
        for (int i = 0; i < listOfVisits.size(); i++) {
            Visit visit = listOfVisits.get(i);
            arr.put(visit.getJSONObject());
        }
        return arr;
    }

    public JSONObject prepareConsentRequest(Visit visit) {
        JSONObject mainObj = new JSONObject();
        mainObj.put("consentRequest", new JSONArray());
        List<ConsentRequest> consentRequestList = visit.getConsentRequestList();
        for (int i = 0; i < consentRequestList.size(); i++) {
            ConsentRequest consentRequest = consentRequestList.get(i);
            JSONObject consentRequestObj = new JSONObject();

            consentRequestObj.put("id", consentRequest.getRequestId());
            consentRequestObj.put("status", consentRequest.getStatus());
            consentRequestObj.put("consent", new JSONArray());
            if (consentRequest.getStatus().equals("REQUESTED")) {}
            else {
                List<Consent> consentList = consentRequest.getConsentList();
                for (int j = 0; j < consentList.size(); j++) {
                    Consent consent = consentList.get(j);
                    JSONObject consentObj = new JSONObject();
                    consentObj.put("id", consent.getConsentId());
                    consentObj.put("status", consent.getStatus());
                    if (!consent.getStatus().equals("DELIVERED")) {
                        consentObj.put("careContext", new JSONArray());
                        consentRequestObj.getJSONArray("consent").put(consentObj);
                        continue;
                    }
                    consentObj.put("careContext", new JSONArray());
                    List<CareContext> careContextList = consent.getCareContextList();

                    for (int k = 0; k < careContextList.size(); k++) {
                        CareContext careContext = careContextList.get(k);
                        JSONObject careContextObj = new JSONObject();
                        careContextObj.put("patientReference", careContext.getPatientReference());
                        careContextObj.put("prescription", careContext.getPrescription());
                        careContextObj.put("diagnosis", careContext.getDiagnosis());
                        careContextObj.put("dosageInstruction", careContext.getDosageInstruction());
                        careContextObj.put("doctorName", careContext.getDoctorName());
                        careContextObj.put("doctorId", careContext.getDoctorId());
                        careContextObj.put("careContextReference", careContext.getCareContextReference());
                        consentObj.getJSONArray("careContext").put(careContextObj);
                    }
                    consentRequestObj.getJSONArray("consent").put(consentObj);
                }
            }
            mainObj.getJSONArray("consentRequest").put(consentRequestObj);
        }
        return mainObj;
    }
}
