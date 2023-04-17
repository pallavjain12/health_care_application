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
    private String status;
    @Column
    private String consentId;
    @Column
    private String hiTypes;
    @Column
    private String signature;
    @Column
    private String accessMode;
    @Column
    private String transactionId;
    @Column
    private String receiverPublicKey;
    @Column
    private String receiverPrivateKey;
    @Column
    private String receiverNonce;
    @Column
    private String requestId;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "consent_id", referencedColumnName = "consentId")
    List<CareContext> careContextList = new ArrayList<>();

    @Column
    private String dataFrom;

    @Column
    private String dataTo;

    @Column
    private String dataEraseAt;
    @Column
    private String patientReferenceWhenSendingData;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public String getDataTo() {
        return dataTo;
    }

    public void setDataTo(String dataTo) {
        this.dataTo = dataTo;
    }

    public String getDataEraseAt() {
        return dataEraseAt;
    }

    public void setDataEraseAt(String dataEraseAt) {
        this.dataEraseAt = dataEraseAt;
    }

    public String getReceiverPublicKey() {
        return receiverPublicKey;
    }

    public void setReceiverPublicKey(String receiverPublicKey) {
        this.receiverPublicKey = receiverPublicKey;
    }

    public String getReceiverPrivateKey() {
        return receiverPrivateKey;
    }

    public void setReceiverPrivateKey(String receiverPrivateKey) {
        this.receiverPrivateKey = receiverPrivateKey;
    }

    public String getReceiverNonce() {
        return receiverNonce;
    }

    public void setReceiverNonce(String receiverNonce) {
        this.receiverNonce = receiverNonce;
    }

    public String getPatientReferenceWhenSendingData() {
        return patientReferenceWhenSendingData;
    }

    public void setPatientReferenceWhenSendingData(String patientReferenceWhenSendingData) {
        this.patientReferenceWhenSendingData = patientReferenceWhenSendingData;
    }
}
