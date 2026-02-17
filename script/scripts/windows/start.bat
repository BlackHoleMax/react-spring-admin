@echo off
REM Web Template 启动脚本 (Windows版本)

echo [INFO] 开始启动 Web Template 应用...

REM 检查Docker是否安装
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker 未安装，请先安装 Docker Desktop
    pause
    exit /b 1
)

REM 检查Docker Compose是否可用
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Docker Compose 不可用，请确保 Docker Desktop 正在运行
    pause
    exit /b 1
)

echo [INFO] Docker 和 Docker Compose 已就绪

REM 创建必要的目录
if not exist "logs" mkdir logs
if not exist "data" mkdir data
if not exist "data\mysql" mkdir data\mysql
if not exist "data\redis" mkdir data\redis
if not exist "data\minio" mkdir data\minio

echo [INFO] 启动所有服务...
cd ..\..\docker
docker-compose up -d

if %errorlevel% equ 0 (
    echo [INFO] 所有服务启动成功！
    echo [INFO] 服务访问地址：
    echo   - 后端API: http://localhost:8080
    echo   - 前端应用: http://localhost:3000
    echo   - MySQL: localhost:3306
    echo   - Redis: localhost:6379
    echo   - MinIO控制台: http://localhost:9001
    echo.
    echo [INFO] 查看服务状态: docker-compose ps
    echo [INFO] 查看日志: docker-compose logs -f [service_name]
    echo [INFO] 停止服务: %~dp0stop.bat
) else (
    echo [ERROR] 服务启动失败
    pause
    exit /b 1
)

echo [INFO] 等待服务就绪...
timeout /t 10 /nobreak >nul

REM 检查后端健康状态
for /l %%i in (1,1,30) do (
    curl -f http://localhost:8080/actuator/health >nul 2>&1
    if !errorlevel! equ 0 (
        echo [INFO] 后端服务已就绪
        goto :end
    )
    if %%i equ 30 (
        echo [WARNING] 后端服务可能还在启动中，请稍后检查
    )
    timeout /t 2 /nobreak >nul
)

:end
echo [INFO] 启动完成！
pause