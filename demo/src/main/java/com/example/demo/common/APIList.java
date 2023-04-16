package com.example.demo.common;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.print.attribute.standard.JobKOctets;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class APIList {
    public static final String FETCH_AUTH_TOKEN = "https://dev.abdm.gov.in/gateway/v0.5/sessions";
    public static final String REGISTER_HRP_HOST = "https://dev.abdm.gov.in/devservice/v1/bridges";
    public static final String ADD_CARE_CONTEXT = "https://dev.abdm.gov.in/gateway/v0.5/links/link/add-contexts";
    public static final String AUTH_CONFIRM = "https://dev.abdm.gov.in/gateway/v0.5/users/auth/confirm";
    public static final String AUTH_INIT = "https://dev.abdm.gov.in/gateway/v0.5/users/auth/init";
    public static final String CARE_CONTEXT_INIT = "https://dev.abdm.gov.in/gateway/v0.5/consent-requests/init";
    public static final String CARE_CONTEXT_FETCH = "https://dev.abdm.gov.in/gateway/v0.5/consents/fetch";
    public static final String HEALTH_DATA_REQUEST = "https://dev.abdm.gov.in/gateway/v0.5/health-information/cm/request";

    public static void main(String[] args) {
        JSONObject o = new JSONObject("{\n" +
                "  \"requestId\": \"18305fa9-03e0-45a6-ac0d-f870e0e3b116\",\n" +
                "  \"timestamp\": \"2023-03-26T05:52:20.054624235\",\n" +
                "  \"auth\": {\n" +
                "    \"accessToken\": \"eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJwYWxsYXZqYWluQHNieCIsInJlcXVlc3RlclR5cGUiOiJISVAiLCJyZXF1ZXN0ZXJJZCI6InRlYW0tMjktaGlwLTEiLCJwYXRpZW50SWQiOiJwYWxsYXZqYWluQHNieCIsInNlc3Npb25JZCI6IjVhZjFiMjg4LThlMjUtNDk1OS1hNmY1LTBmMzMxMzc5NDlhMiIsImV4cCI6MTY3OTg5NjMzOSwiaWF0IjoxNjc5ODA5OTM5fQ.MhKXcVGwNGNqRl8N3ehU1gIptf4jgBHpy-0L3LCDRx0LADJgADVeCghn5lp6Kyw02b1bUBodpNGGOzgg6Muws1UFVwdNMZqCPJ6NN3APjahraQMu8dNRFvVC-nvY4vKETczefKi-RHnmLPZkb7w9UeTen5s2-1rs5vjnm7IxRAlpzntV1j-mhQd-LtL3xG1JvhXVySbAh9HWVKKgfe0GDS5mIO8RorO1wah_DZg4pO7YVPtNSW5U8TKWCXMQfzx2eG6fKKCfPZiTkBIS5NxH1wJcLCIQigXyS6hH67hGvNSsow5yct7IC6jjG2ukIBNSfxtPj2VOiKt4uZfjugVpmg\",\n" +
                "    \"patient\": {\n" +
                "      \"id\": \"pallavjain@sbx\",\n" +
                "      \"name\": \"Pallav Jain\",\n" +
                "      \"gender\": \"M\",\n" +
                "      \"yearOfBirth\": 1998,\n" +
                "      \"monthOfBirth\": 6,\n" +
                "      \"dayOfBirth\": 15,\n" +
                "      \"address\": {\n" +
                "        \"line\": null,\n" +
                "        \"district\": \"DATIA\",\n" +
                "        \"state\": \"MADHYA PRADESH\",\n" +
                "        \"pincode\": null\n" +
                "      },\n" +
                "      \"identifiers\": [\n" +
                "        {\n" +
                "          \"type\": \"MOBILE\",\n" +
                "          \"value\": \"8109629687\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"type\": \"HEALTH_NUMBER\",\n" +
                "          \"value\": null\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"error\": null,\n" +
                "  \"resp\": {\n" +
                "    \"requestId\": \"7a160772-9793-404d-b013-b28be537cd5c\"\n" +
                "  }\n" +
                "}");

//        JSONObject t = o.getJSONObject("auth");
//        JSONObject p = t.getJSONObject("patient");
//        JSONArray i = p.getJSONArray("identifiers");
//        JSONObject tt = i.getJSONObject(1);
//        for (int j = 0; j < i.length(); j++) {
//            JSONObject temp = i.getJSONObject(j);
//            for (Object e : temp.keySet()) {
//                if ( !temp.isNull(e.toString())) System.out.println(temp.getString(e.toString()));
//            }
//        }
        System.out.println(LocalDate.now());
    }
}
