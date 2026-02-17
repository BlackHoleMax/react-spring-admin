#!/bin/bash

# Podman 开发环境停止脚本

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "========================================="
echo "React Spring Admin - 停止 Podman 环境"
echo "========================================="
echo ""

# 进入 docker-compose 目录
cd "$SCRIPT_DIR"

echo "🛑 停止所有服务..."
podman-compose -f podman-compose.yml down

echo ""
echo "✅ 所有服务已停止"
echo ""
echo "如需删除数据卷，请运行:"
echo "  podman volume rm mysql_data redis_data rabbitmq_data minio_data"
echo ""