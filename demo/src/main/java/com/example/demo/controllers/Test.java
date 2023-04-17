package com.example.demo.controllers;

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
}




















