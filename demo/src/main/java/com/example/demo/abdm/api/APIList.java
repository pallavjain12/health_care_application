package com.example.demo.abdm.api;

import org.json.JSONObject;

public class APIList {
    public static final String FETCH_AUTH_TOKEN = "https://dev.abdm.gov.in/gateway/v0.5/sessions";
    public static final String REGISTER_HRP_HOST = "https://dev.abdm.gov.in/devservice/v1/bridges";
    public static final String ADD_CARE_CONTEXT = "https://dev.abdm.gov.in/gateway/v0.5/links/link/add-contexts";
    public static final String AUTH_CONFIRM = "https://dev.abdm.gov.in/gateway/v0.5/users/auth/confirm";
    public static final String AUTH_INIT = "https://dev.abdm.gov.in/gateway/v0.5/users/auth/init";

    public static void main(String[] args) {
        JSONObject o = new JSONObject("{\n" +
                "  \"requestId\": \"6149b299-c520-49d7-8de8-11570ebf8604\",\n" +
                "  \"timestamp\": \"2023-04-13T10:13:46.000125032\",\n" +
                "  \"auth\": {\n" +
                "    \"transactionId\": \"14a77617-bec1-41bd-a41d-9515c9f31640\",\n" +
                "    \"mode\": \"MOBILE_OTP\",\n" +
                "    \"meta\": {\n" +
                "      \"hint\": null,\n" +
                "      \"expiry\": \"2023-04-13T12:13:46.000137008\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"error\": null,\n" +
                "  \"resp\": {\n" +
                "    \"requestId\": \"94536578-5cd1-451b-bc88-a756d349c2f7\"\n" +
                "  }\n" +
                "}");
        System.out.println(o.get("error").toString().equals("null"));
    }
}
