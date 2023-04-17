package com.example.demo.repository;

import com.example.demo.model.ConsentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRequestRepository extends JpaRepository<ConsentRequest, Long> {
    ConsentRequest findConsentRequestByRequestId(String requestId);
    ConsentRequest findConsentRequestByConsentRequestId(String consentRequestId);
}
