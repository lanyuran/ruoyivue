-- R1: 病例号唯一约束（院区内唯一）
USE fl;

-- 执行前请先运行：sql/4_历史重复数据排查.sql
-- 若有重复数据，需先清洗再执行本脚本

ALTER TABLE patient_visit_info
  ADD UNIQUE KEY uk_hospital_medical_record_no (hospital_dept_id, medical_record_no);

