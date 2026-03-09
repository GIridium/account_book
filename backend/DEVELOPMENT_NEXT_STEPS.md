# 后端开发下一步计划

## 当前进度（2026-03-09）

- 后端服务可以正常启动（`bootRun`）。
- 健康检查接口可用：`GET /api/health` 返回 `200`。
- 交易接口的基础 CRUD 流程已打通。
- 当前存储方式是**内存存储**，不是真实数据库持久化。
- 本项目 Gradle/JDK 环境已基本稳定（项目使用 JDK 21）。

## 当前核心问题

后端仍使用内存存储，服务重启后数据会丢失，需要尽快切换到真实数据库。

## 阶段目标

将后端持久化从内存切换到 PostgreSQL，并验证服务重启后数据仍然存在。

## 任务清单

### 1. 准备本地 PostgreSQL

- 可选方案：
- 方案 A：Docker（推荐）
- 方案 B：本机直接安装 PostgreSQL

若使用 Docker，创建并运行本地开发用 PostgreSQL 容器。

验收标准：
- PostgreSQL 正常运行且可连接。
- 可以通过 host/port/user/password 连通数据库。

### 2. 完成 Spring Data JPA + PostgreSQL 配置

- 确认 `build.gradle.kts` 中已有依赖：
- `spring-boot-starter-data-jpa`
- `org.postgresql:postgresql`

- 在 `application.yml` 或 `application-dev.yml` 中补充数据源配置：
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.jpa.hibernate.ddl-auto`（开发环境建议先用 `update`）

验收标准：
- 服务能在连接 PostgreSQL 的情况下启动。
- 启动日志中可见数据源初始化成功。

### 3. 用 JPA Repository 替换内存仓库

- 保持现有 API 协议不变。
- 为交易表建立 JPA Entity。
- 新建 `JpaRepository` 接口。
- 修改 Service 层，让它使用 JPA 仓库，而不是 `InMemoryTransactionRepository`。

验收标准：
- `POST /api/transactions` 可写入数据库。
- `GET /api/transactions` 可从数据库读取。
- `PUT`、`DELETE` 功能正常。

### 4. 增加基础数据库迁移能力（推荐）

- 引入 Flyway（或 Liquibase）进行版本化迁移。
- 创建交易表初始迁移脚本。

验收标准：
- 启动时可自动完成 schema 初始化。
- 不同环境下表结构一致、可复现。

### 5. 增加持久化验证测试

- 增加集成测试（或手工验证脚本）确认数据持久化。

建议验证流程：
1. 创建一条交易记录。
2. 停止后端服务。
3. 重新启动后端服务。
4. 查询交易列表。
5. 确认之前记录仍存在。

验收标准：
- 重启后数据不丢失。

## 建议执行顺序

1. 搭建本地 PostgreSQL
2. 完成数据源配置
3. 实现 JPA Entity + Repository + Service 改造
4. 增加迁移脚本
5. 完成持久化验证
6. 对接前端联调

## 手工验证命令

启动后端：

```powershell
cd G:\account_book\account_book\backend
.\gradlew.bat --stop
.\gradlew.bat clean test
.\gradlew.bat bootRun
```

健康检查：

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/health"
```

创建交易：

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/transactions" -ContentType "application/json" -Body '{"amount":12.34,"category":"food","note":"lunch","date":"2026-03-09"}'
```

查询列表：

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/transactions"
```

## 风险与注意事项

- 若出现 Gradle/JDK 不匹配，先确认项目 Gradle JVM 是否为 JDK 21。
- 若数据库连接失败，重点检查 `url/username/password` 和端口占用。
- 凭据不要直接提交到 Git，生产环境建议使用环境变量。

## 完成定义（Definition of Done）

- 后端已切换到 PostgreSQL 持久化。
- 服务重启后交易数据仍存在。
- 核心交易接口冒烟测试通过。
- 构建和测试结果为绿色（通过）。
