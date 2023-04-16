package com.example.demo.controllers;

import com.example.demo.constants.StringConstants;
import com.example.demo.model.Consent;
import com.example.demo.model.ConsentRequest;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.service.ConsentRequestService;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;

public class ConsentRequestController {
    ConsentRequestService consentRequestService;
    HashMap<String, SseEmitter> map = new HashMap<>();

    @PostMapping("/create-consent-request")
    @CrossOrigin
    public SseEmitter consentRequestInit(@RequestBody String req) {
        ConsentRequest consentRequest = consentRequestService.prepareConsentRequest(req);
        if (consentRequest == null) throw new RuntimeException();
        String requestId = consentRequestService.fireABDMConsentRequestInit(consentRequest);

        SseEmitter sseEmitter = new SseEmitter();
        try {
            sseEmitter.send(SseEmitter.event().name("consent-request-init"));
        }
        catch (Exception e) {
            System.out.println(e);
        }
        map.put(requestId, sseEmitter);
        return sseEmitter;
    }

    @PostMapping("/v0.5/consent-requests/on-init")
    @CrossOrigin
    public void onConsentRequestInit(@RequestBody String responseBody) {
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
            System.out.println(e);
            sseEmitter.complete();
            map.remove(response[0]);
        }
    }

    @PostMapping("/v0.5/consents/hiu/notify")
    @CrossOrigin
    public void hiuConsentNotify(@RequestBody JSONObject requestBody) {
        consentRequestService.updateConsentRequestStatus(requestBody);
        consentRequestService.fireArtifactsFetchRequest(requestBody.getJSONObject("notification").getJSONArray("consentArtefacts"));
    }

    @PostMapping("/v0.5/consents/on-fetch")
    @CrossOrigin
    public void onFetch(@RequestBody JSONObject requestBody) {
        Consent consent = consentRequestService.updateConsentRequestAfterOnFetch(requestBody);
        consentRequestService.fireABDMHealthInformationCMRequest(consent);
    }

    @PostMapping("/v0.5/health-information/cm/on-request")
    public void onRequest(@RequestBody JSONObject requestBody) {
        consentRequestService.updateConsentTransactionId(requestBody);
    }

    @PostMapping("/data/push")
    public void dataPush(@RequestBody JSONObject data) {
        consentRequestService.saveData(data);
    }

}
