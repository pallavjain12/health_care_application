package com.example.demo.helper.Service;

import com.example.demo.constants.StringConstants;
import com.example.demo.model.Employee;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class EmployeeServiceHelper {
    static Logger logger = LoggerFactory.getLogger(EmployeeServiceHelper.class);
    public static String convertToJSONSrtByRole(List <Employee> list) {
        logger.info("Entering convertToJSONSrtByRole with data" + Arrays.toString(list.toArray()));
        JSONObject response = new JSONObject();

        JSONArray doctors = new JSONArray();
        JSONArray frontdesk = new JSONArray();
        JSONArray admin = new JSONArray();

        for (Employee employee : list) {
            if (employee.getRole().equals(StringConstants.Doctor)) {
                doctors.put(employee.toJSONObject());
            }
            else if (employee.getRole().equals(StringConstants.ADMIN)) {
                admin.put(employee.toJSONObject());
            }
            else {
                frontdesk.put(employee.toJSONObject());
            }
        }
        response.put(StringConstants.FRONTDESK, frontdesk);
        response.put(StringConstants.Doctor, doctors);
        response.put(StringConstants.ADMIN, admin);
        logger.info("Exiting convertToJSONSrtByRole with data: " + response);
        return response.toString();
    }

}
