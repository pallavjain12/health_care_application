package com.example.demo.service;

import com.example.demo.common.APIList;
import com.example.demo.constants.StringToChange;
import com.example.demo.model.CareContext;
import com.example.demo.model.Consent;
import com.example.demo.model.Patient;
import com.example.demo.model.Visit;
import com.example.demo.repository.CareContextRepository;
import com.example.demo.repository.ConsentRepository;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.VisitRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

import static com.example.demo.common.ResponseHelper.prepareHeader;
import static com.example.demo.helper.DataEncrypterDecrypter.*;
import static com.example.demo.helper.FHIRJSON.prepareFHIRJSONString;
import static com.example.demo.helper.Service.DataTransferServiceHelper.*;
import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;
import static com.example.demo.utility.TokenUtil.getAccessToken;


@Service
public class DataTransferService {
    Logger logger = LoggerFactory.getLogger(DataTransferService.class);
    @Autowired
    ConsentRepository consentRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    VisitRepository visitRepository;
    @Autowired
    CareContextRepository careContextRepository;
    public String[] saveHIPNotifyConsent(JSONObject notification) {
        logger.info("eneteing saveHIPNotifyConsent with data: " + notification);
        Consent consent = new Consent();
        consent.setRequestId(notification.getString("requestId"));
        notification = notification.getJSONObject("notification");

        consent.setStatus(notification.getString("status"));
        consent.setConsentId(notification.getString("consentId"));

        notification = notification.getJSONObject("consentDetail");

        JSONArray arr = notification.getJSONArray("careContexts");
        for (int i = 0 ; i < arr.length(); i++) {
            JSONObject obj = (JSONObject) arr.get(i);
            Visit visit = visitRepository.findVisitByReferenceNumber(obj.getString("careContextReference"));
            System.out.println("care context not found " + obj.getString("careContextReference"));
            if (visit == null)  continue;
            CareContext careContext = convertVisitIntoCareContext(visit);
            careContextRepository.save(careContext);
            consent.addCareContext(careContext);
        }
        consent.setHiTypes(notification.getJSONArray("hiTypes").toString());
        consent.setAccessMode(notification.getJSONObject("permission").getString("accessMode"));
        consent.setPatientReferenceWhenSendingData(notification.getJSONObject("patient").getString("id"));
        consentRepository.save(consent);
        logger.info("exiting saveHIPNotifyConsent after saving consent: " + consent);
        return new String[]{consent.getConsentId(), consent.getRequestId()};
    }

    public void fireABDMOnNotify(String[] ids) {
        logger.info("entering fireABDMOnNotify with data: "  + ids.toString());
        JSONObject request = prepareOnNotifyRequestObject(ids);
        HttpHeaders headers = prepareHeader(getAccessToken());

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
        restTemplate.postForObject(APIList.HIP_REQUEST_ON_NOTIFY, entity, String.class);
    }

    public void fireABDMRequestAcknowledgement(JSONObject requestObj) {
        logger.info("entering fireABDMRequestAcknowledgement with data: " + requestObj);
        String txnId = requestObj.getString("transactionId");
        String requestId = requestObj.getString("requestId");

        JSONObject responseObj = prepareRequestAcknowledgementRequest(txnId, requestId);
        HttpHeaders headers = prepareHeader(getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(responseObj.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(APIList.HIP_REQUEST_ON_REQUEST, entity, String.class);
    }

    public JSONObject prepareAndSendData(JSONObject requestObj) {
        logger.info("entering prepareAndSendData with data: " + requestObj);
        Consent consent = consentRepository.findConsentByConsentId(requestObj.getJSONObject("hiRequest").getJSONObject("consent").getString("id"));
        JSONObject object = prepareDataToTransfer(consent, requestObj);
        String dataPushUrl = requestObj.getJSONObject("hiRequest").getString("dataPushUrl");

        HttpHeaders headers = prepareHeader(getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(object.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(dataPushUrl, entity, String.class);
        return object;
    }
    public void sendDataTransferCompletedNotification(JSONObject object, JSONObject requestObj) {
        logger.info("entering sendDataTransferCompletedNotification with data: ");
        logger.info("object : " + object.toString());
        logger.info("requestbObj: "+ requestObj);
        Consent consent = consentRepository.findConsentByConsentId(requestObj.getJSONObject("hiRequest").getJSONObject("consent").getString("id"));
        JSONObject obj = prepareDeliveredNotification(object, requestObj, consent);
        HttpHeaders headers = prepareHeader(getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(obj.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(APIList.HIP_ON_DATA_TRANSFER_COMPLETE_NOTIFY, entity, String.class);
    }
}
