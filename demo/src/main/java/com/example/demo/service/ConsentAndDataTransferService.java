package com.example.demo.service;

import com.example.demo.constants.StringConstants;
import com.example.demo.model.ConsentRequest;
import com.example.demo.model.Employee;
import com.example.demo.model.Patient;
import com.example.demo.repository.ConsentRequestRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.PatientRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;
@Service
public class ConsentAndDataTransferService {
    @Autowired
    PatientRepository patientRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ConsentRequestRepository consentRequestRepository;
    /*
         {
            "requestId": "{{$guid}}",
            "timestamp": "{{$isoTimestamp}}",
            "consent": {
                "purpose": {
                    "text": "Previous mediactions record",
                    "code": "CAREMGT"
                },
                "patient": {
                    "id": "pallavjain@sbx"
                },
                "hiu": {
                    "id": "team-29-hiu-1"
                },
                "requester": {
                    "name": "Dr. Saurabh Tripathi",
                    "identifier": {
                        "type": "REGNO",
                        "value": "MH1001",
                        "system": "https://www.mciindia.org"
                    }
                },
                "hiTypes": [
                    "OPConsultation"
                ],
                "permission": {
                    "accessMode": "VIEW",
                    "dateRange": {
                        "from": "2022-09-25T12:52:34.925Z",
                        "to": "2022-11-15T12:52:34.925Z"
                    },
                    "dataEraseAt": "2023-05-25T12:52:34.925Z",
                    "frequency": {
                        "unit": "HOUR",
                        "value": 1,
                        "repeats": 0
                    }
                }
            }
        }
         */
    public JSONObject getConsentObject(String req) {
        JSONObject response = new JSONObject();
        JSONObject request = new JSONObject(req);
        String patient_id = request.getString("patient_id");
        String doctor_id = request.getString("doctor_id");
        Patient patient = patientRepository.findPatientById(Long.parseLong(patient_id));
        Employee doctor = employeeRepository.findEmployeeById(Long.parseLong(doctor_id));

        JSONObject purpose = new JSONObject();
        purpose.put("text", request.getString("purpose"));
        purpose.put("code", request.getString("code"));
        response.put("purpose", purpose);

        JSONObject patientObj = new JSONObject();
        patientObj.put("id", patient.getAbhaId());
        response.put("patient", patientObj);

        JSONObject hiu = new JSONObject();
        hiu.put("id", StringConstants.HIU_ID);
        response.put("hiu", hiu);

        JSONObject requester = new JSONObject();
        requester.put("name", doctor.getName());
        JSONObject identifier = new JSONObject();
        identifier.put("type", StringConstants.REGNO);
        identifier.put("value", doctor.getRegistrationNumber());
        identifier.put("system", StringConstants.REQUESTER_IDENTIFIER_SYSTEM);
        requester.put("identifier", identifier);
        response.put("requester", requester);

        response.put("hiTypes", request.getJSONArray("hiTypes"));

        JSONObject permission = new JSONObject();
        permission.put("accessMode", request.getString("accessMode"));
        JSONObject dateRange = new JSONObject();
        // TODO: check how to convert normal date and time to desired dataTime object here
        dateRange.put("from", request.getString("dateTimeFrom"));
        dateRange.put("to", request.getString("dateTimeTo"));
        permission.put("dataEraseAt", request.getString("dataEraseAt"));
        JSONObject frequency = new JSONObject();
        frequency.put("unit", "HOUR");
        frequency.put("value", 1);
        frequency.put("repeats", 0);
        permission.put("frequency", frequency);
        response.put("permission", permission);

        return response;
    }

    public JSONObject getConsentInitObject(JSONObject req) {
        JSONObject ans = new JSONObject();
        ans.put("requestId", getRandomUUID());
        ans.put("timestamp", getTimeStamp());
        ans.put("consent", req);
        return ans;
    }

    public boolean saveConsentRequest(JSONObject requestObj) {
        ConsentRequest consentRequest = new ConsentRequest();
        consentRequest.setRequestId(requestObj.getString("requestId"));
        consentRequest.setPatient(patientRepository.findPatientByAbhaId(requestObj.getJSONObject("patient").getString("id")));
        consentRequest.setDoctor(employeeRepository.findEmployeesByRegistrationNumber(requestObj.getJSONObject("requester").getJSONObject("identifier").getString("value")));
//        consentRequest.setConsentExpiryDateTime();
//        consentRequest.setHealthInfoFromDate();
//        consentRequest.setHealthInfoUpToDate();
//        consentRequest.setPurpose();
        consentRequestRepository.save(consentRequest);
        return true;
    }

    /*
            {
              "requestId": "8ac7fb54-6b8c-48cc-b064-60cc437fe187",
              "timestamp": "2023-04-14T13:23:29.547906116",
              "consentRequest": {
                "id": "82caedec-d1cc-484a-b433-8ae236be7bd5"
              },
              "error": null,
              "resp": {
                "requestId": "f0df30f1-0875-4628-a721-0b86630c40d8"
              }
            }
         */
    public String[] prepareOnConsentRequestInitResponse(String responseBody) {
        JSONObject obj = new JSONObject(responseBody);
        String requestId = obj.getJSONObject("resp").getString("requestId");
        JSONObject response = new JSONObject();
        String[] ans = new String[3];
        ans[0] = requestId;
        if (obj.isNull("error")) {
            response.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
            response.put(StringConstants.MSG, "Consent request sent successfully");
            ans[1] = obj.getJSONObject("consentRequest").getString("id");
        }
        else {
            response.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
            response.put(StringConstants.MSG, obj.getJSONObject("error").getString("message"));
            ans[1] = null;
        }
        ans[2] = response.toString();
        return ans;
    }

    public void updateConsentRequestId(String requestId, String consentRequestId) {
        ConsentRequest consentRequest = consentRequestRepository.findConsentRequestByRequestId(requestId);
        consentRequest.setConsentRequestId(consentRequestId);
        consentRequest.setStatus("REQUESTED");
        consentRequestRepository.save(consentRequest);
    }

    public void updateConsentStatus(JSONObject obj) {
        String consentRequestId = obj.getJSONObject("notification").getString("consentRequestId");
        String status = obj.getJSONObject("notification").getString("status");
        ConsentRequest consentRequest = consentRequestRepository.findConsentRequestByConsentRequestId(consentRequestId);
        consentRequest.setStatus(status);
        if (status.equals("GRANTED")) {
            consentRequest.setConsentArtifactsId(obj.getJSONObject("notification").getJSONObject("consentArtefacts").getString("id"));
            consentRequestRepository.save(consentRequest);
        }
    }

    /*
    {
        "requestId": "{{$guid}}",
        "timestamp": "{{$isoTimestamp}}",
        "acknowledgement": [
            {
                "status": "OK",
                "consentId": "ac4c2823-46d5-4ebd-8276-f90fe4bfc49a"
            }
        ],
        "resp": {
            "requestId": "cf39fde8-2990-4c08-aa69-d396c884f64d"
        }
    }
     */
}
