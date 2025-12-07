ALTER TABLE `patient_visit_info` 
ADD COLUMN `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
ADD COLUMN `create_time` datetime DEFAULT NULL COMMENT '创建时间',
ADD COLUMN `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
ADD COLUMN `update_time` datetime DEFAULT NULL COMMENT '更新时间',
ADD COLUMN `remark` varchar(500) DEFAULT NULL COMMENT '备注';
