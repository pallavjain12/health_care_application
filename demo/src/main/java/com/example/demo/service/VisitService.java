package com.example.demo.service;

import com.example.demo.common.APIList;
import com.example.demo.constants.StringConstants;
import com.example.demo.model.Patient;
import com.example.demo.model.Visit;
import com.example.demo.repository.VisitRepository;

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
import static com.example.demo.utility.TokenUtil.getAccessToken;
import static com.example.demo.helper.Service.VisitServiceHelper.prepareAddContextRequest;

@Service
public class VisitService {
    Logger logger = LoggerFactory.getLogger(VisitService.class);
    @Autowired
    VisitRepository visitRepository;
    public Visit createNewVisit(Patient patient) {
        logger.info("entering createnew visit with data:" + patient);
        List<Visit> list = visitRepository.findVisitByPatient_Id(patient.getId());
        Visit visit = new Visit(LocalDate.now(), "Visit-" + patient.getId() + "-" + (list.size() + 1), "Consultation on : " + LocalDate.now());
        visit.setPatient(patient);
        visitRepository.save(visit);
        logger.info("exiting createnewcisit with data: "+ visit);
        return visit;
    }

    public String addCareContext(Patient patient, String patientAuthToken) {
        logger.info("entering add carecontext with data: " + patientAuthToken + " and pateing = " + patient);
        RestTemplate restTemplate = new RestTemplate();

        String authToken = getAccessToken();
        if (authToken.equals("-1")) return null;
        Visit visit = createNewVisit(patient);
        JSONObject request = prepareAddContextRequest(patientAuthToken, visit, "" + patient.getId(), patient.getName());
        HttpHeaders headers = prepareHeader(authToken);
        visit.setRequestId(request.getString("requestId"));
        visitRepository.save(visit);
        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

        HttpEntity<String> response = restTemplate.exchange(APIList.ADD_CARE_CONTEXT, HttpMethod.POST, entity, String.class);
        logger.info("exiting add care context with response = " + response);
        return request.getString("requestId");
    }

    public String[] createOnAddContextResponse(String res) {
        logger.info("Entering createOnAddContextResponse with data: " + res );
        JSONObject obj = new JSONObject(res);
        JSONObject response = new JSONObject();
        JSONObject resp = obj.getJSONObject("resp");
        String requestId = resp.getString("requestId");
        Visit visit = visitRepository.findVisitByRequestId(requestId);
        if (obj.isNull("error")) {
            response.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
            response.put(StringConstants.MSG, "New visit created with id " + visit.getId());
            response.put(StringConstants.DATA, visit);
        }
        else {
            response.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
            response.put(StringConstants.MSG, "Unable to create a new visit. Token Expired. Please authenticate again");
            visitRepository.delete(visit);
        }
        logger.info("exinng createonaddcontextresponse with daving dtata" + response);
        return new String[]{requestId, response.toString()};
    }
}
