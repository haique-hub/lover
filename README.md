# Lovers Space

情侣互动情侣空间系统，包含后端 Spring Boot 3 + 前端 Vue 3 + MySQL 8 建表脚本。

## 目录结构

- `backend`：Spring Boot 后端
- `frontend`：Vue 3 前端
- `backend/sql/lovers_space.sql`：MySQL 8 初始化脚本

## 后端启动

1. 创建数据库并执行脚本：
   - `backend/sql/lovers_space.sql`
2. 修改后端配置：
   - `backend/src/main/resources/application.yml`
3. 进入后端目录启动：

```bash
cd backend
mvn spring-boot:run
```

默认端口：`8080`

## 前端启动

```bash
cd frontend
npm install
npm run dev
```

默认端口：`5173`

开发环境已配置代理：

- `/api` -> `http://localhost:8080`
- `/files` -> `http://localhost:8080`

## 默认说明

- 登录方式：用户名 + 密码
- 鉴权方式：JWT Bearer Token
- 图片存储：照片与头像统一存储在 MySQL 数据库中
- 内容权限：仅本人和已绑定情侣可访问
