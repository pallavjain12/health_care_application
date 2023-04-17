package com.example.demo.common;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.print.attribute.standard.JobKOctets;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.example.demo.helper.misc.getRandomUUID;

public class APIList {
    public static final String FETCH_AUTH_TOKEN = "https://dev.abdm.gov.in/gateway/v0.5/sessions";
    public static final String REGISTER_HRP_HOST = "https://dev.abdm.gov.in/devservice/v1/bridges";
    public static final String ADD_CARE_CONTEXT = "https://dev.abdm.gov.in/gateway/v0.5/links/link/add-contexts";
    public static final String AUTH_CONFIRM = "https://dev.abdm.gov.in/gateway/v0.5/users/auth/confirm";
    public static final String AUTH_INIT = "https://dev.abdm.gov.in/gateway/v0.5/users/auth/init";
    public static final String CARE_CONTEXT_INIT = "https://dev.abdm.gov.in/gateway/v0.5/consent-requests/init";
    public static final String CARE_CONTEXT_FETCH = "https://dev.abdm.gov.in/gateway/v0.5/consents/fetch";
    public static final String HEALTH_DATA_REQUEST = "https://dev.abdm.gov.in/gateway/v0.5/health-information/cm/request";
    public static final String HIP_REQUEST_ON_NOTIFY = "/v0.5/consents/hip/on-notify";
    public static final String HIP_REQUEST_ON_REQUEST = "/v0.5/health-information/hip/on-request";
    public static final String HIP_ON_DATA_TRANSFER_COMPLETE_NOTIFY = "/v0.5/health-information/notify";

    public static void main(String[] args) {
        String doctorName = "Abdul bari";
        String dosageInstructions = "2 times a day";
        String doctorId = "34";
        String diagnosis = "AIDS";
        String patientId = "1";
        String patientName = "KRK";
        String medicineName = "Vodka 200ml neat";

        JSONObject o = new JSONObject("{\"resourceType\":\"Bundle\",\"id\":\""+getRandomUUID()+"\",\"meta\":{\"lastUpdated\":\"2018-08-01T00:00:00.000+05:30\"},\"identifier\":{\"system\":\"https://www.max.in/bundle\",\"value\":\""+getRandomUUID()+"\"},\"type\":\"document\",\"timestamp\":\"2018-08-01T00:00:00.000+05:30\",\"entry\":[{\"fullUrl\":\"Composition/"+getRandomUUID()+"\",\"resource\":{\"resourceType\":\"Composition\",\"id\":\""+getRandomUUID()+"\",\"identifier\":{\"system\":\"https://www.max.in/document\",\"value\":\""+getRandomUUID()+"\"},\"status\":\"final\",\"type\":{\"coding\":[{\"system\":\"https://projecteka.in/sct\",\"code\":\"440545006\",\"display\":\"Prescription record\"}]},\"subject\":{\"reference\":\"Patient/" + patientId +"\"},\"date\":\"2018-08-01T00:00:00.605+05:30\",\"author\":[{\"reference\":\"Practitioner/"+ doctorId + "\",\"display\":\"" + doctorName + "\"}],\"title\":\"Prescription\",\"section\":[{\"title\":\"OPD Prescription\",\"code\":{\"coding\":[{\"system\":\"https://projecteka.in/sct\",\"code\":\"440545006\",\"display\":\"Prescription record\"}]},\"entry\":[{\"reference\":\"MedicationRequest/"+getRandomUUID()+"\"}]}]}},{\"fullUrl\":\"Practitioner/"+doctorId+"\",\"resource\":{\"resourceType\":\"Practitioner\",\"id\":\""+doctorId+"\",\"identifier\":[{\"system\":\"https://www.mciindia.in/doctor\",\"value\":\""+doctorId+"\"}],\"name\":[{\"text\":\""+doctorName+"\",\"prefix\":[\"Dr\"],\"suffix\":[\"\"]}]}},{\"fullurl\":\"Patient/"+patientId+"\",\"resource\":{\"resourceType\":\"Patient\",\"id\":\""+patientId+"\",\"name\":[{\"text\":\""+patientName+"\"}],\"gender\":\"male\"}},{\"fullUrl\":\"Condition/"+getRandomUUID()+"\",\"resource\":{\"resourceType\":\"Condition\",\"id\":\""+getRandomUUID()+"\",\"code\":{\"text\":\""+diagnosis+"\"},\"subject\":{\"reference\":\"Patient/"+patientId+"\"}}},{\"fullUrl\":\"Medication/"+getRandomUUID()+"\",\"resource\":{\"resourceType\":\"Medication\",\"id\":\""+getRandomUUID()+"\",\"code\":{\"text\":\""+medicineName+"\"}}},{\"fullUrl\":\"MedicationRequest/"+getRandomUUID()+"\",\"resource\":{\"resourceType\":\"MedicationRequest\",\"id\":\""+getRandomUUID()+"\",\"status\":\"active\",\"intent\":\"order\",\"medicationReference\":{\"reference\":\"Medication/"+getRandomUUID()+"\"},\"subject\":{\"reference\":\"Patient/"+patientId+"\"},\"authoredOn\":\"2018-08-01T00:00:00+05:30\",\"requester\":{\"reference\":\"Practitioner/"+doctorId+"\"},\"reasonReference\":[{\"reference\":\"Condition/"+getRandomUUID() +"\"}],\"dosageInstruction\":[{\"text\":\""+dosageInstructions+"\"}]}}]}");

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
        System.out.println(o.toString());
    }
}
