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
    @Column
    private String purpose;
    @Column(nullable = false)
    private LocalDate consentExpiryDateTime;

    @Column(nullable = false)
    private LocalDate healthInfoFromDate;

    @Column(nullable = false)
    private LocalDate healthInfoToDate;

    @Column
    private String status;
}
