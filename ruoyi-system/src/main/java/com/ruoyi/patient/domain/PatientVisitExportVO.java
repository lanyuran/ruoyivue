package com.ruoyi.patient.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ruoyi.common.annotation.Excel;

//导出专用VO类
public class PatientVisitExportVO{

    @Excel(name="患者姓名")
    private String name;

    @Excel(name="患者性别")
    private String gender;

    @Excel(name="患者出生日期")
    private Date birthDate;

    @Excel(name="就诊时间")
    private Date visitTime;

    @Excel(name="就诊医院名称")
    private String hospital;

    @Excel(name="病历号")
    private String medicalRecordNo;

    @Excel(name="家长姓名")
    private String parentName;

    @Excel(name="联系手机号码")
    private String phone;

    @Excel(name="既往用药史")
    private String pastMedication;

    @Excel(name="主诉")
    private String chiefComplaint;

    @Excel(name="主证")
    private String mainSymptom;

    @Excel(name="共患病")
    private String comorbidity;

    @Excel(name="体格检查结果")
    private String physicalExam;

    @Excel(name="舌脉情况")
    private String tonguePulse;

    @Excel(name="过敏原检测-总IgE")
    private BigDecimal allergenTotalIge;

    @Excel(name="过敏原检测-特异性IgE")
    private BigDecimal allergenSpecificIge;

    @Excel(name="中医诊断")
    private String tcmDiagnosis;

    @Excel(name="中医治法")
    private String tcmTreatment;

    @Excel(name="中医外治处方")
    private String tcmExternalPrescription;

    // getter & setter
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getGender(){
        return gender;
    }

    public void setGender(String gender){
        this.gender=gender;
    }

    public Date getBirthDate(){
        return birthDate;
    }

    public void setBirthDate(Date birthDate){
        this.birthDate=birthDate;
    }

    public Date getVisitTime(){
        return visitTime;
    }

    public void setVisitTime(Date visitTime){
        this.visitTime=visitTime;
    }

    public String getHospital(){
        return hospital;
    }

    public void setHospital(String hospital){
        this.hospital=hospital;
    }

    public String getMedicalRecordNo(){
        return medicalRecordNo;
    }

    public void setMedicalRecordNo(String medicalRecordNo){
        this.medicalRecordNo=medicalRecordNo;
    }

    public String getParentName(){
        return parentName;
    }

    public void setParentName(String parentName){
        this.parentName=parentName;
    }

    public String getPhone(){
        return phone;
    }

    public void setPhone(String phone){
        this.phone=phone;
    }

    public String getPastMedication(){
        return pastMedication;
    }

    public void setPastMedication(String pastMedication){
        this.pastMedication=pastMedication;
    }

    public String getChiefComplaint(){
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint){
        this.chiefComplaint=chiefComplaint;
    }

    public String getMainSymptom(){
        return mainSymptom;
    }

    public void setMainSymptom(String mainSymptom){
        this.mainSymptom=mainSymptom;
    }

    public String getComorbidity(){
        return comorbidity;
    }

    public void setComorbidity(String comorbidity){
        this.comorbidity=comorbidity;
    }

    public String getPhysicalExam(){
        return physicalExam;
    }

    public void setPhysicalExam(String physicalExam){
        this.physicalExam=physicalExam;
    }

    public String getTonguePulse(){
        return tonguePulse;
    }

    public void setTonguePulse(String tonguePulse){
        this.tonguePulse=tonguePulse;
    }

    public BigDecimal getAllergenTotalIge(){
        return allergenTotalIge;
    }

    public void setAllergenTotalIge(BigDecimal allergenTotalIge){
        this.allergenTotalIge=allergenTotalIge;
    }

    public BigDecimal getAllergenSpecificIge(){
        return allergenSpecificIge;
    }

    public void setAllergenSpecificIge(BigDecimal allergenSpecificIge){
        this.allergenSpecificIge=allergenSpecificIge;
    }

    public String getTcmDiagnosis(){
        return tcmDiagnosis;
    }

    public void setTcmDiagnosis(String tcmDiagnosis){
        this.tcmDiagnosis=tcmDiagnosis;
    }

    public String getTcmTreatment(){
        return tcmTreatment;
    }

    public void setTcmTreatment(String tcmTreatment){
        this.tcmTreatment=tcmTreatment;
    }

    public String getTcmExternalPrescription(){
        return tcmExternalPrescription;
    }

    public void setTcmExternalPrescription(String tcmExternalPrescription){
        this.tcmExternalPrescription=tcmExternalPrescription;
    }
}
