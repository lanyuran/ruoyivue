package com.ruoyi.patient.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 鼻炎患者就诊信息主（包含文档中所有字段）对象 patient_visit_info
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public class PatientVisitInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 就诊记录ID（主键） */
    private Long visitId;

    /** 患者姓名 */
    @Excel(name = "患者姓名")
    private String name;

    /** 患者性别（仅男女选项） */
    @Excel(name = "患者性别", readConverterExp = "仅=男女选项")
    private String gender;

    /** 患者出生日期（格式：YYYY-MM-DD） */
    @Excel(name = "患者出生日期", readConverterExp = "格=式：YYYY-MM-DD")
    private Date birthDate;

    /** 就诊时间（格式：YYYY-MM-DD HH:MM:SS） */
    @Excel(name = "就诊时间", readConverterExp = "格=式：YYYY-MM-DD,H=H:MM:SS")
    private Date visitTime;

    /** 就诊医院名称 */
    @Excel(name = "就诊医院名称")
    private String hospital;

    /** 病历号（唯一标识患者病历） */
    @Excel(name = "病历号", readConverterExp = "唯=一标识患者病历")
    private String medicalRecordNo;

    /** 家长姓名（儿童患者必填，成人可选） */
    @Excel(name = "家长姓名", readConverterExp = "儿=童患者必填，成人可选")
    private String parentName;

    /** 联系手机号码（用于后续随访） */
    @Excel(name = "联系手机号码", readConverterExp = "用=于后续随访")
    private String phone;

    /** 既往用药史（支持多行文本，记录历史用药情况） */
    @Excel(name = "既往用药史", readConverterExp = "支=持多行文本，记录历史用药情况")
    private String pastMedication;

    /** 主诉（患者主要症状描述） */
    @Excel(name = "主诉", readConverterExp = "患=者主要症状描述")
    private String chiefComplaint;

    /** 主证（如鼻塞、鼻痒等，多个症状用逗号分隔） */
    @Excel(name = "主证", readConverterExp = "如=鼻塞、鼻痒等，多个症状用逗号分隔")
    private String mainSymptom;

    /** 共患病（如鼻窦炎、过敏性哮喘等，多个疾病用逗号分隔） */
    @Excel(name = "共患病", readConverterExp = "如=鼻窦炎、过敏性哮喘等，多个疾病用逗号分隔")
    private String comorbidity;

    /** 体格检查结果（如鼻黏膜色红、鼻甲肿胀等） */
    @Excel(name = "体格检查结果", readConverterExp = "如=鼻黏膜色红、鼻甲肿胀等")
    private String physicalExam;

    /** 舌脉情况（记录舌质、舌苔、脉象描述） */
    @Excel(name = "舌脉情况", readConverterExp = "记=录舌质、舌苔、脉象描述")
    private String tonguePulse;

    /** 过敏原检测-总IgE（数值型，单位：IU/mL） */
    @Excel(name = "过敏原检测-总IgE", readConverterExp = "数=值型，单位：IU/mL")
    private BigDecimal allergenTotalIge;

    /** 过敏原检测-特异性IgE（数值型，单位：kUA/L） */
    @Excel(name = "过敏原检测-特异性IgE", readConverterExp = "数=值型，单位：kUA/L")
    private BigDecimal allergenSpecificIge;

    /** 中医诊断（如“鼻鼽 风寒犯肺证”） */
    @Excel(name = "中医诊断", readConverterExp = "如=“鼻鼽,风=寒犯肺证”")
    private String tcmDiagnosis;

    /** 中医治法（如“疏风散寒 宣通鼻窍”） */
    @Excel(name = "中医治法", readConverterExp = "如=“疏风散寒,宣=通鼻窍”")
    private String tcmTreatment;

    /** 中医外治处方（如香嗅疗法、耳穴压豆等具体方案） */
    @Excel(name = "中医外治处方", readConverterExp = "如=香嗅疗法、耳穴压豆等具体方案")
    private String tcmExternalPrescription;

    public void setVisitId(Long visitId) 
    {
        this.visitId = visitId;
    }

    public Long getVisitId() 
    {
        return visitId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getGender() 
    {
        return gender;
    }

    public void setBirthDate(Date birthDate) 
    {
        this.birthDate = birthDate;
    }

    public Date getBirthDate() 
    {
        return birthDate;
    }

    public void setVisitTime(Date visitTime) 
    {
        this.visitTime = visitTime;
    }

    public Date getVisitTime() 
    {
        return visitTime;
    }

    public void setHospital(String hospital) 
    {
        this.hospital = hospital;
    }

    public String getHospital() 
    {
        return hospital;
    }

    public void setMedicalRecordNo(String medicalRecordNo) 
    {
        this.medicalRecordNo = medicalRecordNo;
    }

    public String getMedicalRecordNo() 
    {
        return medicalRecordNo;
    }

    public void setParentName(String parentName) 
    {
        this.parentName = parentName;
    }

    public String getParentName() 
    {
        return parentName;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }

    public void setPastMedication(String pastMedication) 
    {
        this.pastMedication = pastMedication;
    }

    public String getPastMedication() 
    {
        return pastMedication;
    }

    public void setChiefComplaint(String chiefComplaint) 
    {
        this.chiefComplaint = chiefComplaint;
    }

    public String getChiefComplaint() 
    {
        return chiefComplaint;
    }

    public void setMainSymptom(String mainSymptom) 
    {
        this.mainSymptom = mainSymptom;
    }

    public String getMainSymptom() 
    {
        return mainSymptom;
    }

    public void setComorbidity(String comorbidity) 
    {
        this.comorbidity = comorbidity;
    }

    public String getComorbidity() 
    {
        return comorbidity;
    }

    public void setPhysicalExam(String physicalExam) 
    {
        this.physicalExam = physicalExam;
    }

    public String getPhysicalExam() 
    {
        return physicalExam;
    }

    public void setTonguePulse(String tonguePulse) 
    {
        this.tonguePulse = tonguePulse;
    }

    public String getTonguePulse() 
    {
        return tonguePulse;
    }

    public void setAllergenTotalIge(BigDecimal allergenTotalIge) 
    {
        this.allergenTotalIge = allergenTotalIge;
    }

    public BigDecimal getAllergenTotalIge() 
    {
        return allergenTotalIge;
    }

    public void setAllergenSpecificIge(BigDecimal allergenSpecificIge) 
    {
        this.allergenSpecificIge = allergenSpecificIge;
    }

    public BigDecimal getAllergenSpecificIge() 
    {
        return allergenSpecificIge;
    }

    public void setTcmDiagnosis(String tcmDiagnosis) 
    {
        this.tcmDiagnosis = tcmDiagnosis;
    }

    public String getTcmDiagnosis() 
    {
        return tcmDiagnosis;
    }

    public void setTcmTreatment(String tcmTreatment) 
    {
        this.tcmTreatment = tcmTreatment;
    }

    public String getTcmTreatment() 
    {
        return tcmTreatment;
    }

    public void setTcmExternalPrescription(String tcmExternalPrescription) 
    {
        this.tcmExternalPrescription = tcmExternalPrescription;
    }

    public String getTcmExternalPrescription() 
    {
        return tcmExternalPrescription;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("visitId", getVisitId())
            .append("name", getName())
            .append("gender", getGender())
            .append("birthDate", getBirthDate())
            .append("visitTime", getVisitTime())
            .append("hospital", getHospital())
            .append("medicalRecordNo", getMedicalRecordNo())
            .append("parentName", getParentName())
            .append("phone", getPhone())
            .append("pastMedication", getPastMedication())
            .append("chiefComplaint", getChiefComplaint())
            .append("mainSymptom", getMainSymptom())
            .append("comorbidity", getComorbidity())
            .append("physicalExam", getPhysicalExam())
            .append("tonguePulse", getTonguePulse())
            .append("allergenTotalIge", getAllergenTotalIge())
            .append("allergenSpecificIge", getAllergenSpecificIge())
            .append("tcmDiagnosis", getTcmDiagnosis())
            .append("tcmTreatment", getTcmTreatment())
            .append("tcmExternalPrescription", getTcmExternalPrescription())
            .toString();
    }
}
