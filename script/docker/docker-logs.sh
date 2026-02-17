#!/bin/bash

# Docker 日志查看脚本

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"

if [ -z "$1" ]; then
    echo "查看所有服务日志 (按 Ctrl+C 退出)"
    docker-compose logs -f
else
    echo "查看 $1 服务日志 (按 Ctrl+C 退出)"
    docker-compose logs -f "$1"
fi