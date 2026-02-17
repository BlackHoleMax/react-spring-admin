#!/bin/bash

# 部署状态检查脚本 (Linux/Mac版本)

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_message "检查部署状态..."
echo

cd ../../docker
print_message "服务状态："
docker-compose ps
echo

print_message "服务健康检查："
echo

# 检查后端服务
echo "检查后端服务 (http://localhost:8080)..."
if curl -f -s http://localhost:8080/actuator/health > /dev/null; then
    print_message "后端服务运行正常"
else
    print_error "后端服务不可用"
fi

# 检查前端服务
echo "检查前端服务 (http://localhost:3000)..."
if curl -f -s http://localhost:3000 > /dev/null; then
    print_message "前端服务运行正常"
else
    print_warning "前端服务不可用或未启动"
fi

# 检查MySQL连接
echo "检查MySQL连接 (localhost:3306)..."
if docker-compose exec -T mysql mysqladmin ping -h localhost -u root -p123456 > /dev/null 2>&1; then
    print_message "MySQL连接正常"
else
    print_error "MySQL连接失败"
fi

# 检查Redis连接
echo "检查Redis连接 (localhost:6379)..."
if docker-compose exec -T redis redis-cli ping | grep -q "PONG"; then
    print_message "Redis连接正常"
else
    print_error "Redis连接失败"
fi

# 检查RabbitMQ连接
echo "检查RabbitMQ连接 (localhost:5672)..."
if docker-compose exec -T rabbitmq rabbitmq-diagnostics -q ping > /dev/null 2>&1; then
    print_message "RabbitMQ连接正常"
else
    print_error "RabbitMQ连接失败"
fi

# 检查RabbitMQ管理界面
echo "检查RabbitMQ管理界面 (http://localhost:15672)..."
if curl -f -s http://localhost:15672/ > /dev/null; then
    print_message "RabbitMQ管理界面正常"
else
    print_warning "RabbitMQ管理界面不可用"
fi

# 检查MinIO连接
echo "检查MinIO连接 (http://localhost:9000)..."
if curl -f -s http://localhost:9000/minio/health/live > /dev/null; then
    print_message "MinIO连接正常"
else
    print_error "MinIO连接失败"
fi

echo
print_message "检查完成！"
echo
echo "访问地址："
echo "- 后端API: http://localhost:8080"
echo "- 前端应用: http://localhost:3000"
echo "- RabbitMQ管理界面: http://localhost:15672 (admin/123456)"
echo "- MinIO控制台: http://localhost:9001 (admin/12345678)"
echo