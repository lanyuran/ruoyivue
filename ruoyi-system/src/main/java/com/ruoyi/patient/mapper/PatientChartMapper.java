package com.ruoyi.patient.mapper;

import com.ruoyi.patient.domain.PatientChart;

public interface PatientChartMapper
{
    PatientChart selectPatientChartByChartId(Long chartId);

    PatientChart selectPatientChartByHospitalRecord(PatientChart patientChart);

    PatientChart selectPatientChartByPatientHospital(PatientChart patientChart);

    int insertPatientChart(PatientChart patientChart);

    int updatePatientChart(PatientChart patientChart);
}
