package com.example.demo.helper;

import com.example.demo.utility.SessionUtil;
import org.apache.tomcat.Jar;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class misc {

    public static String getTimeStamp() {
        return ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );
    }

    public static UUID getRandomUUID() {
        return java.util.UUID.randomUUID();
    }
    public static String addCareContext(String patientAuthToken, String name, String id) {
        String timestamp = ZonedDateTime.now( ZoneOffset.UTC ).format( DateTimeFormatter.ISO_INSTANT );
        String addCareContext = "https://dev.abdm.gov.in/gateway/v0.5/links/link/add-contexts";
        String authToken = SessionUtil.getAccessToken();
        if (authToken.equals("-1")) return "ABDM not reachable";

        RestTemplate restTemplate = new RestTemplate();
        JSONObject request = new JSONObject();
        request.put("requestId", java.util.UUID.randomUUID());
        request.put("timestamp", timestamp);

        JSONObject link = new JSONObject();
        link.put("accessToken", patientAuthToken);

        JSONObject pateint = new JSONObject();
        pateint.put("referenceNumber", "PUID-" + id);
        pateint.put("display", name);

        JSONObject careContext = new JSONObject();
        careContext.put("referenceNumber", "visit-" + java.util.UUID.randomUUID());
        careContext.put("display", "Consultation on " + LocalDate.now());
        link.put("patient", pateint);
        pateint.put("careContexts", careContext);
        request.put("link", link);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        headers.set("X-CM-ID", "sbx");
        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);

        HttpEntity<String> response = restTemplate.exchange(addCareContext, HttpMethod.POST, entity, String.class);
        System.out.println(entity.toString());
        System.out.println("misc care context reply -> " + response);
        System.out.flush();
        return "care context added successfull";
    }

    public void convertStringToDateTime(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    }

    public static String convertDateTOZonedUTC(String date) {
        return ZonedDateTime.of(LocalDateTime.parse(date + "T00:00:00.00000001"), ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    public static void main(String[] args) {
        String date = "1998-06-15T00:00:00.00000001";
        System.out.println(ZonedDateTime.of(LocalDateTime.parse(date), ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
    }
}
