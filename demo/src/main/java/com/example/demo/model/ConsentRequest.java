package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class ConsentRequest {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true)
    private String requestId;

    @Column(nullable = false, unique = true)
    private String consentRequestId;
    @Column
    private String purpose;
    @Column(nullable = false)
    private LocalDate consentExpiryDateTime;

    @Column(nullable = false)
    private LocalDate healthInfoFromDate;

    @Column(nullable = false)
    private LocalDate healthInfoUpToDate;

    @Column
    private LocalDate dataExpiryDate;
    @Column
    private String status;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Employee doctor;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;

    private String consentArtifactsId;
    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getRequestId() { return requestId; }

    public void setRequestId(String requestId) { this.requestId = requestId; }

    public String getConsentRequestId() { return consentRequestId; }

    public void setConsentRequestId(String consentRequestId) { this.consentRequestId = consentRequestId; }

    public String getPurpose() { return purpose; }

    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDate getConsentExpiryDateTime() { return consentExpiryDateTime; }

    public void setConsentExpiryDateTime(LocalDate consentExpiryDateTime) { this.consentExpiryDateTime = consentExpiryDateTime; }

    public LocalDate getHealthInfoFromDate() { return healthInfoFromDate; }

    public void setHealthInfoFromDate(LocalDate healthInfoFromDate) { this.healthInfoFromDate = healthInfoFromDate; }

    public LocalDate getHealthInfoUpToDate() { return healthInfoUpToDate; }

    public void setHealthInfoUpToDate(LocalDate healthInfoUpToDate) { this.healthInfoUpToDate = healthInfoUpToDate; }

    public LocalDate getDataExpiryDate() { return dataExpiryDate; }

    public void setDataExpiryDate(LocalDate dataExpiryDate) { this.dataExpiryDate = dataExpiryDate; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Employee getDoctor() { return doctor; }

    public void setDoctor(Employee doctor) { this.doctor = doctor; }

    public Patient getPatient() { return patient; }

    public void setPatient(Patient patient) { this.patient = patient; }

    public String getConsentArtifactsId() { return consentArtifactsId; }

    public void setConsentArtifactsId(String consentArtifactsId) { this.consentArtifactsId = consentArtifactsId; }
}
