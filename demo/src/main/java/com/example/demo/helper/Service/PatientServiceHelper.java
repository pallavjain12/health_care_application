package com.example.demo.helper.Service;

import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;

public class PatientServiceHelper {
    public static JSONObject prepareGenerateOTPEntity(String abhaId) {
        JSONObject request = new JSONObject();
        request.put("requestId", getRandomUUID());
        request.put("timestamp", getTimeStamp());
            JSONObject query = new JSONObject();
            query.put("id", abhaId);
            query.put("purpose", "KYC_AND_LINK");
            query.put("authMode", "MOBILE_OTP");
                JSONObject requester = new JSONObject();
                requester.put("type", "HIP");
                requester.put("id", "team-29-hip-1");
            query.put("requester", requester);
        request.put("query", query);
        return request;
    }

    public static HttpHeaders prepareGenerateOTPHeader(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        headers.set("X-CM-ID", "sbx");
        return headers;
    }

    public static JSONObject prepareConfirmOTPRequest (String txnId, String otp) {
        JSONObject request = new JSONObject();
        request.put("requestId", getRandomUUID());
        request.put("timestamp", getTimeStamp());
        request.put("transactionId", txnId);

        JSONObject credential = new JSONObject();
        credential.put("authCode", otp);

        request.put("credential", credential);

        return request;
    }

    public static HttpHeaders prepareConfirmOTPHeader(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        headers.set("X-CM-ID", "sbx");
        return headers;
    }
}
