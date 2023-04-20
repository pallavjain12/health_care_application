package com.example.demo.controllers;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import com.example.demo.service.VisitService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@RestController
@CrossOrigin
public class VisitController {
    Logger logger = LoggerFactory.getLogger(VisitController.class);
    @Autowired
    PatientRepository patientRepository;

    @Autowired
    VisitService visitService;

    private static HashMap<String, SseEmitter> map = new HashMap<>();
    @PostMapping("/add-visit")
    @CrossOrigin
    public SseEmitter addNewVisit(@RequestBody String request) {
        JSONObject obj = new JSONObject(request);
        String patient_id = obj.getString("patientId");
        String patientAuthToken = obj.getString("accessToken");
        logger.info("Entering addNewVisitClass with data: patientId - " + patient_id + " authToken: " + patientAuthToken);
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Patient patient = patientRepository.findPatientById(Long.parseLong(patient_id));
        String requestId = visitService.addCareContext(patient, patientAuthToken);

        /*
            TODO: Do something about this
         */
        if (requestId == null)    throw new RuntimeException("unable to send request");

        try {
            emitter.send(SseEmitter.event().name("add-visit"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        map.put(requestId, emitter);
        return emitter;
    }

    @PostMapping("/v0.5/links/link/on-add-contexts")
    @CrossOrigin
    public void onAddNewVisit(@RequestBody String response) {
        logger.info("Entering addNewVisit with data: " + response);
        String[] respond = visitService.createOnAddContextResponse(response);
        SseEmitter emitter = map.get(respond[0]);

        try {
            emitter.send(SseEmitter.event().name("on-init").data(respond[1]));
            emitter.complete();
            map.remove(respond[0]);
        }
        catch (Exception e) {
            logger.error("error occurred: " + e);
            emitter.complete();
            map.remove(respond[0]);
        }
    }

    @PatchMapping("/update-visit")
    public String updateVisit(@RequestBody String req) {
        logger.info("entering update visit with req: " + req);
        return visitService.updatePrescription(req).toString();
    }

    @GetMapping("/visit")
    public String getVisit(@RequestBody String req) {
        return visitService.getVisitById(req);
    }
}
