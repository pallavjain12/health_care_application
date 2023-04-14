package com.example.demo.controllers;

import com.example.demo.constants.StringConstants;
import com.example.demo.model.Employee;
import com.example.demo.service.EmployeeService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

import static com.example.demo.common.ResponseHelper.serverSideError;

@RestController()
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;
    @PostMapping("/employee")
    public String addEmployee(@RequestBody Employee employee) {
        try {
             return employeeService.addNewEmployee(employee);
        }
        catch (Exception e) {
            return serverSideError(e.toString());
        }
    }

    @GetMapping("/employee")
    public String getEmployee(@RequestBody HashMap<String, String> request) {
        try {
            return employeeService.getEmployeeById(Long.parseLong(request.get("id")));
        }
        catch (Exception e) {
            return serverSideError(e.toString());
        }
    }

    @GetMapping("/employees")
    public String getAllEmployees() {
        try {
            return employeeService.getAllEmployees();
        }
        catch (Exception e) {
            return serverSideError(e.toString());
        }
    }

    @GetMapping("/login")
    public String login(@RequestBody HashMap<String, String> map) {
        try {
            return employeeService.loginCredentialsCheck(map.get(StringConstants.EMAIL), map.get(StringConstants.PASSWORD));
        }
        catch (Exception e) {
            return serverSideError(e.toString());
        }
    }
}
