package com.example.demo.controllers;

import com.example.demo.model.Staff;
import com.example.demo.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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

    @GetMapping("/getStaff")
    Staff getStaff(@RequestBody HashMap<String, String> map) {
        System.out.println(map);
        System.out.flush();
        Staff temp = new Staff();
        temp.setId(Long.parseLong(map.get("id")));
        return staffRepository.getReferenceById(temp.getId());
    }
}
