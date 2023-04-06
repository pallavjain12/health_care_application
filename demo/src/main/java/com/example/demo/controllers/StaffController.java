package com.example.demo.controllers;

import com.example.demo.model.Staff;
import com.example.demo.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StaffController {

    @Autowired
    private StaffRepository staffRepository;

    @PostMapping("/staff")
    Staff newEmployee(@RequestBody Staff newStaff){
        return staffRepository.save(newStaff);
    }

    @GetMapping("/staffs")
    List<Staff> getAllEmployees(){
        return staffRepository.findAll();
    }
}
