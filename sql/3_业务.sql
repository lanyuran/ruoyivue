-- 用途: 业务表（患者信息、就诊主表、问卷；不含示例数据）
USE fl;
SET NAMES utf8mb4 COLLATE utf8mb4_general_ci;
# MySQL 8.4 LTS 

####################################
######## TABLE patient_info ########
####################################

DROP TABLE IF EXISTS `patient_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;

CREATE TABLE `patient_info` (
        `patient_id`        int(11) NOT NULL AUTO_INCREMENT,
        `name`              varchar(50) NOT NULL,
        `gender`            enum('男','女') NOT NULL,
        `birth_date`        date NOT NULL,
        `visit_time`        datetime NOT NULL,
        `hospital`          varchar(100) NOT NULL,
        `medical_record_no` varchar(50) NOT NULL,
        `parent_name`       varchar(50) NOT NULL,
        `phone`             varchar(20) NOT NULL,
        `past_medication`   text,
        `chief_complaint`   text NOT NULL,
        PRIMARY KEY (`patient_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

/*!40101 SET character_set_client = @saved_cs_client */;

######## TABLE patient_info end ########

################################################################
######## Table structure for table `patient_visit_info` ########
################################################################

DROP TABLE IF EXISTS `patient_visit_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;

CREATE TABLE `patient_visit_info` (
      `visit_id`                  INT(11) NOT NULL AUTO_INCREMENT COMMENT '0.序号（主键）',
      `name`                      VARCHAR(50) COMMENT '1、姓名',
      `gender`                    ENUM('男','女') COMMENT '2、性别',
      `birth_date`                DATE COMMENT '3、出生日期',
      `visit_time`                DATE COMMENT '4、就诊时间',
      `hospital`                  VARCHAR(100) COMMENT '5、就诊医院',
      `hospital_dept_id`          BIGINT(20) COMMENT '就诊医院所属部门ID',
      `medical_record_no`         VARCHAR(50) COMMENT '6、病历号',
      `parent_name`               VARCHAR(50) COMMENT '7、家长姓名',
      `phone`                     VARCHAR(20) COMMENT '8、手机号码',
      `past_medication`           TEXT COMMENT '9、既往用药',
      `chief_complaint`           TEXT COMMENT '10、主诉',
      `main_symptom`              TEXT COMMENT '11、主证',
      `comorbidity`               TEXT COMMENT '12、共患病',
      `physical_exam`             TEXT COMMENT '13、体格检查 ',
      `tongue_pulse`              TEXT COMMENT '14、舌脉',
      `tongue_image_path`         TEXT COMMENT '15、请上传自然光线下舌象照片',
      `allergen_total_ige`        TEXT COMMENT '16、过敏原检测 总IgE（IU/ml）',
      `allergen_specific_ige`     TEXT COMMENT '17、过敏原检测 特异性IgE（IU/ml）',
      `blood_test_image_path`     TEXT COMMENT '18、血常规     RBC;WBC;PLT;Hb;EOS',
      `inflammation_image_path`   TEXT COMMENT '19、炎症因子 IL-4；IL-6；IL-10；TNF-α',
      `liver_kidney_image_path`   TEXT COMMENT '20、肝肾功能 Scr；Bun；ALT；AST；TBIL',
      `renal_injury_image_path`   TEXT COMMENT '21、肾早期损伤 尿微量蛋白；尿肌酐；β2-微球蛋白；微量蛋白/尿肌酐',
      `tcm_diagnosis`             VARCHAR(100) COMMENT '22、中医诊断',
      `tcm_treatment`             VARCHAR(100) COMMENT '23、中医治法',
      `tcm_treatment_image_path`  TEXT COMMENT '24、中药处方',
      `tcm_external_prescription` TEXT COMMENT '25、中医外治处方',
      `create_by`                 VARCHAR(64) DEFAULT '' COMMENT '创建者',
      `create_time`               DATETIME DEFAULT NULL COMMENT '创建时间',
      `update_by`                 VARCHAR(64) DEFAULT '' COMMENT '更新者',
      `update_time`               DATETIME DEFAULT NULL COMMENT '更新时间',
      `remark`                    VARCHAR(500) DEFAULT NULL COMMENT '备注',
      PRIMARY KEY (`visit_id`),
      KEY `idx_medical_record_no` (`medical_record_no`) COMMENT '病历号索引',
      KEY `idx_visit_time` (`visit_time`) COMMENT '就诊时间索引',
      KEY `idx_hospital_dept_id` (`hospital_dept_id`) COMMENT '就诊医院部门索引',
      KEY `idx_hospital` (`hospital`) COMMENT '医院名称索引'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='鼻炎患者就诊信息主表（包含图片路径等扩展字段）';

/*!40101 SET character_set_client = @saved_cs_client */;


-- ============================================================================
-- 问卷表
-- ============================================================================
DROP TABLE IF EXISTS sys_survey;
CREATE TABLE sys_survey (
  survey_id         bigint(20)      not null auto_increment    comment '问卷ID',
  title             varchar(255)    default ''                 comment '问卷标题',
  content           longtext                                   comment '问卷内容',
  status            char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (survey_id)
) engine=innodb auto_increment=100 default charset=utf8mb4 collate=utf8mb4_general_ci comment = '问卷表';
