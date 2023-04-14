package com.example.demo.service;

import com.example.demo.constants.StringConstants;
import com.example.demo.helper.Service.EmployeeServiceHelper;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.demo.common.ResponseHelper.serverSideError;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public String addNewEmployee(Employee employee) {
        Employee savedEmployee = employeeRepository.save(employee);
        JSONObject obj = new JSONObject();
        obj.put(StringConstants.STATUS, StringConstants.SUCCESSFULL);
        obj.put(StringConstants.MSG, "Employee added successfully");
        obj.put(StringConstants.DATA, savedEmployee.toString());
        return obj.toString();
    }

    public String getEmployeeById(long id) {
        Employee employee = employeeRepository.findEmployeeById(id);
        if (employee == null) {
            return serverSideError("No Employee found with id: " + id);
        }
        return employee.toString();
    }

    public String getAllEmployees() {
        try {
            List<Employee> list = employeeRepository.findAll();
            return EmployeeServiceHelper.convertToJSONSrtByRole(list);
        }
        catch(Exception e) {
            return serverSideError(e.toString());
        }
    }

    public String loginCredentialsCheck(String email, String password) {
        try {
            Employee employee = employeeRepository.findEmployeeByEmail(email);
            System.out.println("check answer");
            System.out.println(employee == null);
            if (employee == null) {
                System.out.println(employee.toJSONObject());
                return serverSideError("Invalid email or password");
            }
            else {
                System.out.println(employee.getEmail());
                if (employee.isValidPassword(password)) {
                    return employee.toString();
                }
                else {
                    return serverSideError("Invalid email or password");
                }
            }
        }
        catch (Exception e) {
            return serverSideError(e.toString());
        }
    }
}
