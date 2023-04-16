package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class CareContext {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    @Column
    private String patientReference;
    @Column
    private String careContextReference;
    @Column
    private String data;
    @Column
    private String encryptedData;
    @Column
    private String checkSum;
    public String getPatientReference() { return patientReference; }

    public String getCareContextReference() { return careContextReference; }

    public CareContext(String patientReference, String careContextReference) {
        this.careContextReference = careContextReference;
        this.patientReference = patientReference;
    }

    public CareContext() {}

    public String getData() { return data; }

    public void setData(String data) { this.data = data; }

    public String getEncryptedData() { return encryptedData; }

    public void setEncryptedData(String encryptedData) { this.encryptedData = encryptedData; }

    public String getCheckSum() { return checkSum; }

    public void setCheckSum(String checkSum) { this.checkSum = checkSum; }
}
