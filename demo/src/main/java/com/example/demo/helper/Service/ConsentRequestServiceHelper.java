package com.example.demo.helper.Service;

import com.example.demo.constants.StringConstants;
import com.example.demo.controllers.VisitController;
import com.example.demo.helper.DataEncrypterDecrypter;
import com.example.demo.model.CareContext;
import com.example.demo.model.Consent;
import com.example.demo.model.ConsentRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;

public class ConsentRequestServiceHelper {
    static Logger logger = LoggerFactory.getLogger(VisitController.class);
    public static JSONObject prepareFetchRequestObj(String consentId) {
        logger.info("Entering prepareFetchRequestObj with data: consentId = " + consentId);
        JSONObject ans = new JSONObject();
        ans.put("requestId", getRandomUUID());
        ans.put("timestamp", getTimeStamp());
        ans.put("consentId", consentId);
        logger.info("Exiting prepareFetchRequestObj with data: " + ans.toString());
        return ans;
    }

    public static JSONObject getConsentObjectForInIt(ConsentRequest consentRequest) {
        logger.info("Entering getConsentObjectForInit with data: consentRequest " + consentRequest.toString());
        JSONObject consent = new JSONObject();
        consent.put("purpose", new JSONObject());
        consent.getJSONObject("purpose").put("text", consentRequest.getPurpose());
        consent.getJSONObject("purpose").put("code", consentRequest.getPurposeCode());

        consent.put("patient", new JSONObject());
        consent.getJSONObject("patient").put("id", consentRequest.getPatient().getAbhaId());

        consent.put("hiu", new JSONObject());
        consent.getJSONObject("hiu").put("id", StringConstants.HIU_ID);

        consent.put("requester", new JSONObject());
        consent.getJSONObject("requester").put("name", consentRequest.getDoctor().getName());
        consent.getJSONObject("requester").put("identifier", new JSONObject());
        consent.getJSONObject("requester").getJSONObject("identifier").put("type", StringConstants.REGNO);
        consent.getJSONObject("requester").getJSONObject("identifier").put("value", consentRequest.getDoctor().getRegistrationNumber());
        consent.getJSONObject("requester").getJSONObject("identifier").put("type", StringConstants.REQUESTER_IDENTIFIER_SYSTEM);

        consent.put("hiTypes", new JSONArray(consentRequest.getHiTypes()));

        consent.put("permission", new JSONObject());
        consent.getJSONObject("permission").put("accessMode", consentRequest.getAccessMode());
        consent.getJSONObject("permission").put("dataEraseAt", consentRequest.getDataEraseAt());
        consent.getJSONObject("permission").put("dateRange", new JSONObject());
        consent.getJSONObject("permission").getJSONObject("dateRange").put("from", consentRequest.getDateFrom());
        consent.getJSONObject("permission").getJSONObject("dateRange").put("to", consentRequest.getDateTo());

        consent.getJSONObject("permission").put("frequency", new JSONObject());
        consent.getJSONObject("permission").getJSONObject("frequency").put("unit", "HOUR");
        consent.getJSONObject("permission").getJSONObject("frequency").put("value", 1);
        consent.getJSONObject("permission").getJSONObject("frequency").put("repeats", 0);
        logger.info("Exiting getConsentObjectForInIt with data: consent = " + consent);
        return consent;
    }

    public static JSONObject prepareHealthInformationCMRequest(Consent consent) {
        logger.info("Entering prepareHealthInformationCMRequest with data: consent = " + consent);
        JSONObject response = new JSONObject();
        response.put("requestId", getRandomUUID());
        response.put("timeStamp", getTimeStamp());
        response.put("hiRequest", new JSONObject());

        response.getJSONObject("hiRequest").put("consent", new JSONObject());
        response.getJSONObject("hiRequest").getJSONObject("consent").put("id", consent.getConsentId());

        response.getJSONObject("hiRequest").put("dateRange", new JSONObject());
        response.getJSONObject("hiRequest").getJSONObject("dateRange").put("from", consent.getDataFrom());
        response.getJSONObject("hiRequest").getJSONObject("dateRange").put("to", consent.getDataTo());

        response.getJSONObject("hiRequest").put("dataPushUrl", StringConstants.DATA_PUSH_URL);

        response.getJSONObject("hiRequest").put("keyMaterial", new JSONObject());
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").put("cryptoAlg", StringConstants.CRYPTO_ALGO);

        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").put("dhPublicKey", new JSONObject());
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("expiry", "2023-06-05T01:02:03.0009Z");
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("parameters", "Ephemeral public key");
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("keyValue", consent.getReceiverPublicKey());
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").put("nonce", consent.getReceiverNonce());
        logger.info("exiting prepareHealthInformationCMRequest with data : " + response);
        return response;
    }

    public static CareContext findCareContext(List<CareContext> list, String careContextName) {
        for (int i = 0; i < list.size(); i++) {
            CareContext cc = list.get(i);
            if (cc.getCareContextReference().equals(careContextName))   return cc;
            else return null;
        }
        return null;
    }

    public static HashMap<String, String> updateCareContextData(String senderNonce, String senderPublicKey, String receiverNonce, String receiverPrivateKey, String encryptedData) {
        logger.info("Entering updateCareContextData with data: ");
        logger.info("sender Nonce : " + senderNonce);
        logger.info("sender Public Key : " + senderPublicKey);
        logger.info("receiverNonce: "  + receiverNonce);
        logger.info("receiverPrivate Key: " + receiverPrivateKey);
        logger.info("encrpytedData: " + encryptedData);
        try {
            String decryptedData = DataEncrypterDecrypter.decrypt(encryptedData, senderPublicKey, senderNonce, receiverPrivateKey, receiverNonce);
            logger.info("decrypted data: " + decryptedData);
            return readFHIRDataAndUpdateCareContext(decryptedData);
        }
        catch (Exception e) {
            System.out.println("Error while decrypting " + e);
            return null;
        }
    }

    public static HashMap<String, String> readFHIRDataAndUpdateCareContext(String decryptedData) {
        logger.info("Entering readFHIRDataAndUpdateCareContext with data: " + decryptedData);
        JSONObject obj = new JSONObject(decryptedData);
        JSONArray arr =  obj.getJSONArray("entry");
        String doctorId = "", patientName = "", doctorName = "", dosageInstructions = "", patientId = "", diagnosis = "", medicineName = "";
        for (int i = 0; i < arr.length(); i++) {
            JSONObject temp = arr.getJSONObject(i);
            String[] splitArr = temp.getString("fullUrl").split("/");
            String identifier = splitArr[0];
            if (identifier.equalsIgnoreCase("Practitioner")) {
                temp = temp.getJSONObject("resource");
                doctorId =  temp.getString("id");
                doctorName = temp.getJSONArray("name").getJSONObject(0).getString("text");
            }
            else if (identifier.equalsIgnoreCase("Patient")) {
                patientId = temp.getJSONObject("resource").getString("id");
                patientName = temp.getJSONObject("resource").getJSONArray("name").getJSONObject(0).getString("text");
            }
            else if (identifier.equalsIgnoreCase("Condition")) {
                diagnosis = temp.getJSONObject("resource").getJSONObject("code").getString("text");
            }
            else if(identifier.equalsIgnoreCase("Medication")) {
                medicineName = temp.getJSONObject("resource").getJSONObject("code").getString("text");
            }
            else if (identifier.equalsIgnoreCase("MedicationRequest")) {
                dosageInstructions = temp.getJSONObject("resource").getJSONArray("dosageInstruction").getJSONObject(0).getString("text");
            }
            else {
                System.out.println("Found extra data -> " + temp.toString());
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("doctorId", doctorId);
        map.put("doctorName", doctorName);
        map.put("dosageInstruction", dosageInstructions);
        map.put("medicineName", medicineName);
        map.put("diagnosis", diagnosis);
        map.put("patientName", patientName);
        map.put("patientId", patientId);
        logger.info("Exiting readFHIRDataAndUpdateCareContext with data: " + map);
        return map;
    }
}
