-- R1: 历史重复病例号排查（院区内唯一策略）
USE fl;

-- 1) 重复分组统计
SELECT
  hospital_dept_id,
  medical_record_no,
  COUNT(*) AS duplicate_count
FROM patient_visit_info
WHERE medical_record_no IS NOT NULL
  AND TRIM(medical_record_no) <> ''
GROUP BY hospital_dept_id, medical_record_no
HAVING COUNT(*) > 1
ORDER BY duplicate_count DESC, hospital_dept_id, medical_record_no;

-- 2) 重复明细
SELECT p.*
FROM patient_visit_info p
JOIN (
  SELECT hospital_dept_id, medical_record_no
  FROM patient_visit_info
  WHERE medical_record_no IS NOT NULL
    AND TRIM(medical_record_no) <> ''
  GROUP BY hospital_dept_id, medical_record_no
  HAVING COUNT(*) > 1
) d
  ON p.hospital_dept_id <=> d.hospital_dept_id
 AND p.medical_record_no = d.medical_record_no
ORDER BY p.hospital_dept_id, p.medical_record_no, p.visit_id;

