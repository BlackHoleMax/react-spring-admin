# React Spring Admin - 后端

基于 Spring Boot 3.5.9 的企业级后端服务，采用多模块 Maven 架构，集成了完整的权限管理、系统监控、缓存机制等功能。

## 项目简介

这是一个功能完善的后端管理系统模板，提供了：

- ✅ 基于 RBAC 的权限管理系统（支持按钮级细粒度权限控制）
- ✅ JWT 认证 + Spring Security 安全防护
- ✅ 滑块验证码（TianaiCaptcha）
- ✅ 双重缓存机制（Caffeine + Redis）
- ✅ 异步处理（@Async + 自定义线程池）
- ✅ 定时任务调度（Quartz）
- ✅ 对象存储（MinIO）
- ✅ 操作日志和登录日志（异步记录）
- ✅ 系统监控和健康检查
- ✅ API 文档（Swagger/Knife4j）
- ✅ API 接口限流（Bucket4j）
- ✅ IP 地址定位（ip2region）
- ✅ 代码生成器

## 项目结构

```
backend/
├── pom.xml                           # 父 POM，定义公共依赖和插件
├── rsa-common/                       # 通用模块
│   ├── src/main/java/dev/illichitcat/common/
│   │   ├── common/                   # 常量、属性、结果
│   │   │   ├── constant/             # 常量定义
│   │   │   ├── properties/           # 配置属性类
│   │   │   └── result/               # 统一返回结果
│   │   ├── exception/                # 异常处理
│   │   │   ├── AuthException.java
│   │   │   ├── BizException.java
│   │   │   ├── DaoException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   └── utils/                    # 工具类
│   │       ├── ExcelUtils.java
│   │       ├── IpUtils.java
│   │       ├── JwtUtil.java
│   │       ├── MinioUtils.java
│   │       └── UserAgentUtils.java
│   └── pom.xml
├── rsa-admin/                        # 管理模块（启动模块）
│   ├── src/main/java/dev/illichitcat/
│   │   ├── BackendApplication.java   # 主启动类
│   │   └── api/                      # 控制器层
│   │       ├── AuthController.java   # 认证接口
│   │       ├── CaptchaController.java # 验证码接口
│   │       ├── FileController.java   # 文件上传接口
│   │       ├── CommonController.java # 通用接口
│   │       ├── SystemHealthController.java # 健康检查
│   │       ├── NotFoundController.java # 404 处理
│   │       ├── monitor/              # 监控接口
│   │       ├── system/               # 系统管理接口
│   │       │   ├── UserController.java
│   │       │   ├── RoleController.java
│   │       │   ├── MenuController.java
│   │       │   ├── PermissionController.java
│   │       │   ├── DictController.java
│   │       │   ├── ConfigController.java
│   │       │   ├── JobController.java
│   │       │   ├── JobLogController.java
│   │       │   ├── LoginLogController.java
│   │       │   ├── OperLogController.java
│   │       │   ├── UserOnlineController.java
│   │       │   ├── NoticeController.java
│   │       │   ├── CacheController.java
│   │       │   └── FileController.java
│   │       └── user/                 # 用户管理接口
│   │           ├── ProfileController.java
│   │           └── MyNoticeController.java
│   └── src/main/resources/
│       ├── application.yml           # 主配置文件
│       ├── application-dev.yml       # 开发环境配置
│       ├── application-local.yml     # 本地环境配置
│       ├── background/               # 背景图片资源
│       ├── captcha/slider/           # 验证码模板
│       ├── font/                     # 字体文件
│       └── geoip/                    # IP 定位数据
│   └── pom.xml
├── rsa-system/                       # 系统模块
│   ├── src/main/java/dev/illichitcat/system/
│   │   ├── config/                   # 配置类
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtInterceptor.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── AsyncConfig.java
│   │   │   ├── MinioConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   ├── WebSocketConfig.java
│   │   │   └── RateLimitConfig.java
│   │   ├── dao/mapper/               # MyBatis Mapper
│   │   │   ├── UserMapper.java
│   │   │   ├── RoleMapper.java
│   │   │   ├── MenuMapper.java
│   │   │   └── ...
│   │   ├── listener/task/            # 定时任务监听器
│   │   │   ├── DictCacheScheduledTask.java
│   │   │   ├── RoleCacheScheduledTask.java
│   │   │   ├── MenuCacheScheduledTask.java
│   │   │   ├── PermissionCacheScheduledTask.java
│   │   │   ├── ConfigCacheScheduledTask.java
│   │   │   └── OnlineUserCleanupTask.java
│   │   ├── model/                    # 数据模型
│   │   │   ├── entity/               # 实体类
│   │   │   ├── dto/                  # 数据传输对象
│   │   │   ├── vo/                   # 视图对象
│   │   │   └── query/                # 查询对象
│   │   └── service/                  # 业务逻辑层
│   │       ├── impl/                 # 服务实现
│   │       └── async/                # 异步服务
│   └── src/main/resources/mapper/    # MyBatis XML 映射
│       ├── UserMapper.xml
│       ├── RoleMapper.xml
│       └── ...
├── rsa-generator/                    # 代码生成器模块
│   └── src/main/java/dev/illichitcat/generator/
│       ├── controller/               # 代码生成控制器
│       ├── service/                  # 代码生成服务
│       └── utils/                    # 代码生成工具类
└── rsa-quartz/                       # 定时任务模块
    └── src/main/java/dev/illichitcat/quartz/
        ├── config/                   # Quartz 配置
        └── utils/                    # 任务工具类
```

## 技术栈

### 核心框架

- **Spring Boot**: 3.5.9
- **Spring Security**: 6.x
- **MyBatis Plus**: 3.5.5

### 数据存储

- **MySQL**: 9.3.0
- **Redis**: 6.0+
- **Caffeine**: 本地缓存

### 工具库

- **JWT**: 0.12.3 (jjwt)
- **Lombok**: 简化代码
- **Hutool**: 5.8.40
- **Apache Commons Lang3**: 3.18.0
- **FastExcel**: 1.3.0 (Excel 处理)

### 文档和监控

- **Swagger/Knife4j**: 4.4.0
- **Spring Boot Actuator**: 健康检查和监控

### 其他功能

- **MinIO**: 8.5.7 (对象存储)
- **TianaiCaptcha**: 1.5.3 (验证码)
- **Quartz**: 定时任务
- **WebSocket**: 实时通信
- **IP2Region**: 3.3.1 (IP 定位)
- **Bucket4j**: 8.10.1 (API 限流)

## 环境要求

- **JDK**: 17 或 21（推荐 21 LTS）
- **Maven**: 3.6+
- **MySQL**: 9.3.0+
- **Redis**: 6.0+
- **MinIO**: RELEASE.2023+ (可选)

## 快速开始

### 1. 数据库初始化

```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE simple_admin CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 执行初始化脚本
mysql -u root -p simple_admin < ../script/sql/init.sql
```

### 2. 配置环境变量

复制配置模板并修改：

```bash
cp ../script/config/.env.example ../script/config/.env
```

编辑 `.env` 文件，配置数据库、Redis、MinIO 等信息。

### 3. 启动后端服务

```bash
# 方式一：使用 Maven 启动
cd backend
mvn clean install
cd rsa-admin
mvn spring-boot:run

# 方式二：打包后启动
cd backend
mvn clean package
java -jar rsa-admin/target/rsa-admin-0.0.1-SNAPSHOT.jar
```

### 4. 访问服务

- **API 文档**: http://localhost:8080/doc.html
- **健康检查**: http://localhost:8080/actuator/health
- **系统监控**: http://localhost:8080/actuator/metrics

### 5. 默认账号

- 用户名: `admin`
- 密码: `admin123`

## 核心功能

### 1. 权限管理（RBAC）

- 用户管理：增删改查、角色分配、状态管理
- 角色管理：角色权限分配、菜单权限
- 菜单管理：动态菜单、权限控制
- 权限管理：接口权限、按钮级细粒度权限控制

### 2. 安全认证

- JWT 认证：无状态认证
- 滑块验证码：防止暴力破解
- 密码加密：BCrypt 加密
- 登录日志：记录登录信息（异步）
- 操作日志：记录用户操作（异步）
- API 限流：防止恶意请求

### 3. 缓存机制

- **双重缓存**：Caffeine 一级缓存 + Redis 二级缓存
- **异步刷新**：定时任务预热 + 异步刷新
- **缓存管理**：提供缓存清除和预热接口
- **缓存监控**：查看缓存状态和统计信息

### 4. 定时任务

- 支持 Cron 表达式
- 任务调度中心
- 任务暂停/恢复
- 执行日志查看

### 5. 通知公告

- 公告类型管理
- 发布范围控制
- 有效期管理
- 已读未读状态追踪
- WebSocket 实时推送

### 6. 系统监控

- JVM 信息监控
- 系统指标监控
- 健康检查
- 在线用户管理
- 缓存监控

### 7. 文件管理

- MinIO 对象存储
- 用户头像上传
- 文件预览和下载
- 文件分类管理

### 8. 代码生成器

- 基于数据库表自动生成前后端代码
- 支持自定义模板
- 批量代码生成
- 代码预览和下载

## 配置说明

### 主配置文件

`rsa-admin/src/main/resources/application.yml`

```yaml
server:
  port: 8080

spring:
  application:
    name: backend-template
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD}
      database: ${REDIS_DATABASE}
  threads:
    virtual:
      enabled: true

jwt:
  expiration: 86400  # JWT 过期时间（秒）

minio:
  endpoint: ${MINIO_ENDPOINT}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket-name: ${MINIO_BUCKET_NAME}

# 字典缓存配置
dict:
  cache:
    enabled: true
    caffeine:
      enabled: true
      maximum-size: 1000
      expire-after-write: 10
    redis:
      enabled: true
      expire-time: 30
    scheduled:
      enabled: true
      warm-up-cron: "0 0 2 * * ?"

# API接口限流配置
rate-limit:
  enabled: true
  time: 60  # 默认时间窗口（秒）
  count: 100  # 默认时间窗口内允许的最大请求数
  alert-enabled: false
  alert-threshold: 80
```

### 环境变量

在 `.env` 文件中配置：

```bash
# 数据库配置
DB_URL=jdbc:mysql://localhost:3306/simple_admin?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
DB_USERNAME=root
DB_PASSWORD=your_password

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DATABASE=0

# MinIO 配置
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=admin
MINIO_SECRET_KEY=12345678
MINIO_BUCKET_NAME=react-spring-admin
```

## 开发指南

### 代码规范

1. **包命名规范**
    - 基础包名: `dev.illichitcat`
    - 控制器: `dev.illichitcat.api`
    - 服务层: `dev.illichitcat.system.service`
    - 数据访问: `dev.illichitcat.system.dao.mapper`

2. **接口规范**
    - 统一返回格式: `Result<T>`
    - 异常处理: `GlobalExceptionHandler`
    - 参数验证: JSR-303 注解

3. **数据库规范**
    - 表名前缀: `sys_`
    - 主键: `id`（自增）
    - 公共字段: `create_time`、`update_time`、`del_flag`

### 添加新功能

1. **创建实体类**
   ```java
   @Data
   @TableName("sys_example")
   public class Example extends BaseEntity {
       @TableId(type = IdType.AUTO)
       private Long id;
       private String name;
       // 其他字段...
   }
   ```

2. **创建 Mapper**
   ```java
   @Mapper
   public interface ExampleMapper extends BaseMapper<Example> {
   }
   ```

3. **创建 Service**
   ```java
   public interface ExampleService extends IService<Example> {
   }

   @Service
   public class ExampleServiceImpl extends ServiceImpl<ExampleMapper, Example> implements ExampleService {
   }
   ```

4. **创建 Controller**
   ```java
   @RestController
   @RequestMapping("/api/system/example")
   @Tag(name = "示例管理")
   public class ExampleController {
       @Autowired
       private ExampleService exampleService;

       @GetMapping("/list")
       @Operation(summary = "获取列表")
       public Result<IPage<ExampleVO>> list(ExampleQuery query) {
           // ...
       }
   }
   ```

## 测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 生成测试覆盖率报告
mvn test jacoco:report
```

### 测试覆盖率

- 目标覆盖率: > 70%
- 使用 JUnit 5 + Mockito

## 部署

### Docker 部署

```bash
# 使用项目提供的 Docker Compose
cd ../script/docker
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

### Podman 部署

```bash
# 使用项目提供的 Podman Compose
cd ../script/podman
podman-compose up -d

# 查看服务状态
podman-compose ps

# 查看日志
podman-compose logs -f backend
```

### 手动部署

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar rsa-admin/target/rsa-admin-0.0.1-SNAPSHOT.jar

# 指定配置文件
java -jar rsa-admin/target/rsa-admin-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 常见问题

### 1. 数据库连接失败

检查 MySQL 服务是否启动，确认配置文件中的数据库地址、端口、用户名、密码是否正确。

### 2. Redis 连接超时

检查 Redis 服务是否启动，确认配置文件中的 Redis 地址和端口。

### 3. 字典缓存不生效

确认缓存配置中的 `dict.cache.enabled` 为 true，检查 Redis 连接是否正常。

### 4. 验证码加载失败

检查 `backend/src/main/resources/captcha/slider` 目录下是否有模板文件，确认文件路径配置是否正确。

## 性能优化

1. **双重缓存**
    - Caffeine 一级缓存：本地高性能缓存
    - Redis 二级缓存：分布式缓存
    - 自动刷新：定时任务 + 异步刷新

2. **数据库优化**
    - 使用 MyBatis Plus 简化 CRUD
    - 合理使用索引
    - 分页查询避免全表扫描

3. **异步处理**
    - 使用 @Async + 自定义线程池处理耗时操作
    - 操作日志异步记录
    - 字典缓存异步刷新

4. **API 限流**
    - 使用 Bucket4j 实现令牌桶算法
    - 防止恶意请求
    - 保护系统稳定性

## 架构决策

### 异步处理策略

项目遵循 YAGNI 原则，根据实际需求选择合适的异步处理方案：

1. **同步处理（80%场景）**：大多数业务逻辑直接同步调用
2. **简单异步（15%场景）**：使用 `@Async` + 自定义线程池（如日志记录、缓存刷新）
3. **进程内解耦（4%场景）**：使用 Spring 事件机制
4. **外部集成（1%场景）**：仅在真正需要时引入 MQ

## API 文档

启动服务后访问：http://localhost:8080/doc.html

## 许可证

MIT License