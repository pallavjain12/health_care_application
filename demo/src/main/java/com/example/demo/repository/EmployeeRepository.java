package com.example.demo.repository;

import com.example.demo.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Employee findEmployeeById(long id);
    Employee findEmployeeByEmail(String email);
    List<Employee> findAll();

    List<Employee> findEmployeesByActiveIsTrue();

    Employee findEmployeesByRegistrationNumber(String registrationNumber);
}