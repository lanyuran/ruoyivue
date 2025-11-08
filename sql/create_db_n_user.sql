# mysql

# 创建一个数据库 fl
# 字符集改为 utf8mb4
#   源字符集为 utf8(utf8mb3), utf8mb4兼容utf8, utf8不兼容utf8mb4
# 排序方式为 utf8mb4_general_ci
CREATE DATABASE fl
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

CREATE USER 'fluser'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON fl.* TO 'fluser'@'localhost';
FLUSH PRIVILEGES;
