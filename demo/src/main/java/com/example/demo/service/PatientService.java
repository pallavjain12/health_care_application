package com.example.demo.service;

import com.example.demo.utility.TokenUtil;
import com.example.demo.common.APIList;
import com.example.demo.constants.StringConstants;
import com.example.demo.helper.Service.PatientServiceHelper;
import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.helper.Service.PatientServiceHelper.createNewPatient;


@Service
public class PatientService {
    @Autowired
    PatientRepository patientRepository;

    public String fireABDMGenerateOTP(String abhaId) {

        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();
        JSONObject request = PatientServiceHelper.prepareGenerateOTPEntity(abhaId);
        HttpHeaders headers = PatientServiceHelper.prepareGenerateOTPHeader(authToken);

        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
        restTemplate.postForObject(APIList.AUTH_INIT, entity, String.class);
        return request.get("requestId").toString();
    }

    public String[] prepareOnGenerateResponse(String response) {
        String[] ans = new String[2];

        JSONObject obj = new JSONObject(response);
        JSONObject respond = new JSONObject();

        if (!obj.isNull("error")) {
            respond.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
            respond.put(StringConstants.MSG, obj.getJSONObject("error").get("message").toString());
        }
        else {
            JSONObject auth = obj.getJSONObject("auth");
            respond.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
            respond.put(StringConstants.MSG, "OTP sent Successfully");
            respond.put(StringConstants.DATA, new JSONObject().put("transactionId", auth.getString("transactionId")).toString());
        }

        ans[1] = respond.toString();
        ans[0] = obj.getJSONObject("resp").getString("requestId");

        return ans;
    }

    public String fireABDMConfirmOTP(String transactionId, String OTP) {
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();

        JSONObject request = PatientServiceHelper.prepareConfirmOTPRequest(transactionId, OTP);
        HttpHeaders headers = PatientServiceHelper.prepareConfirmOTPHeader(authToken);

        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
        restTemplate.postForObject(APIList.AUTH_CONFIRM, entity, String.class);

        return request.get("requestId").toString();
    }

    /*
        {
          "requestId": "18305fa9-03e0-45a6-ac0d-f870e0e3b116",
          "timestamp": "2023-03-26T05:52:20.054624235",
          "auth": {
            "accessToken": "eyJhbGciOiJSUzUxMiJ9.eyJzdWIiOiJwYWxsYXZqYWluQHNieCIsInJlcXVlc3RlclR5cGUiOiJISVAiLCJyZXF1ZXN0ZXJJZCI6InRlYW0tMjktaGlwLTEiLCJwYXRpZW50SWQiOiJwYWxsYXZqYWluQHNieCIsInNlc3Npb25JZCI6IjVhZjFiMjg4LThlMjUtNDk1OS1hNmY1LTBmMzMxMzc5NDlhMiIsImV4cCI6MTY3OTg5NjMzOSwiaWF0IjoxNjc5ODA5OTM5fQ.MhKXcVGwNGNqRl8N3ehU1gIptf4jgBHpy-0L3LCDRx0LADJgADVeCghn5lp6Kyw02b1bUBodpNGGOzgg6Muws1UFVwdNMZqCPJ6NN3APjahraQMu8dNRFvVC-nvY4vKETczefKi-RHnmLPZkb7w9UeTen5s2-1rs5vjnm7IxRAlpzntV1j-mhQd-LtL3xG1JvhXVySbAh9HWVKKgfe0GDS5mIO8RorO1wah_DZg4pO7YVPtNSW5U8TKWCXMQfzx2eG6fKKCfPZiTkBIS5NxH1wJcLCIQigXyS6hH67hGvNSsow5yct7IC6jjG2ukIBNSfxtPj2VOiKt4uZfjugVpmg",
            "patient": {
              "id": "pallavjain@sbx",
              "name": "Pallav Jain",
              "gender": "M",
              "yearOfBirth": 1998,
              "monthOfBirth": 6,
              "dayOfBirth": 15,
              "address": {
                "line": null,
                "district": "DATIA",
                "state": "MADHYA PRADESH",
                "pincode": null
              },
              "identifiers": [
                {
                  "type": "MOBILE",
                  "value": "8109629687"
                },
                {
                  "type": "HEALTH_NUMBER",
                  "value": "123-123123-12312"
                }
              ]
            }
          },
          "error": null,
          "resp": {
            "requestId": "7a160772-9793-404d-b013-b28be537cd5c"
          }
        }
     */
    public String[] prepareOnConfirmOTPResponse(String response) {
        String[] ans = new String[2];

        JSONObject obj = new JSONObject(response);
        JSONObject auth = obj.getJSONObject("auth");
        JSONObject resp = obj.getJSONObject("resp");
        JSONObject respond = new JSONObject();

        JSONObject patientObj =  auth.getJSONObject("patient");
        JSONArray identifiersObj = patientObj.getJSONArray("identifiers");
        patientObj.put("mobile", "");
        patientObj.put("abhaNumber", "");
        for (Object identifier : identifiersObj) {
            JSONObject temp = (JSONObject) identifier;
            if (!temp.isNull("value") && temp.getString("type").toString().equals("MOBILE")) patientObj.put("mobile", temp.getString("value"));
            if (!temp.isNull("value") && temp.getString("type").toString().equals("HEALTH_NUMBER")) patientObj.put("abhaNumber", temp.getString("value"));
        }
        Patient newPatient = patientRepository.findPatientByAbhaId(patientObj.getString("id"));
        newPatient = (newPatient != null) ? newPatient : patientRepository.save(createNewPatient(patientObj));
        respond.put("patient", newPatient);
        respond.put("accessToken", auth.getString("accessToken"));

        JSONObject finalObj = new JSONObject();
        finalObj.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
        finalObj.put(StringConstants.MSG, "Data fetched successfully");
        finalObj.put(StringConstants.DATA, response);

        ans[0] = resp.get("requestId").toString();
        ans[1] = finalObj.toString();

        return ans;
    }
}
