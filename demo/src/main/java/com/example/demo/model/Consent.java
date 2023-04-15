package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Consent {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private long id;
    @Column
    private String purpose;
    @Column(nullable = false)
    private LocalDateTime consentExpiryDateTime;
    @Column(nullable = false)
    private LocalDateTime healthInfoFromDate;
    @Column(nullable = false)
    private LocalDateTime healthInfoUpToDate;

    @Column(nullable = false)
    private LocalDateTime dataExpiryDate;
    @Column
    private String status;

    private String purposeCode;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;
    @Column
    private String consentId;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "consent_id", referencedColumnName = "id")
    private List<CareContext> careContextList = new ArrayList<>();
    @Column
    private String hiTypes;
    @Column
    private String signature;
    @Column
    private String accessMode;
    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getPurpose() { return purpose; }

    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDateTime getConsentExpiryDateTime() { return consentExpiryDateTime; }

    public void setConsentExpiryDateTime(LocalDateTime consentExpiryDateTime) { this.consentExpiryDateTime = consentExpiryDateTime; }

    public LocalDateTime getHealthInfoFromDate() { return healthInfoFromDate; }

    public void setHealthInfoFromDate(LocalDateTime healthInfoFromDate) { this.healthInfoFromDate = healthInfoFromDate; }

    public LocalDateTime getHealthInfoUpToDate() { return healthInfoUpToDate; }

    public void setHealthInfoUpToDate(LocalDateTime healthInfoUpToDate) { this.healthInfoUpToDate = healthInfoUpToDate; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Patient getPatient() { return patient; }

    public void setPatient(Patient patient) { this.patient = patient; }

    public String getConsentId() { return consentId; }

    public void setConsentId(String consentId) { this.consentId = consentId; }

    public List<CareContext> getCareContextList() { return careContextList; }

    public void addCareContext(CareContext careContext) { this.careContextList.add(careContext); }

    public String getHiTypes() { return hiTypes; }

    public void setHiTypes(String hiTypes) { this.hiTypes = hiTypes; }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setPurposeCode(String purposeCode) { this.purposeCode = purposeCode; }

    public String getPurposeCode() {
        return purposeCode;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public LocalDateTime getDataExpiryDate() {
        return dataExpiryDate;
    }

    public void setDataExpiryDate(LocalDateTime dataExpiryDate) {
        this.dataExpiryDate = dataExpiryDate;
    }
}
