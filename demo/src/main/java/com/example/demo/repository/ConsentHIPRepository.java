package com.example.demo.repository;

import com.example.demo.model.Consent;
import com.example.demo.model.ConsentHIP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentHIPRepository extends JpaRepository<ConsentHIP, Long> {
    ConsentHIP findConsentHIPByConsentId(String consentId);
    ConsentHIP findConsentByRequestId(String RequestId);
    ConsentHIP findConsentByTransactionId(String transactionId);
}
