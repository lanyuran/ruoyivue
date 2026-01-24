package com.ruoyi.patient.domain;

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
 * @date 2025-11-15
 */
public class PatientVisitInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 0.序号（主键） */
    private Long visitId;

    /** 1、姓名 */
    @Excel(name = "1、姓名")
    private String name;

    /** 2、性别 */
    @Excel(name = "2、性别")
    private String gender;

    /** 3、出生日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "3、出生日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date birthDate;

    /** 4、就诊时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "4、就诊时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date visitTime;

    /** 5、就诊医院 */
    @Excel(name = "5、就诊医院")
    private String hospital;

    /** 就诊医院所属部门ID */
    private Long hospitalDeptId;

    /** 6、病历号 */
    @Excel(name = "6、病历号")
    private String medicalRecordNo;

    /** 7、家长姓名 */
    @Excel(name = "7、家长姓名")
    private String parentName;

    /** 8、手机号码 */
    @Excel(name = "8、手机号码")
    private String phone;

    /** 9、既往用药 */
    @Excel(name = "9、既往用药")
    private String pastMedication;

    /** 10、主诉 */
    @Excel(name = "10、主诉")
    private String chiefComplaint;

    /** 11、主证 */
    @Excel(name = "11、主证")
    private String mainSymptom;

    /** 12、共患病 */
    @Excel(name = "12、共患病")
    private String comorbidity;

    /** 13、体格检查  */
    @Excel(name = "13、体格检查 ")
    private String physicalExam;

    /** 14、舌脉 */
    @Excel(name = "14、舌脉")
    private String tonguePulse;

    /** 15、请上传自然光线下舌象照片 */
    @Excel(name = "15、请上传自然光线下舌象照片")
    private String tongueImagePath;

    /** 16、过敏原检测 总IgE（IU/ml） */
    @Excel(name = "16、过敏原检测 总IgE", readConverterExp = "I=U/ml")
    private String allergenTotalIge;

    /** 17、过敏原检测 特异性IgE（IU/ml） */
    @Excel(name = "17、过敏原检测 特异性IgE", readConverterExp = "I=U/ml")
    private String allergenSpecificIge;

    /** 18、血常规     RBC;WBC;PLT;Hb;EOS */
    @Excel(name = "18、血常规     RBC;WBC;PLT;Hb;EOS")
    private String bloodTestImagePath;

    /** 19、炎症因子 IL-4；IL-6；IL-10；TNF-α */
    @Excel(name = "19、炎症因子 IL-4；IL-6；IL-10；TNF-α")
    private String inflammationImagePath;

    /** 20、肝肾功能 Scr；Bun；ALT；AST；TBIL */
    @Excel(name = "20、肝肾功能 Scr；Bun；ALT；AST；TBIL")
    private String liverKidneyImagePath;

    /** 21、肾早期损伤 尿微量蛋白；尿肌酐；β2-微球蛋白；微量蛋白/尿肌酐 */
    @Excel(name = "21、肾早期损伤 尿微量蛋白；尿肌酐；β2-微球蛋白；微量蛋白/尿肌酐")
    private String renalInjuryImagePath;

    /** 22、中医诊断 */
    @Excel(name = "22、中医诊断")
    private String tcmDiagnosis;

    /** 23、中医治法 */
    @Excel(name = "23、中医治法")
    private String tcmTreatment;

    /** 24、中药处方 */
    @Excel(name = "24、中药处方")
    private String tcmTreatmentImagePath;

    /** 25、中医外治处方 */
    @Excel(name = "25、中医外治处方")
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

    public void setHospitalDeptId(Long hospitalDeptId)
    {
        this.hospitalDeptId = hospitalDeptId;
    }

    public Long getHospitalDeptId()
    {
        return hospitalDeptId;
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

    public void setTongueImagePath(String tongueImagePath)
    {
        this.tongueImagePath = tongueImagePath;
    }

    public String getTongueImagePath()
    {
        return tongueImagePath;
    }

    public void setAllergenTotalIge(String allergenTotalIge)
    {
        this.allergenTotalIge = allergenTotalIge;
    }

    public String getAllergenTotalIge()
    {
        return allergenTotalIge;
    }

    public void setAllergenSpecificIge(String allergenSpecificIge)
    {
        this.allergenSpecificIge = allergenSpecificIge;
    }

    public String getAllergenSpecificIge()
    {
        return allergenSpecificIge;
    }

    public void setBloodTestImagePath(String bloodTestImagePath)
    {
        this.bloodTestImagePath = bloodTestImagePath;
    }

    public String getBloodTestImagePath()
    {
        return bloodTestImagePath;
    }

    public void setInflammationImagePath(String inflammationImagePath)
    {
        this.inflammationImagePath = inflammationImagePath;
    }

    public String getInflammationImagePath()
    {
        return inflammationImagePath;
    }

    public void setLiverKidneyImagePath(String liverKidneyImagePath)
    {
        this.liverKidneyImagePath = liverKidneyImagePath;
    }

    public String getLiverKidneyImagePath()
    {
        return liverKidneyImagePath;
    }

    public void setRenalInjuryImagePath(String renalInjuryImagePath)
    {
        this.renalInjuryImagePath = renalInjuryImagePath;
    }

    public String getRenalInjuryImagePath()
    {
        return renalInjuryImagePath;
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

    public void setTcmTreatmentImagePath(String tcmTreatmentImagePath)
    {
        this.tcmTreatmentImagePath = tcmTreatmentImagePath;
    }

    public String getTcmTreatmentImagePath()
    {
        return tcmTreatmentImagePath;
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
                .append("hospitalDeptId", getHospitalDeptId())
                .append("medicalRecordNo", getMedicalRecordNo())
                .append("parentName", getParentName())
                .append("phone", getPhone())
                .append("pastMedication", getPastMedication())
                .append("chiefComplaint", getChiefComplaint())
                .append("mainSymptom", getMainSymptom())
                .append("comorbidity", getComorbidity())
                .append("physicalExam", getPhysicalExam())
                .append("tonguePulse", getTonguePulse())
                .append("tongueImagePath", getTongueImagePath())
                .append("allergenTotalIge", getAllergenTotalIge())
                .append("allergenSpecificIge", getAllergenSpecificIge())
                .append("bloodTestImagePath", getBloodTestImagePath())
                .append("inflammationImagePath", getInflammationImagePath())
                .append("liverKidneyImagePath", getLiverKidneyImagePath())
                .append("renalInjuryImagePath", getRenalInjuryImagePath())
                .append("tcmDiagnosis", getTcmDiagnosis())
                .append("tcmTreatment", getTcmTreatment())
                .append("tcmTreatmentImagePath", getTcmTreatmentImagePath())
                .append("tcmExternalPrescription", getTcmExternalPrescription())
                .toString();
    }
}
