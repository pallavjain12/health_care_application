package com.example.demo.constants;

public class Enum {
    enum role {
        admin,
        doctor,
        frontdesk
    }

    enum purpose {

    }

    enum accessModes {
        VIEW,
        STORE,
        QUERY,
        STREAM
    }

    enum reports{
        DiagnosticReport,
        Prescription,
        ImmunizationRecord,
        DischargeSummary,
        OPConsultation,
        HealthDocumentRecord,
        WellnessRecord
    }
}
