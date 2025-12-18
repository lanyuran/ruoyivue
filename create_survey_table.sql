drop table if exists sys_survey;
create table sys_survey (
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
) engine=innodb auto_increment=100 comment = '问卷表';
