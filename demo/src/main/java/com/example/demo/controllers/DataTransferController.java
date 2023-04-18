package com.example.demo.controllers;

import com.example.demo.service.DataTransferService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class DataTransferController {
    Logger logger = LoggerFactory.getLogger(DataTransferController.class);
    @Autowired
    DataTransferService dataTransferService;
    @PostMapping("/v0.5/consents/hip/notify")
    @CrossOrigin
    public void hipNotify(@RequestBody String str) {
        JSONObject requestBody = new JSONObject(str);
        logger.info("Entering hip notify with data: " + requestBody);
        String[] ids = dataTransferService.saveHIPNotifyConsent(requestBody);
        dataTransferService.fireABDMOnNotify(ids);
        logger.info("Exiting hip notify");
    }

    @PostMapping("/v0.5/health-information/hip/request")
    @CrossOrigin
    public void dataTransferRequest(@RequestBody String str) {
        JSONObject requestObj = new JSONObject(str);
        logger.info("Entering hip data transfer request with data: " + requestObj.toString());
        dataTransferService.fireABDMRequestAcknowledgement(requestObj);
        JSONObject obj = dataTransferService.prepareAndSendData(requestObj);
        logger.info("sent data to consent request: " + obj.toString());
        dataTransferService.sendDataTransferCompletedNotification(obj, requestObj);
        logger.info("Exiting dataTransferRequest");
    }
}
