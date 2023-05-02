package com.example.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.json.JSONObject;

@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String mobile;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    @ColumnDefault("true")
    private boolean active;

    @Column(unique = true)
    private String registrationNumber;

    public boolean isValidPassword(String password) {
        return password.equals(this.password);
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public String getMobile() { return mobile; }

    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setName(String name) { this.name = name; }

    public void setMobile(String mobile) { this.mobile = mobile; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setRole(String role) { this.role = role; }

    public void setActive(boolean active) { active = active; }

    public String toString() {
        JSONObject obj  = new JSONObject();
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("mobile", getMobile());
        obj.put("email", getEmail());
        obj.put("type", getRole());
        obj.put("registrationNumber", getRegistrationNumber());
        return obj.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject employee = new JSONObject();
        employee.put("id", "" + getId());
        employee.put("name", getName());
        employee.put("email", getName());
        employee.put("role", getRole());
        employee.put("mobile", "" + getMobile());
        employee.put("registrationNumber", getRegistrationNumber());
        return employee;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
