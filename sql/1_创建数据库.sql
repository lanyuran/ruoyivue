-- 用途: 创建 fl 数据库与本地账户（fluser/123456）
# mysql

# 删除并创建数据库 fl
# 字符集改为 utf8mb4
#   源字符集为 utf8(utf8mb3), utf8mb4兼容utf8, utf8不兼容utf8mb4
# 排序方式为 utf8mb4_general_ci
DROP DATABASE IF EXISTS fl;
CREATE DATABASE fl
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

CREATE USER IF NOT EXISTS 'fluser'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON fl.* TO 'fluser'@'localhost';
FLUSH PRIVILEGES;
USE fl;
