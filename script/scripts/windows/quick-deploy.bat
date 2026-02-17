@echo off
REM 快速部署脚本 - 一键完成所有部署步骤

echo [INFO] Web Template 快速部署脚本
echo [INFO] =========================
echo.

REM 检查Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker 未安装，请先安装 Docker Desktop
    pause
    exit /b 1
)

echo [INFO] 1. 复制环境变量配置...
if not exist "..\..\.env" (
    copy "..\..\config\.env.example" "..\..\.env"
    echo [INFO] 已创建 .env 文件，请根据需要修改配置
)

echo [INFO] 2. 启动所有服务...
call %~dp0start.bat

echo [INFO] 3. 等待服务启动完成...
timeout /t 30 /nobreak >nul

echo [INFO] 4. 检查服务状态...
call %~dp0check.bat

echo.
echo [INFO] 部署完成！
echo [INFO] 访问地址：
echo   - 后端API: http://localhost:8080
echo   - 前端应用: http://localhost:3000
echo   - RabbitMQ管理界面: http://localhost:15672 (admin/123456)
echo   - MinIO控制台: http://localhost:9001
echo.
echo [INFO] 如需停止服务，请运行: %~dp0stop.bat
pause