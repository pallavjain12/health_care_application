package com.example.demo.common;

import com.example.demo.constants.StringConstants;
import org.json.JSONObject;

public class ResponseHelper {
    public static String serverSideError(String e) {
        JSONObject obj = new JSONObject();
        obj.put(StringConstants.STATUS, StringConstants.UNSUCCESSFULL);
        obj.put(StringConstants.MSG, "Error occurred. " + e);
        return obj.toString();
    }
}
