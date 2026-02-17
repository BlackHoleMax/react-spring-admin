#!/bin/bash

# Docker 开发环境启动脚本

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

echo "========================================="
echo "React Spring Admin - Docker 开发环境"
echo "========================================="
echo ""

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ 错误: 未找到 docker 命令"
    echo "请先安装 Docker: https://docs.docker.com/get-docker/"
    exit 1
fi

echo "✓ Docker 版本: $(docker --version)"

# 检查 Docker Compose 是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "❌ 错误: 未找到 docker-compose 命令"
    echo "请先安装 Docker Compose: https://docs.docker.com/compose/install/"
    exit 1
fi

echo "✓ Docker Compose 版本: $(docker-compose --version)"

# 进入 docker-compose 目录
cd "$SCRIPT_DIR"

echo ""
echo "🚀 启动开发环境服务..."
echo ""

# 启动服务
docker-compose up -d

echo ""
echo "========================================="
echo "✅ 开发环境启动成功！"
echo "========================================="
echo ""
echo "服务访问地址："
echo "  - 前端应用:     http://localhost:3000"
echo "  - 后端API:      http://localhost:8080"
echo "  - API文档:      http://localhost:8080/doc.html"
echo "  - MySQL:        localhost:3306"
echo "  - Redis:        localhost:6379"
echo "  - RabbitMQ:     localhost:5672"
echo "  - RabbitMQ管理: http://localhost:15672 (admin/12345678)"
echo "  - MinIO:        localhost:9000"
echo "  - MinIO控制台:  http://localhost:9001 (admin/12345678)"
echo ""
echo "默认账号: admin / admin123"
echo "数据库密码: 12345678"
echo "Redis密码: 12345678"
echo ""
echo "查看日志: docker-compose logs -f"
echo "停止服务: docker-compose down"
echo ""