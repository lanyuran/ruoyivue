# ruoyi-ui 前端

## 开发
```bash
# 进入项目目录
cd ruoyi-ui

# 安装依赖
npm install

# 启动服务（默认端口 80）
npm run dev
```

浏览器访问：`http://localhost:80`

如端口 80 被占用，可临时改端口：
```bash
port=8081 npm run dev
```

## 发布
```bash
# 构建测试环境
npm run build:stage

# 构建生产环境
npm run build:prod
```

更多后端/数据库说明请查看项目根目录 `README.md`。
