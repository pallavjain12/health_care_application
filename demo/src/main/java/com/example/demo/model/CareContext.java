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
    @Column
    private String prescription;
    @Column
    private String diagnosis;
    @Column
    private String dosageInstruction;
    @Column
    private String patientName;

    @Column
    private String patientId;

    @Column
    private String doctorId;

    @Column
    String doctorName;
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

    public void setPatientReference(String patientReference) {
        this.patientReference = patientReference;
    }

    public void setCareContextReference(String careContextReference) {
        this.careContextReference = careContextReference;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getDosageInstruction() {
        return dosageInstruction;
    }

    public void setDosageInstruction(String dosageInstruction) {
        this.dosageInstruction = dosageInstruction;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
