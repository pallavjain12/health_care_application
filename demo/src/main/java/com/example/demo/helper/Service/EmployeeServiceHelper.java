package com.example.demo.helper.Service;

import com.example.demo.constants.StringConstants;
import com.example.demo.model.Employee;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class EmployeeServiceHelper {
    public static String convertToJSONSrtByRole(List <Employee> list) {
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
        return response.toString();
    }

}
