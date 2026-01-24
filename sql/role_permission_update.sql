-- Add doctor application fields
ALTER TABLE sys_user
  ADD COLUMN apply_role_key varchar(100) DEFAULT NULL COMMENT '申请角色权限标识',
  ADD COLUMN apply_status char(1) DEFAULT NULL COMMENT '申请状态(0待审批 1通过 2驳回)';

-- Add hospital linkage on visit info (skip if column already exists)
ALTER TABLE patient_visit_info
  ADD COLUMN hospital_dept_id bigint(20) DEFAULT NULL COMMENT '就诊医院所属部门ID';

-- Backfill hospital_dept_id from uploader department
SET @hospital_root_id := (
  SELECT dept_id
  FROM sys_dept
  WHERE dept_name = '医院' AND parent_id = 0
  LIMIT 1
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
END
WHERE p.hospital_dept_id IS NULL OR p.hospital_dept_id = 0;

-- Ensure data upload/query menus carry list permission
UPDATE sys_menu
SET perms = 'patient:information:list'
WHERE menu_name IN ('数据上传', '数据查询')
  AND (perms IS NULL OR perms = '');

-- Add doctor approval permission (button)
INSERT INTO sys_menu (
  menu_name, parent_id, order_num, path, component, query, route_name,
  is_frame, is_cache, menu_type, visible, status, perms, icon,
  create_by, create_time, remark
)
SELECT
  '医生申请审批', m.menu_id, 99, '#', '', '', '',
  1, 0, 'F', '0', '0', 'system:user:approveDoctor', '#',
  'admin', sysdate(), '医生申请审批'
FROM sys_menu m
WHERE m.perms = 'system:user:list'
  AND NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE perms = 'system:user:approveDoctor'
  );

-- Roles (create if missing)
INSERT INTO sys_role (
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT '用户级', 'user', 10, '5', 1, 1, '0', '0', 'admin', sysdate(), '用户级'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'user');

INSERT INTO sys_role (
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT '科室医生级', 'dept_doctor', 20, '3', 1, 1, '0', '0', 'admin', sysdate(), '科室医生级'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'dept_doctor');

INSERT INTO sys_role (
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT '科室主管级', 'dept_manager', 30, '4', 1, 1, '0', '0', 'admin', sysdate(), '科室主管级'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'dept_manager');

INSERT INTO sys_role (
  role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly,
  status, del_flag, create_by, create_time, remark
)
SELECT '院区主管级', 'campus_manager', 40, '1', 1, 1, '0', '0', 'admin', sysdate(), '院区主管级'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'campus_manager');

UPDATE sys_role
SET data_scope = '1'
WHERE role_key = 'campus_manager';

-- Menu access: 用户级
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'user' AND m.menu_name = '数据上传';

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'user'
  AND m.perms IN (
    'patient:information:list',
    'patient:information:add',
    'patient:information:edit',
    'patient:information:remove',
    'patient:information:query'
  );

-- Menu access: 科室医生级
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'dept_doctor' AND m.menu_name IN ('数据上传', '数据查询');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'dept_doctor'
  AND m.perms IN (
    'patient:information:list',
    'patient:information:add',
    'patient:information:edit',
    'patient:information:remove',
    'patient:information:query'
  );

-- Menu access: 科室主管级
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'dept_manager' AND m.menu_name = '系统管理';

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'dept_manager' AND m.menu_name IN ('数据上传', '数据查询', '用户管理', '角色管理');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'dept_manager'
  AND m.perms IN (
    'patient:information:list',
    'patient:information:add',
    'patient:information:edit',
    'patient:information:remove',
    'patient:information:query',
    'system:user:list',
    'system:user:query',
    'system:user:add',
    'system:user:edit',
    'system:user:remove',
    'system:user:resetPwd',
    'system:user:import',
    'system:user:export',
    'system:user:approveDoctor',
    'system:role:list',
    'system:role:query',
    'system:role:add',
    'system:role:edit',
    'system:role:remove',
    'system:role:export'
  );

-- Menu access: 院区主管级
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'campus_manager' AND m.menu_name = '系统管理';

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'campus_manager' AND m.menu_name IN ('数据上传', '数据查询', '用户管理', '医院管理', '角色管理');

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT r.role_id, m.menu_id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'campus_manager'
  AND m.perms IN (
    'patient:information:list',
    'patient:information:add',
    'patient:information:edit',
    'patient:information:remove',
    'patient:information:query',
    'system:user:list',
    'system:user:query',
    'system:user:add',
    'system:user:edit',
    'system:user:remove',
    'system:user:resetPwd',
    'system:user:import',
    'system:user:export',
    'system:user:approveDoctor',
    'system:role:list',
    'system:role:query',
    'system:role:add',
    'system:role:edit',
    'system:role:remove',
    'system:role:export',
    'system:dept:list',
    'system:dept:query',
    'system:dept:add',
    'system:dept:edit',
    'system:dept:remove'
  );
