package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String prescription;
    @Column
    private String dosageInstruction;
    @Column
    private String diagnosis;
    @Column
    private LocalDate visitDate;
    @Column
    private String referenceNumber;
    @Column
    private String display;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;
    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Employee doctor;

    private String requestId;

    public Visit(LocalDate visitDate, String referenceNumber, String display) {
        this.visitDate = visitDate;
        this.display = display;
        this.referenceNumber = referenceNumber;
    }

    public Visit() {}

    public Patient getPatient() { return patient; }

    public void setPatient(Patient patient) { this.patient = patient; }

    public Employee getDoctor() { return doctor; }

    public void setDoctor(Employee doctor) { this.doctor = doctor; }

    public long getId() { return id; }

    public String getPrescription() { return prescription; }

    public LocalDate getVisitDate() { return visitDate; }

    public String getReferenceNumber() { return referenceNumber; }

    public String getDisplay() { return display; }

    public String getRequestId() { return requestId; }

    public void setRequestId(String requestId) { this.requestId = requestId; }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getDosageInstruction() {
        return dosageInstruction;
    }

    public void setDosageInstruction(String dosageInstruction) {
        this.dosageInstruction = dosageInstruction;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
}
