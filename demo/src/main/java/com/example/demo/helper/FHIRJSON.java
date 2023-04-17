package com.example.demo.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import static com.example.demo.helper.misc.getRandomUUID;
public class FHIRJSON {
    static Logger logger = LoggerFactory.getLogger(DataEncrypterDecrypter.class);

    public static String prepareFHIRJSONString(String doctorId, String patientName, String doctorName, String dosageInstructions, String patientId, String diagnosis, String medicineName)  {
        logger.info("entering prepareFHIRJSONString with data");
        logger.info(" " + doctorId+" " +patientName+" " +doctorName+" " +dosageInstructions+" " +patientId+" " +diagnosis+" " +medicineName);
        return fillFHIRJSON(doctorId,patientName,doctorName,dosageInstructions,patientId,diagnosis,medicineName);
    }

    private static String fillFHIRJSON(String doctorId, String patientName, String doctorName, String dosageInstructions, String patientId, String diagnosis, String medicineName) {
        return "{\"resourceType\":\"Bundle\",\"id\":\""+getRandomUUID()+"\",\"meta\":{\"lastUpdated\":\"2018-08-01T00:00:00.000+05:30\"},\"identifier\":{\"system\":\"https://www.max.in/bundle\",\"value\":\""+getRandomUUID()+"\"},\"type\":\"document\",\"timestamp\":\"2018-08-01T00:00:00.000+05:30\",\"entry\":[{\"fullUrl\":\"Composition/"+getRandomUUID()+"\",\"resource\":{\"resourceType\":\"Composition\",\"id\":\""+getRandomUUID()+"\",\"identifier\":{\"system\":\"https://www.max.in/document\",\"value\":\""+getRandomUUID()+"\"},\"status\":\"final\",\"type\":{\"coding\":[{\"system\":\"https://projecteka.in/sct\",\"code\":\"440545006\",\"display\":\"Prescription record\"}]},\"subject\":{\"reference\":\"Patient/" + patientId +"\"},\"date\":\"2018-08-01T00:00:00.605+05:30\",\"author\":[{\"reference\":\"Practitioner/"+ doctorId + "\",\"display\":\"" + doctorName + "\"}],\"title\":\"Prescription\",\"section\":[{\"title\":\"OPD Prescription\",\"code\":{\"coding\":[{\"system\":\"https://projecteka.in/sct\",\"code\":\"440545006\",\"display\":\"Prescription record\"}]},\"entry\":[{\"reference\":\"MedicationRequest/"+getRandomUUID()+"\"}]}]}},{\"fullUrl\":\"Practitioner/"+doctorId+"\",\"resource\":{\"resourceType\":\"Practitioner\",\"id\":\""+doctorId+"\",\"identifier\":[{\"system\":\"https://www.mciindia.in/doctor\",\"value\":\""+doctorId+"\"}],\"name\":[{\"text\":\""+doctorName+"\",\"prefix\":[\"Dr\"],\"suffix\":[\"\"]}]}},{\"fullurl\":\"Patient/"+patientId+"\",\"resource\":{\"resourceType\":\"Patient\",\"id\":\""+patientId+"\",\"name\":[{\"text\":\""+patientName+"\"}],\"gender\":\"male\"}},{\"fullUrl\":\"Condition/"+getRandomUUID()+"\",\"resource\":{\"resourceType\":\"Condition\",\"id\":\""+getRandomUUID()+"\",\"code\":{\"text\":\""+diagnosis+"\"},\"subject\":{\"reference\":\"Patient/"+patientId+"\"}}},{\"fullUrl\":\"Medication/"+getRandomUUID()+"\",\"resource\":{\"resourceType\":\"Medication\",\"id\":\""+getRandomUUID()+"\",\"code\":{\"text\":\""+medicineName+"\"}}},{\"fullUrl\":\"MedicationRequest/"+getRandomUUID()+"\",\"resource\":{\"resourceType\":\"MedicationRequest\",\"id\":\""+getRandomUUID()+"\",\"status\":\"active\",\"intent\":\"order\",\"medicationReference\":{\"reference\":\"Medication/"+getRandomUUID()+"\"},\"subject\":{\"reference\":\"Patient/"+patientId+"\"},\"authoredOn\":\"2018-08-01T00:00:00+05:30\",\"requester\":{\"reference\":\"Practitioner/"+doctorId+"\"},\"reasonReference\":[{\"reference\":\"Condition/"+getRandomUUID() +"\"}],\"dosageInstruction\":[{\"text\":\""+dosageInstructions+"\"}]}}]}";
    }
}
