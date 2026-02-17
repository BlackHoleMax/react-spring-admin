#!/bin/bash
# 快速部署脚本 - 一键完成所有部署步骤

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_message "Web Template 快速部署脚本"
print_message "========================="
echo

# 检查Docker
if ! command -v docker &> /dev/null; then
    print_error "Docker 未安装，请先安装 Docker"
    exit 1
fi

# 检查Docker Compose
if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

print_message "1. 复制环境变量配置..."
if [ ! -f "../.env" ]; then
    cp "../config/.env.example" "../.env"
    print_message "已创建 .env 文件，请根据需要修改配置"
fi

print_message "2. 启动所有服务..."
$(dirname "$0")/start.sh

print_message "3. 等待服务启动完成..."
sleep 30

print_message "4. 检查服务状态..."
$(dirname "$0")/check.sh

echo
print_message "部署完成！"
print_message "访问地址："
echo "  - 后端API: http://localhost:8080"
echo "  - 前端应用: http://localhost:3000"
echo "  - RabbitMQ管理界面: http://localhost:15672 (admin/123456)"
echo "  - MinIO控制台: http://localhost:9001"
echo
print_message "如需停止服务，请运行: $(dirname "$0")/stop.sh"