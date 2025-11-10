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
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

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
      `name`                      VARCHAR(50) COMMENT '1.患者姓名',
      `gender`                    ENUM('男','女') COMMENT '2.患者性别（仅男女选项）',
      `birth_date`                DATE COMMENT '3.患者出生日期（格式：YYYY-MM-DD）',
      `visit_time`                DATE COMMENT '4.就诊时间（格式：YYYY-MM-DD）',
      `hospital`                  VARCHAR(100) COMMENT '5.就诊医院名称',
      `medical_record_no`         VARCHAR(50) COMMENT '6.病历号（唯一标识患者病历）',
      `parent_name`               VARCHAR(50) COMMENT '7.家长姓名（儿童患者必填，成人可选）',
      `phone`                     VARCHAR(20) COMMENT '8.联系电话（手机号码）',
      `past_medication`           TEXT COMMENT '9.既往用药史（支持多行文本）',
      `chief_complaint`           TEXT COMMENT '10.主诉（主要症状描述）',
      `main_symptom`              TEXT COMMENT '11.主证（如鼻塞、鼻痒等，多个症状用逗号分隔）',
      `comorbidity`               TEXT COMMENT '12.共患病（如鼻窦炎、哮喘等）',
      `physical_exam`             TEXT COMMENT '13.体格检查结果（如鼻黏膜色红、鼻甲肿胀等）',
      `tongue_pulse`              TEXT COMMENT '14.舌脉情况（记录舌质、舌苔、脉象描述）',
      `tongue_image_path`         TEXT COMMENT '15.舌象照片路径（自然光线下拍摄）',
      `allergen_total_ige`        TEXT COMMENT '16.过敏原检测-总IgE（IU/mL）',
      `allergen_specific_ige`     TEXT COMMENT '17.过敏原检测-特异性IgE（kUA/L）',
      `blood_test_image_path`     TEXT COMMENT '18.血常规结果图片路径',
      `inflammation_image_path`   TEXT COMMENT '19.炎症因子结果图片路径',
      `liver_kidney_image_path`   TEXT COMMENT '20.肝肾功能 Scr、Bun、ALT、AST、TBIL',
      `renal_injury_image_path`   TEXT COMMENT '21.肾早期损伤相关检测结果图片路径',
      `tcm_diagnosis`             VARCHAR(100) COMMENT '22.中医诊断（如“鼻鼽 风寒犯肺证”）',
      `tcm_treatment`             VARCHAR(100) COMMENT '23.中医治法（如“疏风散寒 宣通鼻窍”）',
      `tcm_treatment_image_path`  TEXT COMMENT '24.中医处方照片路径',
      `tcm_external_prescription` TEXT COMMENT '25.中医外治处方（如香嗅疗法、耳穴压豆等方案）',
      PRIMARY KEY (`visit_id`),
      KEY `idx_medical_record_no` (`medical_record_no`) COMMENT '病历号索引',
      KEY `idx_visit_time` (`visit_time`) COMMENT '就诊时间索引',
      KEY `idx_hospital` (`hospital`) COMMENT '医院名称索引'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='鼻炎患者就诊信息主表（包含图片路径等扩展字段）';

/*!40101 SET character_set_client = @saved_cs_client */;

-- ----------------------------------------------------------
-- 示例数据
-- ----------------------------------------------------------
LOCK TABLES `patient_visit_info` WRITE;
/*!40000 ALTER TABLE `patient_visit_info` DISABLE KEYS */;

INSERT INTO `patient_visit_info` (
    `name`, `gender`, `birth_date`, `visit_time`, `hospital`, `medical_record_no`,
    `parent_name`, `phone`, `past_medication`, `chief_complaint`, `main_symptom`,
    `comorbidity`, `physical_exam`, `tongue_pulse`, `tongue_image_path`,
    `allergen_total_ige`, `allergen_specific_ige`, `blood_test_image_path`,
    `inflammation_image_path`, `liver_kidney_image_path`, `renal_injury_image_path`,
    `tcm_diagnosis`, `tcm_treatment`, `tcm_treatment_image_path`, `tcm_external_prescription`
)
VALUES
-- 儿童患者（含家长信息）
('小一', '男', '2020-05-12', '2025-10-01', '中医院', 'MR20251001001',
 '张三', '13812345678', '无', '鼻塞', '打喷嚏,流涕',
 '无', '鼻甲轻度肿胀', '舌淡脉浮', '/images/tongue/xiaoyi.jpg',
 123.5, 2.8, '/images/lab/blood_xiaoyi.jpg', '/images/lab/inflam_xiaoyi.jpg',
 '/images/lab/liverkidney_xiaoyi.jpg', NULL,
 '鼻鼽·风寒犯肺证', '疏风散寒,宣通鼻窍', '/images/tcm/fangji_xiaoyi.jpg', '香嗅疗法'),

-- 儿童患者（过敏性哮喘）
('小二', '男', '2021-03-20', '2025-11-05', '儿童医院', 'MR20251105002',
 '李四', '13987654321', NULL, '鼻痒', '流涕,喷嚏频繁',
 '过敏性哮喘', '鼻黏膜充血', '舌红苔薄', '/images/tongue/xiaoer.jpg',
 310.2, 5.1, '/images/lab/blood_xiaoer.jpg', '/images/lab/inflam_xiaoer.jpg',
 NULL, NULL,
 '鼻鼽·风热犯肺证', '清热宣肺,通鼻窍', '/images/tcm/fangji_xiaoer.jpg', '耳穴压豆'),

-- 成人患者（慢性鼻炎）
('大一', '男', '1998-07-15', '2025-11-03', '人民医院', 'MR20251103003',
 NULL, '13799998888', '长期抗组胺药', '鼻塞加重', '喷嚏频繁,流涕',
 '慢性鼻炎', '鼻甲肿胀', '舌红脉细', '/images/tongue/dayi.jpg',
 NULL, NULL, '/images/lab/blood_dayi.jpg', NULL,
 '/images/lab/liverkidney_dayi.jpg', '/images/lab/renal_dayi.jpg',
 '鼻鼽·肺气虚弱证', '益气固表,通窍', '/images/tcm/fangji_dayi.jpg', '穴位贴敷');

/*!40000 ALTER TABLE `patient_visit_info` ENABLE KEYS */;
UNLOCK TABLES;

######## Table structure for table `patient_visit_info` ########