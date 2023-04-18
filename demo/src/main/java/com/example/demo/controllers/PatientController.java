package com.example.demo.controllers;

import com.example.demo.constants.StringConstants;
import com.example.demo.model.Employee;
import com.example.demo.service.PatientService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;

@RestController
public class PatientController {
    Logger logger = LoggerFactory.getLogger(PatientController.class);
    @Autowired
    PatientService patientService;
    private static HashMap<String, SseEmitter> emittersMap = new HashMap<>();

    @PostMapping("/generate-otp")
    SseEmitter generateOTP(@RequestBody HashMap<String, String> requestParams) {
        logger.info("Entering generateOTP with data" + requestParams);
        logger.info("currently map is " + emittersMap);
        String abhaId = requestParams.get(StringConstants.ABHAID);
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        String reqId = patientService.fireABDMGenerateOTP(abhaId);

        if (reqId == null) {
            throw new RuntimeException();
        }

        try {
            sseEmitter.send(SseEmitter.event().name("generate-otp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
            TODO:
                Do something about this.
                if session token not received, code do not know what to do.
         */
        emittersMap.put(reqId, sseEmitter);
        return sseEmitter;
    }

    @PostMapping("/v0.5/users/auth/on-init")
    public void onGenerateOTP(@RequestBody String response) {
        logger.info("Entering onGenerateOTP with data: " + response);
        String[] respond = patientService.prepareOnGenerateResponse(response);
        SseEmitter emitter = emittersMap.get(respond[0]);
        try {
            emitter.send(SseEmitter.event().name("on-init").data(respond[1]));
            emitter.complete();
            emittersMap.remove(respond[0]);
        }
        catch (Exception e) {
            System.out.println(e);
            emitter.complete();
            emittersMap.remove(respond[0]);
        }
    }

    @PostMapping("/confirm-otp")
    public SseEmitter confirmOTP(@RequestBody HashMap<String, String> request) {
        logger.info("Entering confirmOTP with data: " + request);
        String transactionId = request.get("transactionId");
        String otp = request.get("otp");

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("confirm-otp"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String requestId = patientService.fireABDMConfirmOTP(transactionId, otp);

        emittersMap.put(requestId, sseEmitter);
        return sseEmitter;
    }

    @PostMapping("/v0.5/users/auth/on-confirm")
    public void onConfirmOTP(@RequestBody String response) {
        logger.info("Entering onConfirmOTP with data: " + response);
        JSONObject obj = new JSONObject(response);
        String requestId = obj.getJSONObject("resp").get("requestId").toString();
        logger.info("requestId is : " + requestId);
        logger.info("map has currently: " + emittersMap);
        SseEmitter emitter = emittersMap.get(requestId);
        try {
            JSONObject responseObje = patientService.prepareOnConfirmOTPResponse(response);
            logger.info("received this object from prepareOnconfirmOTPresponse: " + responseObje);
            emitter.send(SseEmitter.event().name("on-confirm").data(responseObje.toString()));
            logger.info("sent data to client");
            emitter.complete();
            emittersMap.remove(requestId);
            logger.info("new map state is : " + emittersMap);
        }
        catch (Exception e) {
            logger.error("Error occurred while in sending data: " + e);
            emitter.complete();
            emittersMap.remove(requestId);
        }
    }

    @PostMapping("/v0.5/users/auth/on-fetch-modes")
    void onFetchModes(@RequestBody String response) {

    }
}
