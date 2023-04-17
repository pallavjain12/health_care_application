package com.example.demo.service;

import com.example.demo.utility.TokenUtil;
import com.example.demo.common.APIList;
import com.example.demo.constants.StringConstants;
import com.example.demo.helper.Service.PatientServiceHelper;
import com.example.demo.model.Patient;
import com.example.demo.repository.PatientRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.common.ResponseHelper.prepareHeader;
import static com.example.demo.helper.Service.PatientServiceHelper.createNewPatient;


@Service
public class PatientService {
    Logger logger = LoggerFactory.getLogger(PatientService.class);
    @Autowired
    PatientRepository patientRepository;

    public String fireABDMGenerateOTP(String abhaId) {
        logger.info("enteing fireABDM with data: " + abhaId);
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();
        JSONObject request = PatientServiceHelper.prepareGenerateOTPEntity(abhaId);
        HttpHeaders headers = prepareHeader(authToken);

        HttpEntity<String> entity = new HttpEntity<String>(request.toString(), headers);
        restTemplate.postForObject(APIList.AUTH_INIT, entity, String.class);
        return request.get("requestId").toString();
    }

    public String[] prepareOnGenerateResponse(String response) {
        logger.info("entering prepareOnGenerateResponse with data: " + response);
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
        logger.info("Entering prepareOnGenerateResponse with data: " + ans.toString() );
        return ans;
    }

    public String fireABDMConfirmOTP(String transactionId, String OTP) {
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();

        JSONObject request = PatientServiceHelper.prepareConfirmOTPRequest(transactionId, OTP);
        HttpHeaders headers = prepareHeader(authToken);

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
        JSONArray identifiersObj = patientObj.getJSONArray("identifiers");
        patientObj.put("mobile", "");
        patientObj.put("abhaNumber", "");
        if (!patientObj.isNull("identifiers")) {
            for (Object identifier : identifiersObj) {
                JSONObject temp = (JSONObject) identifier;
                if (!temp.isNull("value") && temp.getString("type").equals("MOBILE")) patientObj.put("mobile", temp.getString("value"));
                if (!temp.isNull("value") && temp.getString("type").equals("HEALTH_NUMBER")) patientObj.put("abhaNumber", temp.getString("value"));
            }
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
