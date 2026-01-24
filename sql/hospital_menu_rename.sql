-- Rename department management menu to hospital management
update sys_menu
set menu_name = '医院管理',
    remark = '医院管理菜单'
where menu_name = '部门管理'
  and path = 'dept';
