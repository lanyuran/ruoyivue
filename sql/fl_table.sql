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
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient_visit_info` (
        `visit_id`                  int(11) NOT NULL AUTO_INCREMENT COMMENT '就诊记录ID（主键）',
        `name`                      varchar(50) NOT NULL COMMENT '患者姓名',
        `gender`                    enum('男','女') NOT NULL COMMENT '患者性别（仅男女选项）',
        `birth_date`                date NOT NULL COMMENT '患者出生日期（格式：YYYY-MM-DD）',
        `visit_time`                datetime NOT NULL COMMENT '就诊时间（格式：YYYY-MM-DD HH:MM:SS）',
        `hospital`                  varchar(100) NOT NULL COMMENT '就诊医院名称',
        `medical_record_no`         varchar(50) NOT NULL COMMENT '病历号（唯一标识患者病历）',
        `parent_name`               varchar(50) DEFAULT NULL COMMENT '家长姓名（儿童患者必填，成人可选）',
        `phone`                     varchar(20) NOT NULL COMMENT '联系手机号码（用于后续随访）',
        `past_medication`           text COMMENT '既往用药史（支持多行文本，记录历史用药情况）',
        `chief_complaint`           text NOT NULL COMMENT '主诉（患者主要症状描述）',
        `main_symptom`              text NOT NULL COMMENT '主证（如鼻塞、鼻痒等，多个症状用逗号分隔）',
        `comorbidity`               text COMMENT '共患病（如鼻窦炎、过敏性哮喘等，多个疾病用逗号分隔）',
        `physical_exam`             text NOT NULL COMMENT '体格检查结果（如鼻黏膜色红、鼻甲肿胀等）',
        `tongue_pulse`              text NOT NULL COMMENT '舌脉情况（记录舌质、舌苔、脉象描述）',
#         `tongue_image_path`         text COMMENT '自然光线下舌象照片的路径',
        `allergen_total_ige`        decimal(10,2) DEFAULT NULL COMMENT '过敏原检测-总IgE（数值型，单位：IU/mL）',
        `allergen_specific_ige`     decimal(10,2) DEFAULT NULL COMMENT '过敏原检测-特异性IgE（数值型，单位：kUA/L）',
#         血常规
#         炎症因子
#         肾早期损伤 尿微量蛋白；尿肌酐；β2-微球蛋白；微量蛋白/尿肌酐
        `tcm_diagnosis`             varchar(100) NOT NULL COMMENT '中医诊断（如“鼻鼽 风寒犯肺证”）',
        `tcm_treatment`             varchar(100) NOT NULL COMMENT '中医治法（如“疏风散寒 宣通鼻窍”）',
#         `tcm_treatment_image_path`  text COMMENT '中医处方照片',
        `tcm_external_prescription` text NOT NULL COMMENT '中医外治处方（如香嗅疗法、耳穴压豆等具体方案）',
        PRIMARY KEY (`visit_id`),
        KEY `idx_medical_record_no` (`medical_record_no`) COMMENT '病历号索引，加速按病历号查询',
        KEY `idx_visit_time` (`visit_time`) COMMENT '就诊时间索引，加速按时间范围查询',
        KEY `idx_hospital` (`hospital`) COMMENT '医院名称索引，加速按医院筛选数据'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='鼻炎患者就诊信息主表（包含文档中所有字段）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_visit_info`
--

LOCK TABLES `patient_visit_info` WRITE;
/*!40000 ALTER TABLE `patient_visit_info` DISABLE KEYS */;
INSERT INTO `patient_visit_info`
    VALUES
        (1,'小一','男','2025-10-01','2025-10-01 00:00:00','中医院','121212',NULL,'1212121212121','没有','没有','不知道','超级多','还行','看不清',12222.00,121212.00,'随便','不知道','啦啦啦'),
        (2,'小二','男','2025-11-13','2025-11-05 00:00:00','11','1',NULL,'1',NULL,'1','1',NULL,'1','1',NULL,NULL,'1','1','1'),
        (3,'大一','男','2025-11-10','2025-11-03 00:00:00','1','1',NULL,'1',NULL,'1','1',NULL,'1','1',NULL,NULL,'1','1','1');
/*!40000 ALTER TABLE `patient_visit_info` ENABLE KEYS */;
UNLOCK TABLES;

######## Table structure for table `patient_visit_info` ########