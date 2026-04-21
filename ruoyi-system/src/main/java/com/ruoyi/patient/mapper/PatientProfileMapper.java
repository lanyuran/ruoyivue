package com.ruoyi.patient.mapper;

import com.ruoyi.patient.domain.PatientProfile;

public interface PatientProfileMapper
{
    PatientProfile selectPatientProfileByPatientId(Long patientId);

    PatientProfile selectPatientProfileByUserId(Long userId);

    PatientProfile selectPatientProfileByIdentity(PatientProfile patientProfile);

    int insertPatientProfile(PatientProfile patientProfile);

    int updatePatientProfile(PatientProfile patientProfile);
}
