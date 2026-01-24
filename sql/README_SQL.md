# SQL 文件说明

本目录包含多份初始化与备份 SQL。为便于统一使用，已生成合并版：
- `sql/all_in_one.sql`

## 单文件用途
- `sql/create_db_n_user.sql`
  - 创建数据库 `fl`，并创建本地用户 `fluser`，授予 `fl.*` 权限。
- `sql/ry_20250522.sql`
  - RuoYi 基础库结构与初始化数据（系统/权限/菜单/角色/字典等）。
- `sql/quartz.sql`
  - Quartz 定时任务相关表结构（QRTZ_*）。
- `sql/fl_table.sql`
  - 业务表 `patient_info`、`patient_visit_info` 的结构与示例数据。
- `sql/fl_20251108154338_backup.sql`
  - 数据库 `fl` 的完整导出快照（含大量数据与表结构）。
- `sql/role_permission_update.sql`
  - 角色与权限增强：医生申请字段、菜单权限、五级角色与菜单绑定、就诊医院与部门关联字段及回填等。

## 合并版说明（`sql/all_in_one.sql`）
该文件按以下顺序拼接：
1. `create_db_n_user.sql`
2. `ry_20250522.sql`
3. `quartz.sql`
4. `fl_table.sql`
5. `fl_20251108154338_backup.sql`（完整快照）
6. `role_permission_update.sql`

### 注意事项
- **完整快照会 DROP/CREATE 多数表**，如果你要“恢复快照”，建议只运行快照段，并在其后运行 `role_permission_update.sql`。
- 如果你从零初始化，不需要恢复快照，可跳过快照段。
- 执行前请确认使用的数据库（建议 `USE fl;`）。

## 推荐使用方式
- **全新初始化（不恢复快照）**：
  - 依次执行：`create_db_n_user.sql` → `ry_20250522.sql` → `quartz.sql` → `fl_table.sql` → `role_permission_update.sql`
- **恢复已有快照**：
  - 执行：`create_db_n_user.sql` → `fl_20251108154338_backup.sql` → `role_permission_update.sql`
