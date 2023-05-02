package com.example.demo.helper.Service;

import com.example.demo.constants.StringToChange;
import com.example.demo.controllers.VisitController;
import com.example.demo.model.CareContext;
import com.example.demo.model.Consent;
import com.example.demo.model.Visit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;

import static com.example.demo.helper.DataEncrypterDecrypter.encryptFHIRData;
import static com.example.demo.helper.DataEncrypterDecrypter.receiverKeys;
import static com.example.demo.helper.FHIRJSON.prepareFHIRJSONString;
import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;
import com.example.demo.model.ConsentHIP;

public class DataTransferServiceHelper {

    static Logger logger = LoggerFactory.getLogger(DataTransferServiceHelper.class);
    public static CareContext convertVisitIntoCareContext(Visit visit) {
        logger.info("Entering convertVisitIntoCareContext with data: " + visit);
        CareContext careContext = new CareContext(visit.getReferenceNumber(), visit.getDisplay());

        careContext.setCareContextReference(visit.getReferenceNumber());
        careContext.setPatientReference(visit.getDisplay());

        careContext.setDiagnosis(visit.getDiagnosis());
        careContext.setPrescription(visit.getPrescription());
        careContext.setDosageInstruction(visit.getDosageInstruction());

        careContext.setDoctorName(visit.getDoctor().getName());
        careContext.setDoctorId("" + visit.getDoctor().getId());
        careContext.setPatientId("" + visit.getPatient().getId());
        careContext.setPatientName("" + visit.getPatient().getName());
        logger.info("Exiting convertVisitIntoCareContext with created carecontext: " + careContext);
        return careContext;
    }

    public static JSONObject prepareOnNotifyRequestObject(String[] ids) {
        logger.info("Entering prepareOnNotifyRequestObject wiht data" + ids.toString());
        JSONObject response = new JSONObject();

        response.put("resp", new JSONObject());
        response.getJSONObject("resp").put("requestId", ids[1]);

        response.put("acknowledgement", new JSONObject());
        response.getJSONObject("acknowledgement").put("status", "OK");
        response.getJSONObject("acknowledgement").put("consentId", ids[0]);
        logger.info("Exiting prepareOnNotifyRequestObject with data: " + response);
        return response;
    }

    public static JSONObject prepareRequestAcknowledgementRequest(String txnId, String requestId) {
        logger.info("Entering prepareRequestAcknowledgementRequest with data: txnId: " + txnId + " requestId: " + requestId);
        JSONObject response = new JSONObject();
        response.put("resp", new JSONObject());
        response.getJSONObject("resp").put("requestId", requestId);

        response.put("requestId", getRandomUUID());
        response.put("timestamp", getTimeStamp());

        response.put("hiRequest", new JSONObject());
        response.getJSONObject("hiRequest").put("transactionId", txnId);
        response.getJSONObject("hiRequest").put("sessionStatus", "ACKNOWLEDGED");
        logger.info("Exiting prepareRequestAcknowledgementRequest with data: " + response.toString());
        return response;
    }

    public static JSONObject prepareDataToTransfer(ConsentHIP consent, JSONObject requestObj) {
        logger.info("Entering prepareDataToTransfer with data: --\\/---");
        logger.info("Consent : " + consent);
        logger.info("requestObj: " + requestObj);
        String txnId = requestObj.getString("transactionId");
        String randomReceiver = requestObj.getJSONObject("hiRequest").getJSONObject("keyMaterial").getString("nonce");
        String receiverPublicKey = requestObj.getJSONObject("hiRequest").getJSONObject("keyMaterial").getJSONObject("dhPublicKey").getString("keyValue");

        if (consent == null) {
            logger.error("consent not found with consent id " + requestObj.getJSONObject("hiRequest").getJSONObject("consent").getString("id"));
            return null;
        }

        HashMap<String, String> keys = receiverKeys();
        List<CareContext> careContextList = consent.getCareContextList();
        JSONObject dataObject = new JSONObject();
        dataObject.put("pageNumber", 1);
        dataObject.put("pageNumber", 1);
        dataObject.put("transactionId", txnId);
        dataObject.put("keyMaterial", new JSONObject());
        dataObject.getJSONObject("keyMaterial").put("cryptoAlg", "ECDH");
        dataObject.getJSONObject("keyMaterial").put("curve", "Curve25519");
        dataObject.getJSONObject("keyMaterial").put("dhPublicKey", new JSONObject());
        dataObject.getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("expiry", "2023-06-05T00:00:00.000Z");
        dataObject.getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("parameters", "Curve25519/32byte random key");
        dataObject.getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("keyValue", keys.get("publicKey"));
        dataObject.getJSONObject("keyMaterial").put("nonce", keys.get("random"));
        dataObject.put("entries", new JSONArray());

        for (CareContext careContext : careContextList) {
            String fhirData = prepareFHIRJSONString(careContext.getDoctorId(), careContext.getPatientName(), careContext.getDoctorName(), careContext.getDosageInstruction(), careContext.getPatientId(), careContext.getDiagnosis(), careContext.getPrescription());
            String encryptedData = encryptFHIRData(receiverPublicKey, randomReceiver, fhirData, keys.get("privateKey"), keys.get("random"));
            JSONObject entryObject = new JSONObject();
            entryObject.put("content", encryptedData);
            entryObject.put("media", "application/fhir+json");
            entryObject.put("checksum", "string");
            entryObject.put("careContextReference", careContext.getCareContextReference());
            dataObject.getJSONArray("entries").put(entryObject);
        }
        logger.info("Exiting prepareDataToTransfer with data: " + dataObject);
        return dataObject;
    }

    public static JSONObject prepareDeliveredNotification(JSONObject object, JSONObject requestObj, ConsentHIP consent) {
        logger.info("Entering prepareDeliveredNotification with data: ");
        logger.info("object: " + object.toString());
        logger.info("requestObj " + requestObj.toString());
        logger.info("consent " + consent);
        JSONObject response = new JSONObject();
        response.put("requestId", getRandomUUID());
        response.put("timestamp", getTimeStamp());
        response.put("notification", new JSONObject());

        response.getJSONObject("notification").put("consentId", requestObj.getJSONObject("hiRequest").getJSONObject("consent").getString("id"));
        response.getJSONObject("notification").put("doneAt", getTimeStamp());
        response.getJSONObject("notification").put("transactionId", object.getString("transactionId"));

        response.getJSONObject("notification").put("notifier", new JSONObject());
        response.getJSONObject("notification").getJSONObject("notifier").put("type", "HIU");
        response.getJSONObject("notification").getJSONObject("notifier").put("id", StringToChange.HIU_ID);

        response.getJSONObject("notification").put("statusNotification", new JSONObject());
        response.getJSONObject("notification").getJSONObject("statusNotification").put("sessionStatus", "DELIVERED");
        response.getJSONObject("notification").getJSONObject("statusNotification").put("hipId", StringToChange.HIP_ID);
        response.getJSONObject("notification").getJSONObject("statusNotification").put("statusResponses", new JSONArray());
        List<CareContext> list = consent.getCareContextList();
        for (int i = 0; i < list.size(); i++) {
            CareContext cc = list.get(i);
            JSONObject tempObj = new JSONObject();
            tempObj.put("careContextReference", cc.getCareContextReference());
            tempObj.put("hiStatus", "OK");
            tempObj.put("description", "string");
            response.getJSONObject("notification").getJSONObject("statusNotification").getJSONArray("sessionStatus").put(tempObj);
        }
        logger.info("Exiting prepareDeliveredNotification with data: " + response.toString());
        return response;
    }
}
