package com.example.demo.controllers;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import com.example.demo.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;

@RestController
@CrossOrigin
public class VisitController {
    @Autowired
    PatientRepository patientRepository;

    @Autowired
    VisitService visitService;

    private static HashMap<String, SseEmitter> map = new HashMap<>();
    @PostMapping("/add-visit")
    @CrossOrigin
    public SseEmitter addNewVisit(@RequestParam("patient_id") Long patient_id, @RequestParam("authToken") String patientAuthToken) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Patient patient = patientRepository.findPatientById(patient_id);
        String requestId = visitService.addCareContext(patient, patientAuthToken);

        /*
            TODO: Do something about this
         */
        if (requestId == null)    throw new RuntimeException("unable to send request");

        try {
            emitter.send(SseEmitter.event().name("connectionSuccessfull"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        map.put(requestId, emitter);
        return emitter;
    }

    @PostMapping("/v0.5/links/link/on-add-contexts")
    @CrossOrigin
    public void onAddNewVisit(@RequestBody String response) {
        String[] respond = visitService.createOnAddContextResponse(response);
        SseEmitter emitter = map.get(respond[0]);

        try {
            emitter.send(SseEmitter.event().name("on-init").data(respond[1]));
            map.remove(respond[0]);
        }
        catch (Exception e) {
            System.out.println(e);
            map.remove(respond[0]);
        }
    }
}
