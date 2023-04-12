package com.example.demo.model;

import jakarta.persistence.*;
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
    private boolean isActive;

    public boolean isValidPassword(String password) {
        return password == this.password;
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

    public void setActive(boolean active) { isActive = active; }

    public String toString() {
        JSONObject obj  = new JSONObject();
        obj.put("id", getId());
        obj.put("name", getName());
        obj.put("mobile", getMobile());
        obj.put("type", getRole());
        return obj.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject employee = new JSONObject();
        employee.put("id", id);
        employee.put("name", name);
        employee.put("email", email);
        employee.put("role", role);
        employee.put("mobile", mobile);
        return employee;
    }
}
