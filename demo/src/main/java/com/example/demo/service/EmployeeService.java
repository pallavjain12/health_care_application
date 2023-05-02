package com.example.demo.service;

import com.example.demo.constants.StringConstants;
import com.example.demo.helper.Service.EmployeeServiceHelper;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.common.ResponseHelper.serverSideError;

@Service
public class EmployeeService {
    Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    @Autowired
    private EmployeeRepository employeeRepository;

    public String addNewEmployee(Employee employee) {
        logger.info("EnteredAddNewEmployee with data: "+ employee);
        Employee savedEmployee = employeeRepository.save(employee);
        JSONObject obj = new JSONObject();
        obj.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
        obj.put(StringConstants.MSG, "Employee added successfully");
        obj.put(StringConstants.DATA, savedEmployee.toJSONObject());
        logger.info("exiting with data: " + obj);
        return obj.toString();
    }

    public String getEmployeeById(long id) {
        logger.info("entering with data: " + id);
        Employee employee = employeeRepository.findEmployeeById(id);
        if (employee == null) {
            return serverSideError("No Employee found with id: " + id).toString();
        }
        return employee.toString();
    }

    public String getAllEmployees() {
        logger.info("Entering getALlEmployee with no data");
        try {
            List<Employee> list = employeeRepository.findAll();
            JSONObject obj  = new JSONObject();
            JSONArray array = new JSONArray();
            for (Employee employee : list) {
                array.put(employee.toJSONObject());
            }
            obj.put("posts", array);
            return obj.toString();
        }
        catch(Exception e) {
            return serverSideError(e.toString()).toString();
        }
    }

    public String loginCredentialsCheck(String email, String password) {
        logger.info("inside loginCredentialsCheck() method");
        logger.info("received email: " + email + " and password: " + password);
        try {
            Employee employee = employeeRepository.findEmployeeByEmail(email);
            logger.info("Fetched employee : ");
            logger.info("employee object is null? " + (employee == null));
            if (employee == null) {
                return serverSideError("Invalid email or password").toString();
            }
            else {
                if (employee.isValidPassword(password)) {
                    return employee.toString();
                }
                else {
                    return serverSideError("Invalid email or password").toString();
                }
            }
        }
        catch (Exception e) {
            return serverSideError(e.toString()).toString();
        }
    }

    public String updateEmployee(JSONObject obj) {
        System.out.println(obj);
        Employee dbEmployee = employeeRepository.findEmployeeById(Long.parseLong(obj.getString("id")));
        if (!obj.isNull("email")) dbEmployee.setEmail(obj.getString("email"));
        if (!obj.isNull("name")) dbEmployee.setName(obj.getString("name"));
        if (!obj.isNull("mobile")) dbEmployee.setMobile(obj.getString("mobile"));
        if (!obj.isNull("role")) dbEmployee.setRole(obj.getString("role"));
        employeeRepository.save(dbEmployee);
        return dbEmployee.toJSONObject().toString();
    }

    public String deleteEmployee(JSONObject obj) {
        Employee dbEmployee = employeeRepository.findEmployeeById(Long.parseLong(obj.getString("id")));
        dbEmployee.setActive(false);
        employeeRepository.save(dbEmployee);
        return "Deleted successfully";
    }
}
