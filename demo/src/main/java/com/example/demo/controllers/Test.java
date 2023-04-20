package com.example.demo.controllers;

import com.example.demo.model.ConsentRequest;
import com.example.demo.model.Patient;
import com.example.demo.model.Visit;
import com.example.demo.repository.ConsentRequestRepository;
import com.example.demo.repository.PatientRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
public class Test {
    public List<SseEmitter> list = new ArrayList<>();
    private static HashMap<String, SseEmitter> emittersMap = new HashMap<>();

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    ConsentRequestRepository consentRequestRepository;
    @GetMapping("/ssetest")
    @CrossOrigin
    public SseEmitter test(@RequestParam("abhaId") String abhaId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            sseEmitter.send(SseEmitter.event().name("connectionSuccessfull"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        emittersMap.put(abhaId, sseEmitter);
        return sseEmitter;
    }

    @GetMapping("/create-event")
    @CrossOrigin
    public void createEvent(@RequestBody HashMap<String, String> mapp) {
        System.out.println("request = " + mapp + "emitter = " + emittersMap);
        SseEmitter emitter = emittersMap.get(mapp.get("abhaId"));
        try {
            emitter.send(SseEmitter.event().name("successfull").data(mapp));
        }
        catch (Exception e) {
            System.out.println(e);
            emittersMap.remove(mapp.get("abhaId"));
        }
    }
    @PostMapping("/check1")
    public String check1(@RequestBody String str) {
        List<Visit> list = patientRepository.findPatientById(302).getVisits();
        ConsentRequest con = consentRequestRepository.findConsentRequestByRequestId("1eaef9c0-f033-4b1f-bfdd-6e9736b58115");
        JSONObject object = new JSONObject();
        object.put("obj", con);
//        object.put("request", )
//        for (int i = 0; i < list.size(); i++) {
//            object.put(list.get(i).getJSONObject());
//        }
//        return object.toString();
        return "";
    }
}




















