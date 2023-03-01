package com.example.demo.utility;

import org.apache.coyote.Request;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class SessionUtil {
    static final String fetchAuthToken = "https://dev.abdm.gov.in/gateway/v0.5/sessions";
    static final String registerHRPHost = "https://dev.abdm.gov.in/devservice/v1/bridges";
    static final String search = "{\"clientId\": \"SBX_002732\",\"clientSecret\": \"d119eb33-c336-44fe-ad53-7335712f5b04\"}";
    public static String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject request = new JSONObject();
        request.put("clientId", "SBX_002732");
        request.put("clientSecret", "d119eb33-c336-44fe-ad53-7335712f5b04");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
        ResponseEntity<String> loginResponse = restTemplate.exchange(fetchAuthToken, HttpMethod.POST, entity, String.class);

        if (loginResponse.getStatusCode() == HttpStatus.OK) {
                JSONObject responseObject = new JSONObject(loginResponse.getBody());
                String authToken = responseObject.getString("accessToken");

                JSONObject secondObject = new JSONObject();
                secondObject.put("url", "\"https://webhook.site/dd78a1e1-49f9-4c81-a425-767e1d408132\"");
                headers = new HttpHeaders();
                headers.setBearerAuth(authToken);
                entity = new HttpEntity<String>(request.toString(), headers);
                return authToken;
        }
        else {
            return "-1";
        }
    }
}
