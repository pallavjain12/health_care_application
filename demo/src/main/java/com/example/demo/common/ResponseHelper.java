package com.example.demo.common;

import com.example.demo.constants.StringConstants;
import com.example.demo.service.EmployeeService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ResponseHelper {
    static Logger logger = LoggerFactory.getLogger(ResponseHelper.class);
    public static JSONObject serverSideError(String e) {
        logger.info("Inside serverside error with string: " + e);
        JSONObject obj = new JSONObject();
        obj.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
        obj.put(StringConstants.MSG, "Error occurred. " + e);
        logger.info("Exiting serverSide error with String: " + obj.toString());
        return obj;
    }

    public static HttpHeaders prepareHeader(String authToken) {
        logger.info("Entering prepareHeader()");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        headers.set("X-CM-ID", "sbx");
        logger.info("Exiting prepareHeader() with headers: " + headers.toString());
        return headers;
    }
}
