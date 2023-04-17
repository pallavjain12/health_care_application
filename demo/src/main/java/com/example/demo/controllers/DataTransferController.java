package com.example.demo.controllers;

import com.example.demo.service.DataTransferService;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin
public class DataTransferController {
    DataTransferService dataTransferService;
    @PostMapping("/v0.5/hip/notify")
    public void hipNotify(@RequestBody JSONObject requestBody) {
        String[] ids = dataTransferService.saveHIPNotifyConsent(requestBody);
        dataTransferService.fireABDMOnNotify(ids);
    }

    @PostMapping("/v0.5/health-information/hip/request")
    public void dataTransferRequest(@RequestBody JSONObject requestObj) {
        dataTransferService.fireABDMRequestAcknowledgement(requestObj);
        JSONObject obj = dataTransferService.prepareAndSendData(requestObj);
        dataTransferService.sendDataTransferCompletedNotification(obj, requestObj);
    }
}
