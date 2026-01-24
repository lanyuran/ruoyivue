-- Hospital scope diagnostics + fixes
-- Use: open and run the sections you need.

-- 1) Check doctor account role + dept
-- Set this before running the query.
SET @doctor_user := 'doctor_username';
SELECT u.user_id,u.user_name,u.dept_id,d.dept_name,r.role_key
FROM sys_user u
LEFT JOIN sys_user_role ur ON u.user_id=ur.user_id
LEFT JOIN sys_role r ON ur.role_id=r.role_id
LEFT JOIN sys_dept d ON u.dept_id=d.dept_id
WHERE u.user_name=@doctor_user;

-- 2) Check hospital root dept
SELECT dept_id,dept_name,parent_id,ancestors
FROM sys_dept
WHERE dept_name='医院' AND parent_id=0;

-- 3) Check hospital_dept_id distribution
SELECT p.hospital_dept_id,d.dept_name,COUNT(*) cnt
FROM patient_visit_info p
LEFT JOIN sys_dept d ON p.hospital_dept_id=d.dept_id
GROUP BY p.hospital_dept_id,d.dept_name;

-- 4) Backfill hospital_dept_id by hospital name (if hospital name matches dept name)
SET @hospital_root_id := (
  SELECT dept_id FROM sys_dept WHERE dept_name='医院' AND parent_id=0 LIMIT 1
);
UPDATE patient_visit_info p
JOIN sys_dept d ON d.dept_name = p.hospital AND d.parent_id = @hospital_root_id
SET p.hospital_dept_id = d.dept_id;

-- 5) Backfill hospital_dept_id by uploader dept tree
SET @hospital_root_id := (
  SELECT dept_id FROM sys_dept WHERE dept_name='医院' AND parent_id=0 LIMIT 1
);
UPDATE patient_visit_info p
JOIN sys_user u ON p.create_by = u.user_name
JOIN sys_dept d ON u.dept_id = d.dept_id
SET p.hospital_dept_id = CASE
  WHEN @hospital_root_id IS NOT NULL AND d.dept_id = @hospital_root_id THEN d.dept_id
  WHEN @hospital_root_id IS NOT NULL AND d.parent_id = @hospital_root_id THEN d.dept_id
  WHEN @hospital_root_id IS NOT NULL AND FIND_IN_SET(@hospital_root_id, d.ancestors) > 0 THEN
    CAST(
      SUBSTRING_INDEX(
        SUBSTRING_INDEX(CONCAT(d.ancestors, ','), CONCAT(',', @hospital_root_id, ','), -1),
        ',', 1
      ) AS UNSIGNED
    )
  WHEN d.ancestors IS NOT NULL AND d.ancestors <> '' AND d.ancestors <> '0' THEN
    CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(d.ancestors, ',', 2), ',', -1) AS UNSIGNED)
  ELSE d.dept_id
END;

-- 6) Ensure System Management menu is bound to dept_manager + campus_manager
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT role_id, 1
FROM sys_role
WHERE role_key IN ('dept_manager','campus_manager');
