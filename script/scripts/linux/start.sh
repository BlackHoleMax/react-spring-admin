#!/bin/bash

# Web Template 启动脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Docker和Docker Compose是否安装
check_dependencies() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi

    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi

    print_message "Docker 和 Docker Compose 已安装"
}

# 创建必要的目录
create_directories() {
    print_message "创建必要的目录..."
    mkdir -p logs
    mkdir -p data/mysql
    mkdir -p data/redis
    mkdir -p data/minio
}

# 启动服务
start_services() {
    print_message "启动所有服务..."
    cd ../../docker && docker-compose up -d
    
    if [ $? -eq 0 ]; then
        print_message "所有服务启动成功！"
        print_message "服务访问地址："
        echo "  - 后端API: http://localhost:8080"
        echo "  - 前端应用: http://localhost:3000"
        echo "  - MySQL: localhost:3306"
        echo "  - Redis: localhost:6379"
        echo "  - MinIO控制台: http://localhost:9001"
        echo ""
        print_message "查看服务状态: cd docker && docker-compose ps"
        print_message "查看日志: cd docker && docker-compose logs -f [service_name]"
        print_message "停止服务: $(dirname "$0")/stop.sh"
    else
        print_error "服务启动失败"
        exit 1
    fi
}

# 等待服务就绪
wait_for_services() {
    print_message "等待服务就绪..."
    sleep 10
    
    # 检查后端健康状态
    for i in {1..30}; do
        if curl -f http://localhost:8080/actuator/health &> /dev/null; then
            print_message "后端服务已就绪"
            break
        fi
        if [ $i -eq 30 ]; then
            print_warning "后端服务可能还在启动中，请稍后检查"
        fi
        sleep 2
    done
}

# 主函数
main() {
    print_message "开始启动 Web Template 应用..."
    
    check_dependencies
    create_directories
    start_services
    wait_for_services
    
    print_message "启动完成！"
}

# 执行主函数
main