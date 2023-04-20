package com.example.demo.helper.Service;

import com.example.demo.model.Visit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;

import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;

public class VisitServiceHelper {
    static Logger logger = LoggerFactory.getLogger(VisitServiceHelper.class);
    public static JSONObject prepareAddContextRequest(String patientAuthToken, Visit visit, String patientId, String patientName) {
        logger.info("Entering prepareAddContextRequest with data: ");
        logger.info("pateintAuthToken: " + patientAuthToken);
        logger.info("visit: " + visit);
        logger.info("patientId: " + patientId);
        logger.info("patientname: " + patientName);

        JSONObject request = new JSONObject();
        request.put("requestId", getRandomUUID());
        request.put("timestamp", getTimeStamp());

        JSONObject link = new JSONObject();
        link.put("accessToken", patientAuthToken);

        JSONObject pateint = new JSONObject();
        pateint.put("referenceNumber", patientId);
        pateint.put("display", patientName);

        JSONObject careContext = new JSONObject();
        careContext.put("referenceNumber", visit.getReferenceNumber());
        careContext.put("display", visit.getDisplay());
        link.put("patient", pateint);
        pateint.put("careContexts", new JSONArray());
        pateint.getJSONArray("careContexts").put(careContext);
        request.put("link", link);

        logger.info("Exiting prepareAddContextRequest with data: " + request);
        return request;
    }
}
