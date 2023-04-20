package com.example.demo.repository;

import com.example.demo.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findVisitByDoctor_IdAndPatient_Id(long doctor_id, long patient_id);
    List<Visit> findVisitByPatient_Id(long patient_id);

    Visit findVisitByReferenceNumber(String referenceNumber);
    Visit findVisitByRequestId (String requestId);

    Visit findVisitById(Long id);
}
