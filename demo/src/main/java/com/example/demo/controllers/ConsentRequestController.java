package com.example.demo.controllers;

import com.example.demo.model.Consent;
import com.example.demo.model.ConsentRequest;
import com.example.demo.service.EmployeeService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.service.ConsentRequestService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;

public class ConsentRequestController {
    Logger logger = LoggerFactory.getLogger(ConsentRequestController.class);
    ConsentRequestService consentRequestService;
    HashMap<String, SseEmitter> map = new HashMap<>();

    @PostMapping("/create-consent-request")
    @CrossOrigin
    public SseEmitter consentRequestInit(@RequestBody String req) {
        logger.info("Entering consentRequestInit with requestBody: " + req);
        logger.info("currently emitter map is " + map);
        ConsentRequest consentRequest = consentRequestService.prepareConsentRequest(req);
        logger.info("Prepared consent");
        if (consentRequest == null) throw new RuntimeException();
        String requestId = consentRequestService.fireABDMConsentRequestInit(consentRequest);

        SseEmitter sseEmitter = new SseEmitter();
        try {
            logger.info("sending event name consent-request-init");
            sseEmitter.send(SseEmitter.event().name("consent-request-init"));
            logger.info("sent event name consent-request-init");
        }
        catch (Exception e) {
            logger.error("Exception occurred while sending event: " + e);
        }
        map.put(requestId, sseEmitter);
        logger.info("Exiting consentRequestInit class and sending sseEmitter");
        return sseEmitter;
    }

    @PostMapping("/v0.5/consent-requests/on-init")
    @CrossOrigin
    public void onConsentRequestInit(@RequestBody String responseBody) {
        logger.info("Entering onConsentRequestInit with responseBody: " + responseBody);
        logger.info("currently emitter map is " + map);
        String[] response = consentRequestService.prepareOnConsentRequestInitResponse(responseBody);
        SseEmitter sseEmitter = map.get(response[0]);
        if (response[1] == null) {
            consentRequestService.updateConsentRequestStatusFailed(response[0]);
        }
        else {
            consentRequestService.updateConsentRequestId(response[0], response[1]);
        }
        try {
            sseEmitter.send(SseEmitter.event().name("consent-request-on-init").data(response[2]));
        }
        catch (Exception e) {
            logger.error("Error occurred while sending emitter: " + e);
            sseEmitter.complete();
            map.remove(response[0]);
        }
        logger.info("Exiting onInit");
    }

    @PostMapping("/v0.5/consents/hiu/notify")
    @CrossOrigin
    public void hiuConsentNotify(@RequestBody JSONObject requestBody) {
        logger.info("Entering hiuConsentNotifyClass with requestBody: " + requestBody.toString());
        boolean consentGranted = consentRequestService.updateConsentRequestStatus(requestBody);
        logger.info("Consent granted = " + consentGranted);
        if (consentGranted) consentRequestService.fireArtifactsFetchRequest(requestBody.getJSONObject("notification").getJSONArray("consentArtefacts"));
        logger.info("Exiting hiuConsentNotify");
    }

    @PostMapping("/v0.5/consents/on-fetch")
    @CrossOrigin
    public void onFetch(@RequestBody JSONObject requestBody) {
        logger.info("Entering onFetch method with requestBody: " + requestBody.toString());
        Consent consent = consentRequestService.updateConsentRequestAfterOnFetch(requestBody);
        consentRequestService.fireABDMHealthInformationCMRequest(consent);
        logger.info("Exiting onFetch");
    }

    @PostMapping("/v0.5/health-information/cm/on-request")
    public void onRequest(@RequestBody JSONObject requestBody) {
        logger.info("Entering onRequest method with requestBody: " + requestBody.toString());
        consentRequestService.updateConsentTransactionId(requestBody);
        logger.info("Exiting onRequest");
    }

    @PostMapping("/data/push")
    public void dataPush(@RequestBody JSONObject data) {
        logger.info("Entering /data/push with data: " + data.toString());
        consentRequestService.saveData(data);
        logger.info("exiting /data/pus");
    }

}
