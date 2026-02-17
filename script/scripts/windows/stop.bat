@echo off
REM Web Template 停止脚本 (Windows版本)

echo [INFO] 停止所有服务...
cd ..\..\docker
docker-compose down

if %errorlevel% equ 0 (
    echo [INFO] 所有服务已停止
    
    REM 询问是否删除数据卷
    set /p confirm="是否要删除数据卷？这将删除所有数据 (y/N): "
    if /i "%confirm%"=="y" (
        echo [WARNING] 删除数据卷...
        docker-compose down -v
        echo [INFO] 数据卷已删除
    )
) else (
    echo [ERROR] 停止服务时出错
    pause
    exit /b 1
)

echo [INFO] 清理完成！
pause