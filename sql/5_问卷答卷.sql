-- R3: 问卷答卷表
USE fl;

DROP TABLE IF EXISTS sys_survey_answer_item;
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

