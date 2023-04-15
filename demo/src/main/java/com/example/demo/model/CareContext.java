package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CareContext {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    private String patientReference;
    private String careContextReference;

    public String getPatientReference() { return patientReference; }

    public String getCareContextReference() { return careContextReference; }

    public CareContext(String patientReference, String careContextReference) {
        this.careContextReference = careContextReference;
        this.patientReference = patientReference;
    }

    public CareContext() {}
}
