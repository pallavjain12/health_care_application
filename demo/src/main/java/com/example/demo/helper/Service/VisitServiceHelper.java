package com.example.demo.helper.Service;

import com.example.demo.model.Visit;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;

public class VisitServiceHelper {
    public static JSONObject prepareAddContextRequest(String patientAuthToken, Visit visit, String patientId, String patientName) {
        JSONObject request = new JSONObject();
        request.put("requestId", getRandomUUID());
        request.put("timestamp", getTimeStamp());

        JSONObject link = new JSONObject();
        link.put("accessToken", patientAuthToken);

        JSONObject pateint = new JSONObject();
        pateint.put("referenceNumber", patientId);
        pateint.put("display", patientName);

        JSONObject careContext = new JSONObject();
        careContext.put("referenceNumber", visit.getReferenceNumber());
        careContext.put("display", visit.getDisplay());
        link.put("patient", pateint);
        pateint.put("careContexts", careContext);
        request.put("link", link);
        return request;
    }

    public static HttpHeaders prepareAddCareContextHeaders(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        headers.set("X-CM-ID", "sbx");
        return headers;
    }
}
