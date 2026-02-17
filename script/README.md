# 部署脚本目录

本目录包含了项目的所有部署相关文件，按照功能进行了分类组织。

## 目录结构

```
script/
├── docker/                   # Docker相关文件
│   ├── docker-compose.yml    # Docker Compose编排文件
│   ├── backend/
│   │   └── Dockerfile        # 后端应用Docker镜像构建文件
│   └── frontend/
│       ├── Dockerfile        # 前端应用Docker镜像构建文件
│       └── nginx.conf        # Nginx配置文件
├── sql/                      # 数据库脚本
│   └── init.sql              # 数据库初始化脚本
├── config/                   # 配置文件
│   └── .env.example          # 环境变量配置示例
├── scripts/                  # 平台特定脚本
│   ├── windows/              # Windows脚本
│   │   ├── start.bat         # 启动脚本
│   │   ├── stop.bat          # 停止脚本
│   │   ├── check.bat         # 检查脚本
│   │   └── quick-deploy.bat  # 快速部署脚本
│   └── linux/                # Linux/Mac脚本
│       ├── start.sh          # 启动脚本
│       ├── stop.sh           # 停止脚本
│       ├── check.sh          # 检查脚本
│       └── quick-deploy.sh   # 快速部署脚本
├── quick-deploy.sh/bat       # 统一快速部署入口
└── README.md                 # 本文件
```

## 使用说明

### Windows系统

1. **快速部署**：
   ```cmd
   # 一键完成所有部署步骤
   quick-deploy.bat
   ```

2. **手动操作**：
   ```cmd
   # 启动服务
   scripts\windows\start.bat
   
   # 检查状态
   scripts\windows\check.bat
   
   # 停止服务
   scripts\windows\stop.bat
   ```

### Linux/Mac系统

1. **快速部署**：
   ```bash
   # 给脚本执行权限
   chmod +x scripts/linux/*.sh scripts/quick-deploy.sh
   
   # 一键完成所有部署步骤
   ./quick-deploy.sh
   ```

2. **手动操作**：
   ```bash
   # 启动服务
   ./scripts/linux/start.sh
   
   # 检查状态
   ./scripts/linux/check.sh
   
   # 停止服务
   ./scripts/linux/stop.sh
   ```

## 服务说明

- **后端应用**: Spring Boot应用，端口8080
- **MySQL 8.0**: 数据库服务，端口3306
- **Redis 7**: 缓存服务，端口6379
- **RabbitMQ 3.12**: 消息队列服务，端口5672（API）/15672（管理界面）
- **MinIO**: 对象存储服务，端口9000（API）/9001（控制台）
- **前端应用**: Nginx托管的前端应用，端口3000（可选）

## 配置说明

主要配置文件：

- `docker-compose.yml`: 服务编排配置
- `config/.env.example`: 环境变量配置示例
- `sql/init.sql`: 数据库初始化脚本

### RabbitMQ配置

RabbitMQ相关配置项（在.env文件中）：

```bash
# RabbitMQ配置
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=123456
RABBITMQ_VHOST=/
RABBITMQ_CONSOLE_PORT=15672
```

### 字典缓存配置

字典缓存相关配置项（在application.yml中）：

```yaml
dict:
  cache:
    enabled: true
    default-expire-time: 30
    mq:
      enabled: true
      exchange: dict.cache.exchange
      queue: dict.cache.queue
      routing-key: dict.cache.refresh
    scheduled:
      enabled: true
      refresh-interval: 10
      warm-up-cron: "0 0 2 * * ?"
```

## 常用命令

```bash
# 查看服务状态
cd docker && docker-compose ps

# 查看日志
cd docker && docker-compose logs -f [service_name]

# 重启特定服务
cd docker && docker-compose restart [service_name]

# 进入容器
cd docker && docker-compose exec [service_name] sh

# 查看RabbitMQ队列状态
cd docker && docker-compose exec rabbitmq rabbitmqctl list_queues

# 查看RabbitMQ交换机状态
cd docker && docker-compose exec rabbitmq rabbitmqctl list_exchanges

# 查看RabbitMQ连接状态
cd docker && docker-compose exec rabbitmq rabbitmqctl list_connections

# 查看快速帮助
./quick-deploy.sh   # Linux/Mac
quick-deploy.bat    # Windows
```

## 脚本分类说明

为了更好的组织和管理，脚本文件已按平台分类：

- **scripts/windows/** - Windows平台专用脚本
- **scripts/linux/** - Linux/Mac平台专用脚本
- **根目录脚本** - 统一入口脚本，自动调用对应平台的脚本

## 访问地址

部署完成后，可通过以下地址访问各项服务：

- **后端API**: http://localhost:8080
- **前端应用**: http://localhost:3000
- **API文档**: http://localhost:8080/doc.html
- **RabbitMQ管理界面**: http://localhost:15672 (admin/123456)
- **MinIO控制台**: http://localhost:9001 (admin/12345678)

## 注意事项

1. 确保Docker Desktop在Windows上已安装并运行
2. 首次启动可能需要较长时间下载镜像
3. 数据持久化存储在Docker卷中，停止容器不会丢失数据
4. 如需完全重置，删除数据卷即可
5. Linux/Mac系统使用前需要给脚本执行权限：`chmod +x scripts/linux/*.sh`
6. RabbitMQ管理界面默认账号：admin/123456，可在.env文件中修改

## 故障排除

### RabbitMQ相关问题

1. **RabbitMQ连接失败**：
    - 检查RabbitMQ服务是否正常启动
    - 确认用户名密码配置正确
    - 检查虚拟主机配置

2. **字典缓存不生效**：
    - 确认MQ配置中的`dict.cache.mq.enabled`为true
    - 检查RabbitMQ队列是否正常创建
    - 查看后端日志中的MQ相关错误

3. **消息队列积压**：
    - 检查消费者是否正常启动
    - 查看死信队列是否有消息
    - 考虑增加消费者数量

### 常见解决方案

```bash
# 重启RabbitMQ服务
cd docker && docker-compose restart rabbitmq

# 清空RabbitMQ数据（谨慎操作）
cd docker && docker-compose down -v && docker-compose up -d

# 查看RabbitMQ详细日志
cd docker && docker-compose logs rabbitmq
```