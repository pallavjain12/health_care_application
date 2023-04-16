package com.example.demo.helper.Service;

import com.example.demo.constants.StringConstants;
import com.example.demo.model.Consent;
import com.example.demo.model.ConsentRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.example.demo.helper.misc.getRandomUUID;
import static com.example.demo.helper.misc.getTimeStamp;

public class ConsentRequestServiceHelper {
    public static JSONObject prepareFetchRequestObj(String consentId) {
        JSONObject ans = new JSONObject();
        ans.put("requestId", getRandomUUID());
        ans.put("timestamp", getTimeStamp());
        ans.put("consentId", consentId);
        return ans;
    }

    public static JSONObject getConsentObjectForInIt(ConsentRequest consentRequest) {
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
        return consent;
    }
    /*
    {
			"keyMaterial": {
				"cryptoAlg": "ECDH",
				"curve": "Curve25519",
				"dhPublicKey": {
					"expiry": "2023-04-15T18:42:28.415Z",
					"parameters": "Curve25519/32byte random key",
					"keyValue": "string"
				},
				"nonce": "3fa85f64-5717-4562-b3fc-2c963f66afa6"
			}
		}
	}
     */

    public static JSONObject prepareHealthInformationCMRequest(Consent consent) {
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
        // TODO
        //  - paramenetes
        //  - expiry
        //  - nonce
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("expiry", "");
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("parameters", "");
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").getJSONObject("dhPublicKey").put("keyValue", "");
        response.getJSONObject("hiRequest").getJSONObject("keyMaterial").put("nonce", "");
        return response;
    }
}
