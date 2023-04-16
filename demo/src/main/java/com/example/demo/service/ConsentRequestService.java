package com.example.demo.service;

import com.example.demo.common.APIList;
import com.example.demo.constants.StringConstants;
import com.example.demo.helper.Service.PatientServiceHelper;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.utility.TokenUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.common.ResponseHelper.prepareHeader;
import static com.example.demo.helper.Service.ConsentRequestServiceHelper.*;
import static com.example.demo.helper.misc.*;

@Service
public class ConsentRequestService {
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    ConsentRequestRepository consentRequestRepository;
    @Autowired
    ConsentRepository consentRepository;
    @Autowired
    CareContextRepository careContextRepository;
    public ConsentRequest prepareConsentRequest(String req) {
        JSONObject requestObj = new JSONObject(req);
        ConsentRequest consentRequest = new ConsentRequest();
        consentRequest.setPurpose(requestObj.getString("purpose"));
        consentRequest.setPurposeCode("CAREMGT");
        consentRequest.setDateFrom(convertDateTOZonedUTC(requestObj.getString("dateFrom")));
        consentRequest.setDateTo(convertDateTOZonedUTC(requestObj.getString("dateTo")));
        consentRequest.setDataEraseAt(convertDateTOZonedUTC(requestObj.getString("dataEraseAt")));
        consentRequest.setAccessMode(requestObj.getString("accessMode"));
        consentRequest.setHiTypes(requestObj.getJSONArray("hiTypes").toString());
        consentRequest.setPatient(patientRepository.findPatientById(Long.parseLong(requestObj.getString("patientId"))));
        consentRequest.setDoctor(employeeRepository.findEmployeeById(Long.parseLong(requestObj.getString("doctorId"))));
        return consentRequestRepository.save(consentRequest);
    }

    public JSONObject prepareConsentRequestInIt(ConsentRequest consentRequest) {
        JSONObject response = new JSONObject();
        response.put("requestId", getRandomUUID());
        response.put("timestamp", getTimeStamp());
        response.put("consent", getConsentObjectForInIt(consentRequest));
        consentRequest.setRequestId(response.getString("requestId"));
        consentRequestRepository.save(consentRequest);
        return response;
    }

    public String fireABDMConsentRequestInit(ConsentRequest consentRequest) {
        JSONObject requestBody = prepareConsentRequestInIt(consentRequest);
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = PatientServiceHelper.prepareGenerateOTPHeader(authToken);

        HttpEntity<String> entity = new HttpEntity<String>(requestBody.toString(), headers);
        restTemplate.postForObject(APIList.CARE_CONTEXT_INIT, entity, String.class);
        return requestBody.get("requestId").toString();
    }

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

    public void updateConsentRequestStatusFailed(String requestId) {
        ConsentRequest consentRequest = consentRequestRepository.findConsentRequestByConsentRequestId(requestId);
        consentRequest.setStatus("FAILED");
        consentRequestRepository.save(consentRequest);
    }

    public void updateConsentRequestStatus(JSONObject obj) {
        String consentRequestId = obj.getJSONObject("notification").getString("consentRequestId");
        String status = obj.getJSONObject("notification").getString("status");
        ConsentRequest consentRequest = consentRequestRepository.findConsentRequestByConsentRequestId(consentRequestId);
        consentRequest.setStatus(status);
        if (status.equals("GRANTED")) {
            for (Object object : obj.getJSONObject("notification").getJSONArray("consentArtefacts")) {
                JSONObject artifactObj = (JSONObject) object;
                Consent consent = new Consent();
                consent.setConsentId(artifactObj.getString("id"));
                consent.setStatus("REQUESTED");
                consentRepository.save(consent);
                consentRequest.addConsent(consent);
            }
        }
        consentRequestRepository.save(consentRequest);
    }

    public String fireArtifactsFetchRequest(JSONArray arr) {
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = prepareHeader(authToken);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject requestObj = prepareFetchRequestObj(((JSONObject)arr.get(i)).getString("id"));
            HttpEntity<String> entity = new HttpEntity<String>(requestObj.toString(), headers);
            restTemplate.postForObject(APIList.CARE_CONTEXT_FETCH, entity, String.class);
            consentRepository.findConsentByConsentId(((JSONObject)arr.get(i)).getString("id")).setRequestId(requestObj.getString("requestId"));
        }
        return "";
    }

    public Consent updateConsentRequestAfterOnFetch(JSONObject requestObj) {
        Consent consent = consentRepository.findConsentByRequestId(requestObj.getJSONObject("resp").getString("requestId"));
        requestObj = requestObj.getJSONObject("consent");
        consent.setStatus(requestObj.getString("status"));
        consent.setSignature(requestObj.getString("signature"));
        requestObj = requestObj.getJSONObject("consentDetail");
        assert consent.getConsentId().equals(requestObj.getString("consentId"));
        JSONArray careContextArr = requestObj.isNull("careContexts") ? new JSONArray() : requestObj.getJSONArray("careContexts");
        for (int i = 0; i < careContextArr.length(); i++) {
            JSONObject cc = (JSONObject) careContextArr.get(i);
            CareContext careContext = new CareContext(cc.getString("patientReference"), cc.getString("careContextReference"));
            careContextRepository.save(careContext);
            consent.addCareContext(careContext);
        }
        consent.setHiTypes(requestObj.getJSONArray("hiTypes").toString());
        consent.setAccessMode(requestObj.getJSONObject("permission").getString("accessMode"));
        consent.setDataFrom(requestObj.getJSONObject("permission").getJSONObject("dateRange").getString("from"));
        consent.setDataTo(requestObj.getJSONObject("permission").getJSONObject("dateRange").getString("to"));
        consent.setDataEraseAt(requestObj.getJSONObject("permission").getString("dataEraseAt"));
        return consentRepository.save(consent);
    }

    public String fireABDMHealthInformationCMRequest(Consent consent) {
        JSONObject requestBody = prepareHealthInformationCMRequest(consent);
        String authToken = TokenUtil.getAccessToken();
        if (authToken.equals("-1")) return null;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = prepareHeader(authToken);

        HttpEntity<String> entity = new HttpEntity<String>(requestBody.toString(), headers);
        consent.setRequestId(requestBody.getString("requestId"));
        consentRepository.save(consent);
        restTemplate.postForObject(APIList.HEALTH_DATA_REQUEST, entity, String.class);
        return requestBody.get("requestId").toString();
    }

    public void updateConsentTransactionId(JSONObject requestBody) {
        Consent consent = consentRepository.findConsentByRequestId(requestBody.getJSONObject("resp").getString("requestId"));
        consent.setStatus(requestBody.getJSONObject("hiRequest").getString("sessionStatus"));
        consent.setTransactionId(requestBody.getJSONObject("hiRequest").getString("transactionId"));
        consentRepository.save(consent);
    }

    /*
    {
		"pageNumber": 0,
		"pageCount": 0,
		"transactionId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
		"entries": [
			{
				"content": "Encrypted content of data packaged in FHIR bundle",
				"media": "application/fhir+json",
				"checksum": "string",
				"careContextReference": "RVH1008"
			},
			{
				"link": "https://data-from.net/sa2321afaf12e13",
				"media": "application/fhir+json",
				"checksum": "string",
				"careContextReference": "NCC1701"
			}
		],
		"keyMaterial": {
		"cryptoAlg": "ECDH",
		"curve": "Curve25519",
		"dhPublicKey": {
			"expiry": "2023-04-15T18:45:55.754Z",
			"parameters": "Curve25519/32byte random key",
			"keyValue": "string"
		},
		"nonce": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
		}
	}
     */
    public void saveData(JSONObject data) {
        Consent consent = consentRepository.findConsentByTransactionId(data.getString("transactionId"));
        // TODO
    }
}
