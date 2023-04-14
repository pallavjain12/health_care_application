package com.example.demo.model;

import jakarta.persistence.*;
import org.json.JSONObject;

import java.time.Period;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String abhaId;

    @Column(unique = true)
    private String abhaNumber;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String mobile;

    @Column
    private String dateOfBirth;

    @Column
    private String monthOfBirth;

    @Column
    private String yearOfBirth;

    @Column(nullable = false)
    private String gender;
    @Column
    private LocalDate registrationDateTime;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Visit> visits = new ArrayList<>();

    public long getId() { return id; }

    public String getName() { return name; }

    public String getAbhaId() { return abhaId; }

    public String getAbhaNumber() { return abhaNumber; }

    public String getEmail() { return email; }

    public String getMobile() { return mobile; }

    public String getDateOfBirth() { return dateOfBirth; }

    public String getMonthOfBirth() { return monthOfBirth; }

    public String getYearOfBirth() { return yearOfBirth; }

    public String getGender() { return gender; }

    public LocalDate getRegistrationDateTime() { return registrationDateTime; }
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAbhaId(String abhaId) { this.abhaId = abhaId; }
    public void setAbhaNumber(String abhaNumber) { this.abhaNumber = abhaNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setMonthOfBirth(String monthOfBirth) { this.monthOfBirth = monthOfBirth; }
    public void setYearOfBirth(String yearOfBirth) { this.yearOfBirth = yearOfBirth; }
    public void setGender(String gender) { this.gender = gender; }
    public String getDOBString() {
        return this.dateOfBirth + "/" + this.monthOfBirth + "/" + this.yearOfBirth;
    }

    public int getAge() {
        String input = this.yearOfBirth + "-" + this.monthOfBirth + "-" + this.dateOfBirth;
        LocalDate dob = LocalDate.parse(input);
        return calculateAge(dob);
    }
    public static int calculateAge(LocalDate dob)  {
        LocalDate curDate = LocalDate.now();
        if ((dob != null))  {
            return Period.between(dob, curDate).getYears();
        }
        else {
            return 0;
        }
    }

    public void setRegistrationDateTime(LocalDate registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public List<Visit> getVisits() {
        return visits;
    }

    public void addVisits(Visit visit) {
        this.visits.add(visit);
    }

    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("name", name);
        obj.put("abha_id", abhaId);
        obj.put("abha_number", abhaNumber);
        obj.put("age", getAge());
        obj.put("gender", gender);
        return obj.toString();
    }
}


































































/*
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String gender;
    private int yearOfBirth;
    private int monthOfBirth;
    private int dayOfBirth;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public int getMonthOfBirth() {
        return monthOfBirth;
    }

    public void setMonthOfBirth(int monthOfBirth) {
        this.monthOfBirth = monthOfBirth;
    }

    public int getDayOfBirth() {
        return dayOfBirth;
    }

    public void setDayOfBirth(int dayOfBirth) {
        this.dayOfBirth = dayOfBirth;
    }
 */