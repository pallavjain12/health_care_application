package com.example.demo.repository;

import com.example.demo.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient,Long>{
    Patient findPatientById(long id);

    Patient findPatientByAbhaId(String abhaId);
}
