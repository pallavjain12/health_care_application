package com.example.demo.controllers;

import com.example.demo.model.Consent;
import com.example.demo.model.ConsentRequest;
import com.example.demo.service.EmployeeService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.ConsentRequestService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;

@RestController
public class ConsentRequestController {
    Logger logger = LoggerFactory.getLogger(ConsentRequestController.class);
    @Autowired
    ConsentRequestService consentRequestService;
    HashMap<String, SseEmitter> map = new HashMap<>();

    @GetMapping("/create-consent-request")
    @CrossOrigin
    public SseEmitter consentRequestInit(@RequestParam("purpose") String purpose,
                                            @RequestParam("dateFrom") String dateFrom,
                                            @RequestParam("dateTo") String dateTo,
                                            @RequestParam("dateEraseAt") String dateEraseAt,
                                            @RequestParam("hiTypes") String hiTypes,
                                            @RequestParam("patientId") String patientId,
                                            @RequestParam("doctorId") String doctorId,
                                            @RequestParam("visitId") String visitId) {

        logger.info("Entering /create-consent-request with requestBody: ");
        logger.info("currently emitter map is " + map);
        JSONObject req = new JSONObject();
        req.put("purpose", purpose);
        req.put("dateFrom", dateFrom);
        req.put("dateTo", dateTo);
        req.put("dateEraseAt", dateEraseAt);
        req.put("hiTypes", "[" + hiTypes + "]");
        req.put("patientId", patientId);
        req.put("doctorId", doctorId);
        req.put("visitId", visitId);
        ConsentRequest consentRequest = consentRequestService.prepareConsentRequest(req.toString());
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
        logger.info("Exiting /create-consent-request class and sending sseEmitter");
        return sseEmitter;
    }

    @PostMapping("/v0.5/consent-requests/on-init")
    @CrossOrigin
    public void onConsentRequestInit(@RequestBody String responseBody) {
        logger.info("Entering /v0.5/consent-requests/on-init with responseBody: " + responseBody);
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
        logger.info("Exiting /v0.5/consent-requests/on-init");
    }

    @PostMapping("/v0.5/consents/hiu/notify")
    @CrossOrigin
    public void hiuConsentNotify(@RequestBody String str) {
        JSONObject requestBody = new JSONObject(str);
        logger.info("Entering /v0.5/consents/hiu/notify with requestBody: " + requestBody);
        boolean consentGranted = consentRequestService.updateConsentRequestStatus(requestBody);
        logger.info("Consent granted = " + consentGranted);
        if (consentGranted) consentRequestService.fireArtifactsFetchRequest(requestBody.getJSONObject("notification").getJSONArray("consentArtefacts"));
        logger.info("Exiting /v0.5/consents/hiu/notify");
    }

    @PostMapping("/v0.5/consents/on-fetch")
    @CrossOrigin
    public void onFetch(@RequestBody String str) {
        JSONObject requestBody = new JSONObject(str);
        logger.info("Entering /v0.5/consents/on-fetch with requestBody: " + requestBody);
        Consent consent = consentRequestService.updateConsentRequestAfterOnFetch(requestBody);
        consentRequestService.fireABDMHealthInformationCMRequest(consent);
        logger.info("Exiting /v0.5/consents/on-fetch");
    }

    @PostMapping("/v0.5/health-information/hiu/on-request")
    @CrossOrigin
    public void onRequest(@RequestBody String str) {
        JSONObject requestBody = new JSONObject(str);
        logger.info("Entering /v0.5/health-information/hiu/on-request with requestBody: " + requestBody);
        consentRequestService.updateConsentTransactionId(requestBody);
        logger.info("Exiting /v0.5/health-information/hiu/on-request");
    }

    @PostMapping("/data/push")
    @CrossOrigin
    public void dataPush(@RequestBody String str) {
        JSONObject data = new JSONObject(str);
        logger.info("Entering /data/push with data: " + data);
        consentRequestService.saveData(data);
        logger.info("exiting /data/pus");
    }

}
