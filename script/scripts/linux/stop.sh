#!/bin/bash

# Web Template 停止脚本

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_message "停止所有服务..."
cd ../../docker && docker-compose down

if [ $? -eq 0 ]; then
    print_message "所有服务已停止"
    
    # 询问是否删除数据卷
    read -p "是否要删除数据卷？这将删除所有数据 (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_warning "删除数据卷..."
        docker-compose down -v
        print_message "数据卷已删除"
    fi
else
    print_error "停止服务时出错"
    exit 1
fi

print_message "清理完成！"