package com.example.demo.model;

import jakarta.persistence.*;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id")
    private Patient patient;
    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private Employee doctor;

    @Column
    private String requestId;
    @Column
    private boolean isDisabled = false;

    @OneToMany(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    List<ConsentRequest> consentRequestList = new ArrayList<>();

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

    public String toString() {
        return "\nprescription: " + prescription+
                "\ndosageInstruction: " + dosageInstruction+
                "\ndiagnosis: " + diagnosis+
                "\nvisitdate: " + visitDate.toString()+
                "\nreferenceNumber: " + referenceNumber+
                "\ndisplay: " + display +
                "\nRequestId: " + requestId +
                "\nisDisabled: " + isDisabled;

    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("id", "" + id);
        obj.put("prescription", prescription);
        obj.put("diagnosis", diagnosis);
        obj.put("dosageInstruction", dosageInstruction);
        obj.put("visitDate", visitDate);
        obj.put("referenceNumber", referenceNumber);
        obj.put("display", display);
        obj.put("isDisabled", isDisabled);
        return obj;
    }

    public List<ConsentRequest> getConsentRequestList() {
        return consentRequestList;
    }

    public void setConsentRequestList(List<ConsentRequest> consentRequestList) {
        this.consentRequestList = consentRequestList;
    }

    public void addConsentRequest(ConsentRequest consentRequest) {
        consentRequest.setVisit(this);
        this.consentRequestList.add(consentRequest);
    }
}
