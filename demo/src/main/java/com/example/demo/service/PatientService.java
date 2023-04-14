package com.example.demo.service;

import com.example.demo.abdm.Util.TokenUtil;
import com.example.demo.abdm.api.APIList;
import com.example.demo.constants.StringConstants;
import com.example.demo.helper.Service.PatientServiceHelper;
import com.example.demo.model.Patient;
import com.example.demo.model.Visit;
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

        if (obj.get("error").toString().equals("null")) {
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

    public String[] prepareOnConfirmOTPResponse(String response) {
        String[] ans = new String[2];

        JSONObject obj = new JSONObject(response);
        JSONObject auth = obj.getJSONObject("auth");
        JSONObject resp = obj.getJSONObject("resp");
        JSONObject respond = new JSONObject();

        JSONObject patientObj =  auth.getJSONObject("patient");
        JSONArray identifiersObj = auth.getJSONArray("identifiers");

        for (Object identifier : identifiersObj) {
            JSONObject temp = (JSONObject) identifier;
            if (!temp.isNull("value") && obj.toString().equals("MOBILE")) patientObj.put("mobile", temp.getString("value"));
            if (!temp.isNull(obj.toString()) && obj.toString().equals("HEALTH_NUMBER")) patientObj.put("abhaNumber", temp.getString("value"));
        }

        Patient newPatient = patientRepository.findPatientByAbhaId(patientObj.getString("id"));
        newPatient = (newPatient != null) ? newPatient : patientRepository.save(createNewPatient(patientObj));


        respond.put("patient", newPatient);
        ans[0] = resp.get("requestId").toString();
        ans[1] = respond.toString();
        return ans;
    }
}
