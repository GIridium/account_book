# 后端开发下一步计划

## 当前进度（2026-03-14 更新）

- **数据库严格落地**：`V1__Init_Schema.sql` 已对齐设计文档，包含 `users`, `categories`, `bills`, `saving_goals`, `shared_books`, `shared_book_members` 表结构及索引、约束。默认分类数据已初始化。
- **账单主流程打通**：`TransactionController` 及 Service 层已实现基于数据库的 CRUD（增删改查）。
- **自动化验证通过**：新增 `TransactionApiTest` 集成测试，验证了数据库连接、Flyway 迁移、数据初始化及 API 契约一致性。
- **接口契约升级**：`Create/UpdateTransactionRequest` 已升级为强类型校验（`categoryId`, `merchant`, `remark`），符合前端对接需求。

## 已完成任务

### 1. 数据库基线建设
- [x] 完成 `V1__Init_Schema.sql` 编写，包含完整约束与默认分类初始化。
- [x] 配置 Flyway 自动迁移与测试环境自动清理策略。
- [x] 验证 PostgreSQL 连接与 Schema 落地。

### 2. 核心业务接口实现（Bills）
- [x] `BillEntity` 与数据库表结构对齐（字段类型、索引、外键）。
- [x] `TransactionService` 实现分类 ID 关联查找与默认用户兜底。
- [x] 实现并验证 POST/GET/PUT/DELETE `/api/transactions` 接口。

### 3. 工程质量保障
- [x] 创建 `TransactionApiTest` 集成测试，覆盖全链路。
- [x] 修复 DTO 校验规则与 Entity 映射逻辑。

## 下一步计划（Next Steps）

### 阶段一：前端联调与体验优化（优先）
目标：支持前端完成核心记账功能的对接与演示。

1. **接口文档与契约冻结**
   - 输出详细接口文档（API Markdown 或 Swagger/OpenAPI），明确字段类型与错误码。
   - 确认 `categoryId` 获取接口（需提供 `GET /api/categories`）。

2. **分类管理接口**
   - 实现 `GET /api/categories`：查询系统默认 + 用户自定义分类。
   - 实现 `POST /api/categories`：用户新增自定义分类。

3. **异常处理标准化**
   - 统一全局异常处理（`GlobalExceptionHandler`），将 404/400 错误转化为前端友好的 JSON 格式。

### 阶段二：高级功能扩展（业务闭环）
目标：完成储蓄目标与共享账本逻辑。

1. **储蓄目标（Saving Goals）**
   - 建立 `SavingGoalService`。
   - 实现与账单的关联（存钱也是一种支出/转账？需明确业务逻辑）。

2. **共享账本（Shared Books）**
   - 实现账本创建与邀请码生成（`InviteCode` 逻辑）。
   - 实现成员加入与鉴权（Middleware/Interceptor 验证 `shared_book_id` 权限）。

3. **用户鉴权（Security）**
   - 引入 Spring Security 或简单 Token 机制，替换目前的 `default_user` 硬编码。

## 如何验证当前功能

### 1. 自动化测试（推荐）
运行集成测试，系统会自动启动 PostgreSQL 容器（或连接本地库）、执行迁移并验证 CRUD：

```powershell
.\backend\gradlew.bat test -p backend
```
查看测试报告：`backend/build/reports/tests/test/index.html`

### 2. 手工验证（使用 PowerShell）

**前提**：确保本地 PostgreSQL 运行，且 `account_book` 数据库存在。

启动服务：
```powershell
.\backend\gradlew.bat bootRun -p backend
```

验证流程：

1. **健康检查**: `Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/health"`
2. **创建账单**:
   ```powershell
   $body = @{
       amount = 88.88
       categoryId = 1  # 假设 '餐饮' ID 为 1，若失败请先查询分类
       remark = "周末聚餐"
       merchant = "海底捞"
       date = "2026-03-14"
   } | ConvertTo-Json
   Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/transactions" -ContentType "application/json" -Body $body
   ```
3. **查询账单**: `Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/transactions"`

### 3. 数据库验证
不仅看接口返回，还可直接查询数据库确认落库：
```sql
SELECT * FROM bills;
SELECT * FROM categories;
```

