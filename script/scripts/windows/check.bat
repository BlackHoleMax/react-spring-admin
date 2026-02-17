@echo off
REM 部署状态检查脚本 (Windows版本)

echo [INFO] 检查部署状态...
echo.

cd ..\..\docker
echo [INFO] 服务状态：
docker-compose ps
echo.

echo [INFO] 服务健康检查：
echo.

REM 检查后端服务
echo 检查后端服务 (http://localhost:8080)...
curl -s -o nul -w "%%{http_code}" http://localhost:8080/actuator/health | findstr "200" >nul
if %errorlevel% equ 0 (
    echo [OK] 后端服务运行正常
) else (
    echo [ERROR] 后端服务不可用
)

REM 检查前端服务
echo 检查前端服务 (http://localhost:3000)...
curl -s -o nul -w "%%{http_code}" http://localhost:3000 | findstr "200" >nul
if %errorlevel% equ 0 (
    echo [OK] 前端服务运行正常
) else (
    echo [WARNING] 前端服务不可用或未启动
)

REM 检查MySQL连接
echo 检查MySQL连接 (localhost:3306)...
docker-compose exec -T mysql mysqladmin ping -h localhost -u root -p123456 >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] MySQL连接正常
) else (
    echo [ERROR] MySQL连接失败
)

REM 检查Redis连接
echo 检查Redis连接 (localhost:6379)...
docker-compose exec -T redis redis-cli ping | findstr "PONG" >nul
if %errorlevel% equ 0 (
    echo [OK] Redis连接正常
) else (
    echo [ERROR] Redis连接失败
)

REM 检查RabbitMQ连接
echo 检查RabbitMQ连接 (localhost:5672)...
docker-compose exec -T rabbitmq rabbitmq-diagnostics -q ping >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] RabbitMQ连接正常
) else (
    echo [ERROR] RabbitMQ连接失败
)

REM 检查RabbitMQ管理界面
echo 检查RabbitMQ管理界面 (http://localhost:15672)...
curl -s -o nul -w "%%{http_code}" http://localhost:15672/ | findstr "200" >nul
if %errorlevel% equ 0 (
    echo [OK] RabbitMQ管理界面正常
) else (
    echo [WARNING] RabbitMQ管理界面不可用
)

REM 检查MinIO连接
echo 检查MinIO连接 (http://localhost:9000)...
curl -s -o nul -w "%%{http_code}" http://localhost:9000/minio/health/live | findstr "200" >nul
if %errorlevel% equ 0 (
    echo [OK] MinIO连接正常
) else (
    echo [ERROR] MinIO连接失败
)

echo.
echo [INFO] 检查完成！
echo.
echo 访问地址：
echo - 后端API: http://localhost:8080
echo - 前端应用: http://localhost:3000
echo - RabbitMQ管理界面: http://localhost:15672 (admin/123456)
echo - MinIO控制台: http://localhost:9001 (admin/12345678)
echo.
pause