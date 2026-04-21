-- 用途: 业务表（患者信息、就诊主表、问卷；不含示例数据）
-- 说明: 新库初始化时直接执行本文件即可，无需额外增量脚本。
USE fl;
SET NAMES utf8mb4 COLLATE utf8mb4_general_ci;
# MySQL 8.4 LTS 

#########################################
######## TABLE patient_profile ########
#########################################

DROP TABLE IF EXISTS `patient_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;

CREATE TABLE `patient_profile` (
      `patient_id`   BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '患者主档ID',
      `user_id`      BIGINT(20) DEFAULT NULL COMMENT '关联账号ID',
      `name`         VARCHAR(50) NOT NULL COMMENT '患者姓名',
      `gender`       ENUM('男','女') DEFAULT NULL COMMENT '患者性别',
      `birth_date`   DATE DEFAULT NULL COMMENT '出生日期',
      `parent_name`  VARCHAR(50) DEFAULT NULL COMMENT '监护人姓名',
      `phone`        VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
      `create_by`    VARCHAR(64) DEFAULT '' COMMENT '创建者',
      `create_time`  DATETIME DEFAULT NULL COMMENT '创建时间',
      `update_by`    VARCHAR(64) DEFAULT '' COMMENT '更新者',
      `update_time`  DATETIME DEFAULT NULL COMMENT '更新时间',
      `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注',
      PRIMARY KEY (`patient_id`),
      UNIQUE KEY `uk_patient_profile_user_id` (`user_id`) COMMENT '账号唯一绑定患者主档',
      KEY `idx_patient_profile_phone` (`phone`) COMMENT '联系电话索引',
      KEY `idx_patient_profile_name_birth` (`name`, `birth_date`) COMMENT '患者姓名出生日期索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='患者主档表';

/*!40101 SET character_set_client = @saved_cs_client */;

######## TABLE patient_profile end ########

#######################################
######## TABLE patient_chart ########
#######################################

DROP TABLE IF EXISTS `patient_chart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;

CREATE TABLE `patient_chart` (
      `chart_id`           BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '病历映射ID',
      `patient_id`         BIGINT(20) NOT NULL COMMENT '患者主档ID',
      `hospital_dept_id`   BIGINT(20) NOT NULL COMMENT '医院部门ID',
      `hospital`           VARCHAR(100) DEFAULT NULL COMMENT '医院名称',
      `medical_record_no`  VARCHAR(50) NOT NULL COMMENT '病历号',
      `create_by`          VARCHAR(64) DEFAULT '' COMMENT '创建者',
      `create_time`        DATETIME DEFAULT NULL COMMENT '创建时间',
      `update_by`          VARCHAR(64) DEFAULT '' COMMENT '更新者',
      `update_time`        DATETIME DEFAULT NULL COMMENT '更新时间',
      `remark`             VARCHAR(500) DEFAULT NULL COMMENT '备注',
      PRIMARY KEY (`chart_id`),
      UNIQUE KEY `uk_patient_chart_hospital_record` (`hospital_dept_id`, `medical_record_no`) COMMENT '院区内病历号唯一',
      KEY `idx_patient_chart_patient_id` (`patient_id`) COMMENT '患者主档索引',
      KEY `idx_patient_chart_patient_hospital` (`patient_id`, `hospital_dept_id`) COMMENT '患者医院索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='患者医院病历映射表';

/*!40101 SET character_set_client = @saved_cs_client */;

######## TABLE patient_chart end ########

################################################################
######## Table structure for table `patient_visit_info` ########
################################################################

DROP TABLE IF EXISTS `patient_visit_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;

CREATE TABLE `patient_visit_info` (
      `visit_id`                  INT(11) NOT NULL AUTO_INCREMENT COMMENT '0.序号（主键）',
      `patient_id`                BIGINT(20) DEFAULT NULL COMMENT '患者主档ID',
      `chart_id`                  BIGINT(20) DEFAULT NULL COMMENT '病历映射ID',
      `name`                      VARCHAR(50) COMMENT '1、姓名',
      `gender`                    ENUM('男','女') COMMENT '2、性别',
      `birth_date`                DATE COMMENT '3、出生日期',
      `visit_time`                DATE COMMENT '4、就诊时间',
      `fill_time`                 DATETIME DEFAULT NULL COMMENT '填表时间',
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
      UNIQUE KEY `uk_hospital_record_visit` (`hospital_dept_id`, `medical_record_no`, `visit_time`, `fill_time`) COMMENT '院区内同一病历号同日同填表时间唯一',
      KEY `idx_patient_id` (`patient_id`) COMMENT '患者主档索引',
      KEY `idx_chart_id` (`chart_id`) COMMENT '病历映射索引',
      KEY `idx_fill_time` (`fill_time`) COMMENT '填表时间索引',
      KEY `idx_medical_record_no` (`medical_record_no`) COMMENT '病历号索引',
      KEY `idx_visit_time` (`visit_time`) COMMENT '就诊时间索引',
      KEY `idx_hospital_dept_id` (`hospital_dept_id`) COMMENT '就诊医院部门索引',
      KEY `idx_hospital` (`hospital`) COMMENT '医院名称索引'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='患者就诊记录表（保留患者基础信息快照与图片路径等扩展字段）';

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

-- ============================================================================
-- 问卷答卷主表
-- ============================================================================
DROP TABLE IF EXISTS sys_survey_answer;
CREATE TABLE sys_survey_answer (
  answer_id        bigint(20)      NOT NULL AUTO_INCREMENT COMMENT '答卷ID',
  survey_id        bigint(20)      NOT NULL COMMENT '问卷ID',
  participant_key  varchar(64)     NOT NULL COMMENT '参与者标识（默认登录用户名）',
  submit_ip        varchar(128)    DEFAULT '' COMMENT '提交IP',
  user_agent       varchar(255)    DEFAULT '' COMMENT '客户端信息',
  answer_json      longtext        COMMENT '答卷原始JSON',
  submit_time      datetime        NOT NULL COMMENT '提交时间',
  create_by        varchar(64)     DEFAULT '' COMMENT '创建者',
  create_time      datetime        DEFAULT NULL COMMENT '创建时间',
  update_by        varchar(64)     DEFAULT '' COMMENT '更新者',
  update_time      datetime        DEFAULT NULL COMMENT '更新时间',
  remark           varchar(500)    DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (answer_id),
  UNIQUE KEY uk_survey_participant (survey_id, participant_key),
  KEY idx_survey_id (survey_id),
  KEY idx_submit_time (submit_time),
  KEY idx_participant_key (participant_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='问卷答卷主表';

-- ============================================================================
-- 问卷答题明细表
-- ============================================================================
DROP TABLE IF EXISTS sys_survey_answer_item;
CREATE TABLE sys_survey_answer_item (
  item_id          bigint(20)      NOT NULL AUTO_INCREMENT COMMENT '答题明细ID',
  answer_id        bigint(20)      NOT NULL COMMENT '答卷ID',
  survey_id        bigint(20)      NOT NULL COMMENT '问卷ID',
  question_key     varchar(128)    DEFAULT NULL COMMENT '问题Key',
  question_label   varchar(255)    DEFAULT NULL COMMENT '问题标题',
  question_type    varchar(64)     DEFAULT NULL COMMENT '问题类型',
  answer_text      longtext        COMMENT '答案文本',
  option_value     varchar(500)    DEFAULT NULL COMMENT '选项值',
  score            decimal(10,2)   DEFAULT NULL COMMENT '得分',
  sort_no          int(11)         DEFAULT NULL COMMENT '排序',
  submit_time      datetime        DEFAULT NULL COMMENT '提交时间',
  PRIMARY KEY (item_id),
  KEY idx_answer_id (answer_id),
  KEY idx_item_survey_id (survey_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='问卷答题明细表';
