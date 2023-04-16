package com.example.demo.common;

import com.example.demo.constants.StringConstants;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ResponseHelper {
    public static String serverSideError(String e) {
        JSONObject obj = new JSONObject();
        obj.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
        obj.put(StringConstants.MSG, "Error occurred. " + e);
        return obj.toString();
    }

    public static HttpHeaders prepareHeader(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        headers.set("X-CM-ID", "sbx");
        return headers;
    }
}
