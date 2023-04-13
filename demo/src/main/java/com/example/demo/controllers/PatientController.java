package com.example.demo.controllers;

import com.example.demo.constants.StringConstants;
import com.example.demo.service.PatientService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;

@RestController
public class PatientController {
    @Autowired
    PatientService patientService;
    private static HashMap<String, SseEmitter> emittersMap = new HashMap<>();

    @PostMapping("/generate-otp")
    SseEmitter generateOTP(@RequestBody HashMap<String, String> requestParams) {
        String abhaId = requestParams.get(StringConstants.ABHAID);
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name("connectionSuccessfull"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String reqId = patientService.fireABDMGenerateOTP(abhaId);

        /*
            TODO:
                Do something about this.
                if session token not received, code do not know what to do.
         */
        if (reqId == null) {
            throw new RuntimeException();
        }
        emittersMap.put(reqId, sseEmitter);
        return sseEmitter;
    }

    @PostMapping("/v0.5/users/auth/on-init")
    public void onGenerateOTP(@RequestBody String response) {
        String[] respond = patientService.prepareOnGenerateResponse(response);
        SseEmitter emitter = emittersMap.get(respond[0]);
        try {
            emitter.send(SseEmitter.event().name("on-init").data(respond[1]));
            emittersMap.remove(respond[0]);
        }
        catch (Exception e) {
            System.out.println(e);
            emittersMap.remove(respond[0]);
        }
    }

    @PostMapping("/confirm-otp")
    public SseEmitter confirmOTP(@RequestBody HashMap<String, String> reuest) {
        String transactionId = reuest.get("transactionId");
        String otp = reuest.get("otp");

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
        String[] respond = patientService.prepareOnConfirmOTPResponse(response);
        SseEmitter emitter = emittersMap.get(respond[0]);
        try {
            emitter.send(SseEmitter.event().name("on-confirm").data(respond[1]));
            emittersMap.remove(respond[0]);
        }
        catch (Exception e) {
            System.out.println(e);
            emittersMap.remove(respond[0]);
        }
    }

    @PostMapping("/v0.5/users/auth/on-fetch-modes")
    void onFetchModes(@RequestBody String response) {

    }
}
