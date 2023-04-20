package com.example.demo.helper.Service;

import com.example.demo.model.Patient;
import com.example.demo.service.VisitService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;

public class PatientServiceHelper {
    static Logger logger = LoggerFactory.getLogger(PatientServiceHelper.class);
    public static JSONObject prepareGenerateOTPEntity(String abhaId) {
        logger.info("Entering prepareGenerateOTPEntity with data abhaId: " + abhaId);
        JSONObject request = new JSONObject();
        request.put("requestId", getRandomUUID());
        request.put("timestamp", getTimeStamp());
            JSONObject query = new JSONObject();
            query.put("id", abhaId);
            query.put("purpose", "KYC_AND_LINK");
            query.put("authMode", "MOBILE_OTP");
                JSONObject requester = new JSONObject();
                requester.put("type", "HIP");
                requester.put("id", "team-29-hip-1");
            query.put("requester", requester);
        request.put("query", query);
        logger.info("Exiting prepareGenerateOTPEntity with data: " + request.toString());
        return request;
    }

    public static JSONObject prepareConfirmOTPRequest (String txnId, String otp) {
        logger.info("Entering prepareConfirmOTPRequest with data txnId: " + txnId + " otp: " + otp);
        JSONObject request = new JSONObject();
        request.put("requestId", getRandomUUID());
        request.put("timestamp", getTimeStamp());
        request.put("transactionId", txnId);

        JSONObject credential = new JSONObject();
        credential.put("authCode", otp);

        request.put("credential", credential);
        logger.info("Exiting prepareConfirmOTPRequest with data: " + request.toString());
        return request;
    }

    public static Patient createNewPatient(JSONObject request) {
        logger.info("Entering createNewPatient with data: " + request);
        Patient patient = new Patient();
        patient.setAbhaId(request.getString("id"));
        patient.setName(request.getString("name"));
        patient.setGender(request.getString("gender"));
        patient.setYearOfBirth(request.get("yearOfBirth").toString());
        patient.setMonthOfBirth(request.get("monthOfBirth").toString());
        patient.setDateOfBirth(request.get("dayOfBirth").toString());
        patient.setRegistrationDateTime(LocalDate.now());
        patient.setMobile(request.getString("mobile"));
        patient.setAbhaNumber(request.getString("abhaNumber"));
        logger.info("Exiting createNewPatient with data: " + patient.getPatientJSONObject());
        return patient;
    }
}
