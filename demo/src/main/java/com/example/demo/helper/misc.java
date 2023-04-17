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

    public static String convertDateTOZonedUTC(String date) {
        return ZonedDateTime.of(LocalDateTime.parse(date + "T00:00:00.00000001"), ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
    }

    public static void main(String[] args) {
        String date = "1998-06-15T00:00:00.00000001";
        System.out.println(ZonedDateTime.of(LocalDateTime.parse(date), ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
    }
}
