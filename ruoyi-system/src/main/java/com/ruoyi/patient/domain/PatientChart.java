package com.ruoyi.patient.domain;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 患者病历映射对象 patient_chart
 */
public class PatientChart extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long chartId;

    private Long patientId;

    private Long hospitalDeptId;

    private String hospital;

    private String medicalRecordNo;

    public Long getChartId()
    {
        return chartId;
    }

    public void setChartId(Long chartId)
    {
        this.chartId = chartId;
    }

    public Long getPatientId()
    {
        return patientId;
    }

    public void setPatientId(Long patientId)
    {
        this.patientId = patientId;
    }

    public Long getHospitalDeptId()
    {
        return hospitalDeptId;
    }

    public void setHospitalDeptId(Long hospitalDeptId)
    {
        this.hospitalDeptId = hospitalDeptId;
    }

    public String getHospital()
    {
        return hospital;
    }

    public void setHospital(String hospital)
    {
        this.hospital = hospital;
    }

    public String getMedicalRecordNo()
    {
        return medicalRecordNo;
    }

    public void setMedicalRecordNo(String medicalRecordNo)
    {
        this.medicalRecordNo = medicalRecordNo;
    }
}
