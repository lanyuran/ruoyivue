# 小儿过敏性鼻炎病证研究数据库管理系统（RuoYi-Vue 3.9.0）

本项目基于 RuoYi-Vue 二次开发，前后端分离：
- 后端：Spring Boot + MyBatis + Redis
- 前端：Vue 2 + Element UI
- 数据库：MySQL（库名固定为 `fl`）

## 运行环境
- JDK 1.8
- Maven
- MySQL 5.7+ / 8.x
- Redis
- Node.js + npm

## 快速启动

### 1) 初始化数据库（库名必须为 `fl`）
按顺序执行（已包含权限/菜单/基础系统功能）：
1. `sql/1_创建数据库.sql` 创建数据库与账号
2. `sql/2_基础运行数据.sql` 导入若依基础数据（含权限/菜单/角色/Quartz）
3. `sql/3_业务.sql` 导入业务表（患者信息、问卷等；不含示例数据）

`sql/0_整合脚本.sql` 为整合包，包含 1-3 的完整合集（不含完整数据快照）。执行前请确认内容与顺序（含 DROP/CREATE）。

### 2) 配置数据库连接
编辑：`ruoyi-admin/src/main/resources/application-druid.yml`
示例（与 README.txt 保持一致）：
- 数据库名：`fl`
- 用户：`fluser`
- 密码：`123456`

### 3) 启动依赖服务
- 启动 MySQL
- 启动 Redis
  - Windows：运行 `redis_server.exe`
  - 其他系统：`redis-server`

### 4) 启动后端
在项目根目录执行：
```bash
mvn -pl ruoyi-admin -am spring-boot:run
```

### 5) 启动前端
```bash
cd ruoyi-ui
npm install
npm run dev
```
浏览器访问：`http://localhost:80`

如端口 80 被占用，可临时改端口：
```bash
port=8081 npm run dev
```

## 文件上传路径说明
`ruoyi-admin/src/main/resources/application.yml` 中配置：
```yml
ruoyi:
  profile: ${user.dir}/image-cache
```
实际上传目录为 `${user.dir}/image-cache/upload`，`image-cache/` 是运行时产物，建议不入库。

## SQL 文件用途速览
- `sql/1_创建数据库.sql`：创建数据库与账号
- `sql/2_基础运行数据.sql`：若依基础库结构与初始数据（含权限/菜单/角色/Quartz）
- `sql/3_业务.sql`：业务表结构（患者信息、问卷，不含示例数据）
- `sql/0_整合脚本.sql`：整合执行脚本（含 DROP/CREATE）

## 常见问题
- `npm` 命令不可用：请确认 Node.js 安装并已加入 PATH。
- Redis 未启动：前端/后端可能登录失败或功能异常，请先启动 Redis。
