# Account Book Backend (Kotlin)

## 运行
1. 确保本机安装 JDK 17+
2. 若首次克隆后缺少 `gradle/wrapper/gradle-wrapper.jar`，先在 `backend` 目录执行：

```powershell
gradle wrapper
```

3. 在 `g:\account_book\account_book\backend` 执行：

```powershell
.\gradlew.bat bootRun
```

4. 访问：
- `GET http://localhost:8080/api/health`

## 测试
```powershell
.\gradlew.bat test
```