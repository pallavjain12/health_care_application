package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class ConsentRequest {
    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    private long id;
    @Column
    private String purpose;
    @Column
    private String purposeCode;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Employee doctor;
    @Column
    private String hiTypes;
    @Column
    private String accessMode;
    @Column(nullable = false)
    private String dateFrom;
    @Column(nullable = false)
    private String dateTo;
    @Column(nullable = false)
    private String dataEraseAt;
    @Column(nullable = false, unique = true)
    private String consentRequestId;
    @Column
    private String status;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "consent_request_id", referencedColumnName = "id")
    List<Consent> consentList = new ArrayList<>();
    @Column
    private String requestId;
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getConsentRequestId() { return consentRequestId; }
    public void setConsentRequestId(String consentRequestId) { this.consentRequestId = consentRequestId; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Employee getDoctor() { return doctor; }
    public void setDoctor(Employee doctor) { this.doctor = doctor; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getHiTypes() {
        return hiTypes;
    }

    public void setHiTypes(String hiTypes) {
        this.hiTypes = hiTypes;
    }

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getDataEraseAt() {
        return dataEraseAt;
    }

    public void setDataEraseAt(String dataEraseAt) {
        this.dataEraseAt = dataEraseAt;
    }

    public List<Consent> getConsentList() {
        return consentList;
    }

    public void addConsent(Consent consent) {
        this.consentList.add(consent);
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String toString() {
        return "\nid: " + id+
                "\npurpose: " +purpose +
                "\npurposeCode: " +purposeCode +
                "\npatient: " + patient+
                "\ndoctor: " +doctor +
                "\nhiTypes: " + hiTypes+
                "\naccessMode: " + accessMode+
                "\ndataEraseAt: " + dataEraseAt+
                "\ndateFrom: " + dateFrom+
                "\ndateTo: " + dateTo+
                "\nconsentRequestId: " +consentRequestId +
                "\nStatus: " + status +
                "\nconsentList: " + Arrays.toString(consentList.toArray()) +
                "\nrequestId: " + requestId;
    }
}
