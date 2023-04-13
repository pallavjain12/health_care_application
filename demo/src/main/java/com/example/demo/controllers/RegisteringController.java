package com.example.demo.controllers;

import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import com.example.demo.utility.SessionUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.demo.helper.misc.addCareContext;

@RestController
@CrossOrigin
public class RegisteringController {

    @Autowired
    PatientRepository patientRepository;
    int gotResponse = 0;
    int gotOTPResponse = 0;
    String webHookResponse = null;
    String webHookOTPResponse = null;
    @RequestMapping("/generate_otp")
    @CrossOrigin
    public String generateOtp(@RequestParam String hid) throws InterruptedException {
        return "use new api";
        //        gotResponse = 0;
//        String timestamp = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );
//
//
//        String generateOTPURL = "https://dev.abdm.gov.in/gateway/v0.5/users/auth/init";
//        String authToken = SessionUtil.getAccessToken();
//        if (authToken.equals("-1")) return "ABDM not reachable";
//
//        RestTemplate restTemplate = new RestTemplate();
//
//        JSONObject request = new JSONObject();
//        request.put("requestId", java.util.UUID.randomUUID());
//        request.put("timestamp", timestamp);
//            JSONObject query = new JSONObject();
//            query.put("id", hid);
//            query.put("purpose", "KYC_AND_LINK");
//            query.put("authMode", "MOBILE_OTP");
//                JSONObject requester = new JSONObject();
//                requester.put("type", "HIP");
//                requester.put("id", "team-29-hip-1");
//            query.put("requester", requester);
//        request.put("query", query);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(authToken);
//        headers.set("X-CM-ID", "sbx");
//        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
//        restTemplate.postForObject(generateOTPURL, entity, String.class);
////        while(gotResponse == 0);
//        System.out.println("webHookresponse in generate_otp = \n"+webHookResponse);
//
//        JSONObject responseFromWebhook = new JSONObject(webHookResponse);
//        System.out.println(responseFromWebhook);
//        JSONObject auth = (JSONObject) responseFromWebhook.get("auth");
//        return (String)auth.get("transactionId");
    }

    @PostMapping("/v0.5/users/auth/on-init")
    public void postGenerateOTP(@RequestBody String responseBody) throws ParseException {
        webHookResponse = responseBody;
        gotResponse = 1;
    }

    @PostMapping("/v0.5/users/auth/on-confirm")
    public void postConfirmOTP(@RequestBody String responseBody) {
        webHookOTPResponse = responseBody;
        gotOTPResponse = 1;
    }
    @RequestMapping("/confirm_otp")
    public String confirmOtp(@RequestParam String otp, @RequestParam String txnId) {
        gotOTPResponse = 0;
        webHookOTPResponse = null;
        String timestamp = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );

        String confirmOTPURL = "https://dev.abdm.gov.in/gateway/v0.5/users/auth/confirm";
        String authToken = SessionUtil.getAccessToken();
        if (authToken.equals("-1")) return "ABDM not reachable";

        RestTemplate restTemplate = new RestTemplate();
        JSONObject request = new JSONObject();
        request.put("requestId", java.util.UUID.randomUUID());
        request.put("timestamp", timestamp);
        request.put("transactionId", txnId);

        JSONObject credential = new JSONObject();
        credential.put("authCode", otp);

        request.put("credential", credential);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        headers.set("X-CM-ID", "sbx");
        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

        restTemplate.postForObject(confirmOTPURL, entity, String.class);
        while(gotOTPResponse == 0);

        JSONObject responseFromWebhook = new JSONObject(webHookOTPResponse);
        JSONObject response = new JSONObject();

        JSONObject auth = (JSONObject) responseFromWebhook.get("auth");
        JSONObject patient = (JSONObject) auth.get("patient");

        response.put("name", patient.get("name"));
        response.put("id", patient.get("id"));
        response.put("gender", patient.get("gender"));
        response.put("dayOfBirth", patient.get("dayOfBirth"));
        response.put("monthOfBirth", patient.get("monthOfBirth"));
        response.put("yearOfBirth", patient.get("yearOfBirth"));

        response.put("address", patient.get("address"));
        response.put("identifiers", patient.get("identifiers"));

        Patient tempPatient = new Patient();

        tempPatient.setGender(patient.get("gender").toString());
        tempPatient.setMonthOfBirth(Integer.parseInt(patient.get("monthOfBirth").toString()));
        tempPatient.setYearOfBirth(Integer.parseInt(patient.get("yearOfBirth").toString()));
        tempPatient.setName(patient.get("name").toString());

        Patient saved = patientRepository.save(tempPatient);

        addCareContext(auth.get("accessToken").toString(), patient.get("name").toString(), "" + saved.getId());

        return response.toString();
    }

    @PostMapping("/v0.5/links/link/on-add-contexts")
    public void onAddCareContexts(@RequestBody String response) {
        System.out.println(response);
        System.out.flush();
    }
}
