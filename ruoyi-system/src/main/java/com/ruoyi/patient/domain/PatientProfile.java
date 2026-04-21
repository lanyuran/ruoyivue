package com.ruoyi.patient.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 患者主档对象 patient_profile
 */
public class PatientProfile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long patientId;

    private Long userId;

    private String name;

    private String gender;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    private String parentName;

    private String phone;

    public Long getPatientId()
    {
        return patientId;
    }

    public void setPatientId(Long patientId)
    {
        this.patientId = patientId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public Date getBirthDate()
    {
        return birthDate;
    }

    public void setBirthDate(Date birthDate)
    {
        this.birthDate = birthDate;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}
