package com.example.demo.controllers;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscoveryController {
    @PostMapping("/v0.5/care-contexts/discover")
    @CrossOrigin
    public void discover(@RequestBody String request) {
//        {
//            "requestId": "499a5a4a-7dda-4f20-9b67-e24589627061",
//            "timestamp": "2023-04-13T16:53:52.530Z",
//            "transactionId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
//            "patient": {
//                "id": "<patient-id>@<consent-manager-id>",
//                "verifiedIdentifiers": [
//                    {
//                        "type": "MOBILE",
//                        "value": "+919800083232"
//                    }
//                ],
//                "unverifiedIdentifiers": [
//                    {
//                        "type": "MR",
//                            "value": "+919800083232"
//                    }
//                ],
//                "name": "chandler bing",
//                "gender": "M",
//                "yearOfBirth": 2000
//            }
//        }
        JSONObject obj = new JSONObject(request);
        JSONObject patient = obj.getJSONObject("patient");
        String transactionId = obj.get("transactionId").toString();
        String requestId = obj.getString("requestId");

    }
}
