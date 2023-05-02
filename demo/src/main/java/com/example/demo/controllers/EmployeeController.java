package com.example.demo.controllers;

import com.example.demo.constants.StringConstants;
import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import jakarta.persistence.Column;
import jdk.jfr.ContentType;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import static com.example.demo.common.ResponseHelper.serverSideError;

@RestController
@CrossOrigin
public class EmployeeController {

    Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    @Autowired
    EmployeeService employeeService;
    @PostMapping("/employee")
    @CrossOrigin
    public String addEmployee(@RequestBody Employee employee) {
        logger.info("Entering addEmployee with data: " + employee.toString());
        try {
             return employeeService.addNewEmployee(employee);
        }
        catch (Exception e) {
            logger.error("Error occured while adding employee: " + e);
            return serverSideError(e.toString()).toString();
        }
    }

    @GetMapping("/employee")
    @CrossOrigin
    public String getEmployee(@RequestBody HashMap<String, String> request) {
        logger.info("Entering getEmployee with data: " + request);
        try {
            return employeeService.getEmployeeById(Long.parseLong(request.get("id")));
        }
        catch (Exception e) {
            logger.error("Error occurred while getting employee: " + e);
            return serverSideError(e.toString()).toString();
        }
    }

    @GetMapping(value = "/employees", produces = "Application/json")
    @CrossOrigin
    public String getAllEmployees() {
        logger.info("Entering getAllEmployees with no data");
        try {
            return employeeService.getAllEmployees();
        }
        catch (Exception e) {
            logger.error("error occurred while getting employees: " + e);
            return serverSideError(e.toString()).toString();
        }
    }

    @PostMapping(value = "/login", produces = "application/json")
    @CrossOrigin
    public String login(@RequestBody HashMap<String, String> map) {
        logger.info("Entering login with data: " + map);
        try {
            return employeeService.loginCredentialsCheck(map.get(StringConstants.EMAIL), map.get(StringConstants.PASSWORD));
        }
        catch (Exception e) {
            return serverSideError(e.toString()).toString();
        }
    }

    @PutMapping(value = "/employee", produces = "Application/JSON")
    public String editEmployee(@RequestBody String obj) {
        return employeeService.updateEmployee(new JSONObject(obj));
    }

    @DeleteMapping(value = "/employee")
    public String deleteEmployee(@RequestBody String str) {
        return employeeService.deleteEmployee(new JSONObject(str));
    }
}
