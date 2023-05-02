package com.example.demo.service;

import com.example.demo.common.APIList;
import com.example.demo.constants.StringConstants;
import com.example.demo.helper.DataEncrypterDecrypter;
import com.example.demo.helper.Service.PatientServiceHelper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.utility.TokenUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.demo.common.ResponseHelper.prepareHeader;
import static com.example.demo.helper.DataEncrypterDecrypter.receiverKeys;
import static com.example.demo.helper.Service.ConsentRequestServiceHelper.*;
import static com.example.demo.helper.misc.*;

@Service
public class ConsentRequestService {
    Logger logger = LoggerFactory.getLogger(ConsentRequestService.class);
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ConsentRequestRepository consentRequestRepository;
    @Autowired
    ConsentRepository consentRepository;
    @Autowired
    CareContextRepository careContextRepository;

    @Autowired
    VisitRepository visitRepository;
    public ConsentRequest prepareConsentRequest(String req) {

        logger.info("Entering prepareConsentRequest with data: " + req);

        JSONObject requestObj = new JSONObject(req);
        ConsentRequest consentRequest = new ConsentRequest();
        consentRequest.setPurpose(requestObj.getString("purpose"));
        consentRequest.setPurposeCode("CAREMGT");

        // TODO: check if converting time is good
        consentRequest.setDateFrom((requestObj.getString("dateFrom")));
        consentRequest.setDateTo((requestObj.getString("dateTo")));
        consentRequest.setDataEraseAt((requestObj.getString("dateEraseAt")));
        consentRequest.setAccessMode("VIEW");

        consentRequest.setHiTypes(requestObj.get("hiTypes").toString());
        consentRequest.setPatient(patientRepository.findPatientById(Long.parseLong(requestObj.getString("patientId"))));
        consentRequest.setDoctor(employeeRepository.findEmployeeById(Long.parseLong(requestObj.getString("doctorId"))));

        Visit visit = visitRepository.findVisitById(Long.parseLong(requestObj.getString("visitId")));

        consentRequest.setVisit(visit);
        visit.addConsentRequest(consentRequest);
        logger.info("Exiting prepareConsentRequest with data if saved " + consentRequest);

        return consentRequestRepository.save(consentRequest);
    }

    public JSONObject prepareConsentRequestInIt(ConsentRequest consentRequest) {
        logger.info("Entering prepareConsentRequestInIt with data: " + consentRequest);
        JSONObject response = new JSONObject();
        response.put("requestId", getRandomUUID());
        response.put("timestamp", getTimeStamp());
        response.put("consent", getConsentObjectForInIt(consentRequest));
        consentRequest.setRequestId(response.get("requestId").toString());
        consentRequestRepository.save(consentRequest);
        logger.info("Exiting prepareConsentRequestInIt with data " + consentRequest);
        return response;
    }

    public String fireABDMConsentRequestInit(ConsentRequest consentRequest) {
        logger.info("Entering fireABDMConsentRequestInit with data: " + consentRequest);
        JSONObject requestBody = prepareConsentRequestInIt(consentRequest);
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = prepareHeader(authToken);

        HttpEntity<String> entity = new HttpEntity<String>(requestBody.toString(), headers);
        restTemplate.postForObject(APIList.CARE_CONTEXT_INIT, entity, String.class);
        return requestBody.get("requestId").toString();
    }

    public String[] prepareOnConsentRequestInitResponse(String responseBody) {
        logger.info("Entering prepareOnConsentRequestInitResponse with data : " + responseBody);
        JSONObject obj = new JSONObject(responseBody);
        String requestId = obj.getJSONObject("resp").get("requestId").toString();
        JSONObject response = new JSONObject();
        String[] ans = new String[3];
        ans[0] = requestId;
        if (obj.isNull("error")) {
            response.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
            response.put(StringConstants.MSG, "Consent request sent successfully");
            ans[1] = obj.getJSONObject("consentRequest").getString("id");
        }
        else {
            response.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
            response.put(StringConstants.MSG, obj.getJSONObject("error").getString("message"));
            ans[1] = null;
        }
        ans[2] = response.toString();
        return ans;
    }

    public void updateConsentRequestId(String requestId, String consentRequestId) {
        logger.info("Entering updateConsentRequestId with data requestId: " + requestId + " consentRequestId: " + consentRequestId);
        ConsentRequest consentRequest = consentRequestRepository.findConsentRequestByRequestId(requestId);
        consentRequest.setConsentRequestId(consentRequestId);
        consentRequest.setStatus("REQUESTED");
        consentRequestRepository.save(consentRequest);
        logger.info("exiting updateConsentRequestId after saving data: " + consentRequest);
    }

    public void updateConsentRequestStatusFailed(String requestId) {
        logger.info("Entering updateConsentRequestStatusFailed with data: " + requestId );
        ConsentRequest consentRequest = consentRequestRepository.findConsentRequestByConsentRequestId(requestId);
        consentRequest.setStatus("FAILED");
        consentRequestRepository.save(consentRequest);
        logger.info("exiting updateConsentRequestStatusFailed after saving consentRequest" + consentRequest);
    }

    public boolean updateConsentRequestStatus(JSONObject obj) {
        logger.info("entering updateConsentRequestStatus with data: " + obj);
        String consentRequestId = obj.getJSONObject("notification").getString("consentRequestId");
        String status = obj.getJSONObject("notification").getString("status");
        ConsentRequest consentRequest = consentRequestRepository.findConsentRequestByConsentRequestId(consentRequestId);
        consentRequest.setStatus(status);
        if (status.equals("GRANTED")) {
            for (Object object : obj.getJSONObject("notification").getJSONArray("consentArtefacts")) {
                JSONObject artifactObj = (JSONObject) object;
                Consent consent = new Consent();
                consent.setConsentId(artifactObj.getString("id"));
                consent.setStatus("REQUESTED");
                consentRepository.save(consent);
                consentRequest.addConsent(consent);
            }
        }
        consentRequestRepository.save(consentRequest);
        logger.info("exiting updateConsentRequestStatus after saving consent request: " + consentRequestId);
        return status.equals("GRANTED");
    }

    public String fireArtifactsFetchRequest(JSONArray arr) {
        logger.info("Entering fireArtifactsFetchRequest with data : "  +arr);
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = prepareHeader(authToken);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject requestObj = prepareFetchRequestObj(((JSONObject)arr.get(i)).getString("id"));
            HttpEntity<String> entity = new HttpEntity<String>(requestObj.toString(), headers);
            restTemplate.postForObject(APIList.CARE_CONTEXT_FETCH, entity, String.class);
            Consent consent = consentRepository.findConsentByConsentId(((JSONObject)arr.get(i)).getString("id"));
            consent.setRequestId(requestObj.get("requestId").toString());
            consentRepository.save(consent);
        }
        return "";
    }

    public Consent updateConsentRequestAfterOnFetch(JSONObject requestObj) {
        logger.info("Entering updateConsentRequestAfterOnFetch with data:" + requestObj);
        Consent consent = consentRepository.findConsentByRequestId(requestObj.getJSONObject("resp").get("requestId").toString());
        requestObj = requestObj.getJSONObject("consent");
        consent.setStatus(requestObj.getString("status"));
        consent.setSignature(requestObj.getString("signature"));
        requestObj = requestObj.getJSONObject("consentDetail");
        assert consent.getConsentId().equals(requestObj.getString("consentId"));
        JSONArray careContextArr = requestObj.isNull("careContexts") ? new JSONArray() : requestObj.getJSONArray("careContexts");
        for (int i = 0; i < careContextArr.length(); i++) {
            JSONObject cc = (JSONObject) careContextArr.get(i);
            CareContext careContext = new CareContext(cc.getString("patientReference"), cc.getString("careContextReference"));
            consent.addCareContext(careContext);
            careContextRepository.save(careContext);
        }
        consent.setHiTypes(requestObj.getJSONArray("hiTypes").toString());
        consent.setAccessMode(requestObj.getJSONObject("permission").getString("accessMode"));
        consent.setDataFrom(requestObj.getJSONObject("permission").getJSONObject("dateRange").getString("from"));
        consent.setDataTo(requestObj.getJSONObject("permission").getJSONObject("dateRange").getString("to"));
        consent.setDataEraseAt(requestObj.getJSONObject("permission").getString("dataEraseAt"));
        HashMap<String, String> keys = receiverKeys();
        consent.setReceiverPublicKey(keys.get("publicKey"));
        consent.setReceiverPrivateKey(keys.get("privateKey"));
        consent.setReceiverNonce(keys.get("random"));
        logger.info("exiting updateConsentRequestAfterOnFetch after saving consent" + consent);
        return consentRepository.save(consent);
    }

    public String fireABDMHealthInformationCMRequest(Consent consent) {
        logger.info("entering fireABDMHealthInformationCMRequest with data: " + consent);
        JSONObject requestBody = prepareHealthInformationCMRequest(consent);
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = prepareHeader(authToken);

        logger.info("requestbody to cm healthrequest" + requestBody);

        HttpEntity<String> entity = new HttpEntity<String>(requestBody.toString(), headers);
        consent.setRequestId(requestBody.get("requestId").toString());

        consentRepository.save(consent);
        logger.info("exiting after saving data: consent" +consent);
        logger.info("entity: " + entity.toString());

        restTemplate.postForObject(APIList.HEALTH_DATA_REQUEST, entity, String.class);
        return requestBody.get("requestId").toString();
    }

    public void updateConsentTransactionId(JSONObject requestBody) {
        logger.info("entering updateConsentTransactionId wiht data: " + requestBody);
        Consent consent = consentRepository.findConsentByRequestId(requestBody.getJSONObject("resp").get("requestId").toString());
        consent.setStatus(requestBody.getJSONObject("hiRequest").getString("sessionStatus"));
        consent.setTransactionId(requestBody.getJSONObject("hiRequest").getString("transactionId"));
        consentRepository.save(consent);
        logger.info("exting updateConsentTransactionId after saving data:  " + consent);
    }
    public void saveData(JSONObject data) {
        logger.info("Entering save data with data: " + data);
        Consent consent = consentRepository.findConsentByTransactionId(data.getString("transactionId"));
        String senderPublicKey = data.getJSONObject("keyMaterial").getJSONObject("dhPublicKey").getString("keyValue");
        String senderNonce = data.getJSONObject("keyMaterial").getString("nonce");
        String receiverNonce = consent.getReceiverNonce();
        String receiverPrivateKey = consent.getReceiverPrivateKey();
        JSONArray arr = data.getJSONArray("entries");
        List<CareContext> careContexts = consent.getCareContextList();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String careContextReference = obj.getString("careContextReference");

            //     Instead of searching care context don't save care context before instead start storing care context which you received
            //     Cannot do this because when consent is granted we have to check again updated consent parameters.
            String encryptedData = obj.getString("content");
            CareContext tempCareContext = findCareContext(careContexts, careContextReference);
            HashMap<String, String> decodedMsg = updateCareContextData(senderNonce, senderPublicKey, receiverNonce, receiverPrivateKey, encryptedData);
            tempCareContext.setDoctorId(decodedMsg.getOrDefault("doctorId", "temp-doctor-id"));
            tempCareContext.setPatientId(decodedMsg.getOrDefault("patientId", "temp-patient-id"));
            tempCareContext.setPatientName(decodedMsg.getOrDefault("patientName", "temp-patient-name"));
            tempCareContext.setDoctorName(decodedMsg.getOrDefault("doctorName", "temp-doctor-name"));
            tempCareContext.setDosageInstruction(decodedMsg.getOrDefault("dosageInstruction", "1 time a day"));
            tempCareContext.setPrescription(decodedMsg.getOrDefault("medicineName", "Paracetamol 650 mg"));
            tempCareContext.setDiagnosis(decodedMsg.getOrDefault("diagnosis", "Fever"));
            careContextRepository.save(tempCareContext);
            logger.info("saving carecontext : " + tempCareContext);
        }
        consent.setStatus("DELIVERED");
    }
}
