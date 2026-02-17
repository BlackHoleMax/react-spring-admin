# react-spring-admin

这是一个基于前后端分离架构的全栈项目模板，提供了完整的用户权限管理、系统监控、配置管理等基础功能模块。

## 项目特点

- 🚀 **前后端分离架构**：React + Spring Boot
- 🔐 **完整的权限管理**：基于RBAC模型的角色权限系统，支持按钮级细粒度权限控制
- 🎨 **现代化UI设计**：支持明暗主题切换
- 📊 **系统监控**：提供系统状态、健康检查等监控功能
- 🛡️ **安全防护**：JWT认证、验证码、操作日志等安全机制
- 📱 **响应式设计**：适配多种设备屏幕
- 🔧 **高度可配置**：支持动态配置系统参数
- ⚡ **代码生成器**：基于数据库表自动生成前后端代码，支持自定义模板

## 技术栈

### 后端技术

- **框架**: Spring Boot 3.4.12
- **数据库**: MySQL 8.0.33 + MyBatis Plus 3.5.5
- **缓存**: Redis + Caffeine（双重缓存机制）
- **异步处理**: Spring @Async + 自定义线程池
- **认证**: JWT 0.12.3 + Spring Security
- **文档**: Swagger/Knife4j
- **存储**: MinIO
- **验证码**: TianaiCaptcha
- **定时任务**: Quartz
- **实时通信**: WebSocket

### 前端技术

- **框架**: React 19.2.0 + TypeScript 5.9.3
- **状态管理**: Redux Toolkit 2.11.2
- **UI组件**: Ant Design 6.1.1
- **图标**: @ant-design/icons 6.1.0
- **样式**: Tailwind CSS 4.1.18
- **路由**: React Router 7.10.1
- **HTTP客户端**: Axios 1.13.2
- **构建工具**: Vite (rolldown-vite 7.2.5)

## 项目结构

```
react-spring-admin/
├── backend/                    # 后端项目（Spring Boot 多模块）
│   ├── pom.xml                 # 父 POM
│   ├── rsa-common/             # 通用模块（常量、工具类、异常处理）
│   │   └── src/main/java/dev/illichitcat/
│   │       ├── common/         # 通用组件
│   │       │   ├── constant/   # 常量定义
│   │       │   ├── exception/  # 异常处理
│   │       │   └── result/     # 统一返回结果
│   │       └── utils/          # 工具类
│   ├── rsa-admin/              # 管理模块（启动模块、控制器）
│   │   └── src/main/java/dev/illichitcat/
│   │       ├── BackendApplication.java  # 主启动类
│   │       └── api/            # 控制器层
│   │           ├── AuthController      # 认证接口
│   │           ├── CaptchaController   # 验证码接口
│   │           ├── FileController      # 文件接口
│   │           ├── UserController      # 用户接口
│   │           └── system/            # 系统管理接口
│   ├── rsa-system/             # 系统模块（Service、Mapper、Config）
│   │   └── src/main/java/dev/illichitcat/system/
│   │       ├── config/        # 配置类
│   │       ├── dao/mapper/    # 数据访问层
│   │       ├── listener/task/ # 定时任务监听器
│   │       ├── model/         # 数据模型
│   │       └── service/       # 业务逻辑层
│   ├── rsa-generator/         # 代码生成器模块
│   │   └── src/main/java/dev/illichitcat/generator/
│   │       ├── controller/    # 代码生成控制器
│   │       ├── service/       # 代码生成服务
│   │       └── utils/         # 代码生成工具类
│   └── rsa-quartz/            # 定时任务模块
│       └── src/main/java/dev/illichitcat/quartz/
│           ├── config/        # Quartz 配置
│           └── utils/         # 任务工具类
└── frontend/                   # 前端项目（React + TypeScript）
    ├── public/                 # 静态资源
    ├── src/
    │   ├── components/         # 通用组件
    │   │   ├── DayNightToggle   # 主题切换组件
    │   │   ├── TianaiCaptcha    # 验证码组件
    │   │   ├── PrivateRoute     # 路由守卫
    │   │   └── Authorized       # 权限控制组件
    │   ├── hooks/               # 自定义Hook
    │   │   ├── usePermission    # 权限检查Hook
    │   │   └── useSessionTimeout # 会话超时Hook
    │   ├── layouts/             # 布局组件
    │   │   └── MainLayout       # 主布局
    │   ├── pages/               # 页面组件
    │   │   ├── Login.tsx        # 登录页
    │   │   ├── Dashboard/       # 仪表盘
    │   │   ├── User/            # 用户管理
    │   │   ├── Role/            # 角色管理
    │   │   ├── Menu/            # 菜单管理
    │   │   ├── Permission/      # 权限管理
    │   │   ├── Dict/            # 字典管理
    │   │   ├── Job/             # 定时任务
    │   │   ├── JobLog/          # 任务日志
    │   │   ├── Notice/          # 通知公告
    │   │   ├── LoginLog/        # 登录日志
    │   │   ├── OperLog/         # 操作日志
    │   │   ├── Online/          # 在线用户
    │   │   ├── Monitor/         # 系统监控
    │   │   ├── ApiDoc/          # API文档
    │   │   ├── Settings/        # 系统设置
    │   │   ├── Profile/         # 个人中心
    │   │   ├── CacheMonitor/    # 缓存监控
    │   │   ├── CacheList/       # 缓存列表
    │   │   ├── File/            # 文件管理
    │   │   ├── CodeGen/         # 代码生成器
    │   │   └── NotFound/        # 404页面
    │   ├── services/            # API服务层
    │   ├── store/               # Redux状态管理
    │   │   ├── slices/          # Redux Slices
    │   │   │   ├── authSlice       # 认证状态
    │   │   │   ├── menuSlice       # 菜单状态
    │   │   │   ├── permissionSlice # 权限状态
    │   │   │   ├── themeSlice      # 主题状态
    │   │   │   └── sessionSlice    # 会话状态
    │   ├── types/               # TypeScript类型定义
    │   └── utils/               # 工具函数
    ├── package.json
    └── vite.config.ts
```

## 核心功能

### ✅ 已实现功能

1. **用户管理**
    - 用户增删改查
    - 用户角色分配
    - 用户状态管理
    - 个人信息修改

2. **权限管理**
    - 基于RBAC的角色权限模型
    - 菜单权限控制
    - 接口权限验证
    - 动态路由生成
    - 按钮级细粒度权限控制
    - 权限组件（Authorized）和权限Hook（usePermission）
    - Redux权限状态管理

3. **系统管理**
    - 角色管理
    - 菜单管理
    - 字典管理
    - 权限管理

4. **安全功能**
    - JWT认证
    - 滑块验证码
    - 操作日志记录
    - 登录日志管理

5. **系统监控**
    - 系统状态监控
    - 健康检查
    - JVM信息监控
    - 系统指标监控

6. **配置管理**
    - 系统参数配置
    - 验证码开关配置
    - 主题配置

7. **对象存储**
    - MinIO集成
    - 用户头像上传
    - 文件管理

8. **字典缓存**
    - Redis + Caffeine 双重缓存机制
    - 定时任务缓存预热
    - 异步缓存刷新
    - 缓存管理接口

## 非功能性需求

### 性能要求
- 支持并发用户数：1000+
- API响应时间：95%请求 < 200ms
- 页面加载时间：首屏 < 2s
- 数据库连接池：最大连接数 100，初始连接数 10

### 安全要求
- 密码策略：最少8位，包含大小写字母、数字、特殊字符
- 会话超时：30分钟无操作自动登出
- 登录失败限制：5次失败后锁定账户30分钟
- SQL注入防护：使用MyBatis预编译语句
- XSS防护：前端输入过滤、后端输出转义
- CSRF防护：Token验证机制

### 可用性要求
- 系统可用性目标：99.5%
- 服务恢复时间：< 5分钟
- 数据备份：每日自动备份，保留30天

### 兼容性要求
- **浏览器支持**：Chrome 90+、Firefox 88+、Edge 90+、Safari 14+
- **移动端支持**：iOS 14+、Android 10+
- **分辨率支持**：1920x1080、1366x768、移动端自适应

### 可维护性要求
- 代码注释率：核心模块 > 30%
- 单元测试覆盖率：> 70%
- 接口文档完整性：100%

### 📋 待实现功能

#### 1. 系统增强 [P0-高优先级]
- [x] 字典管理页面增强
- [x] 增加用户性别字段（性别字典）
- [x] 字典数据缓存自动刷新（消息队列/定时任务）
- [x] **通知公告** - 支持公告类型（系统公告、活动通知）、发布范围（全部用户、指定角色）、有效期管理、已读未读状态追踪
- [x] **定时任务** - 支持Cron表达式、固定延迟、固定速率三种任务类型，提供任务调度中心页面，支持任务暂停/恢复、立即执行、执行日志查看
- [x] **文件管理** - MinIO桶监控与管理，支持文件预览、下载、删除、批量操作，文件分类管理，存储空间统计
- [x] **在线用户** - 实时在线用户列表，支持强制下线、会话管理、登录地点显示（基于IP定位）
- [x] **缓存监控** - Redis服务器状态监控、内存使用情况、命令执行统计
- [x] **缓存列表** - Redis Key管理，支持Key搜索、值查看、TTL设置、批量删除

#### 2. 缓存优化 [P1-中优先级]
- [x] **高频数据缓存** - 动态菜单缓存、分页查询结果缓存、热点数据自动识别与缓存
- [x] **API接口限流** - 基于用户/IP的限流策略，支持令牌桶算法、漏桶算法，限流告警

#### 3. 前端优化 [P1-中优先级]
- [x] **按钮权限控制** - 基于角色权限的按钮显示/隐藏，细粒度的操作权限控制
- [x] **菜单图标完善** - 为所有菜单项配置合适的图标，支持自定义图标上传

#### 4. 高级功能 [P2-低优先级]
- [ ] **多租户支持** - 租户数据隔离、租户配置管理、租户用户管理
- [ ] **邮件功能** - 邮件模板管理、邮件发送记录、支持SMTP/阿里云邮件服务
- [ ] **短信功能** - 短信模板管理、验证码发送、短信发送记录（集成阿里云/腾讯云）
- [ ] **工作流引擎** - 流程设计器、流程实例管理、待办任务管理
- [x] **数据导入导出** - Excel批量导入导出、模板下载、导入校验

#### 5. 系统工具 [P2-低优先级]
- [x] **代码生成器** - 基于数据库表自动生成前后端代码（Entity、Mapper、Service、Controller、React页面），支持自定义模板
- [x] **系统接口文档** - 在线API文档管理、接口测试、接口Mock
- [ ] **SQL监控** - 慢SQL分析、SQL执行计划、SQL性能优化建议
- [x] **日志分析** - 操作日志统计分析、异常日志聚合、日志导出

## 快速开始

### 环境要求

- **JDK**: 17 或 21 (推荐 21 LTS)
- **Node.js**: 18.x 或 20.x LTS
- **MySQL**: 8.0.33+
- **Redis**: 6.0+
- **MinIO**: RELEASE.2023+ (可选，用于对象存储)

### 后端启动

1. mysql数据库执行初始化脚本：
   backend/src/main/resources/sql/init.sql

2. 修改配置文件 `backend/src/main/resources/application.yml`：
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/simple_admin
       username: your_username
       password: your_password
     data:
       redis:
         host: localhost
         port: 6379
   ```

3. 启动后端服务：
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. 访问API文档：http://localhost:8080/doc.html

### 前端启动

1. 安装依赖：
   ```bash
   cd frontend
   npm install
   ```

2. 启动开发服务器：
   ```bash
   npm run dev
   ```

3. 访问前端应用：http://localhost:5173

### 默认账号

- 用户名：admin
- 密码：admin123

## 版本规划

### 当前版本：v1.1.0 (2026-01-15)

**已发布功能**：
- ✅ 用户管理、角色管理、菜单管理
- ✅ 权限管理、字典管理
- ✅ 按钮级细粒度权限控制
- ✅ JWT认证、滑块验证码
- ✅ 操作日志、登录日志（异步记录）
- ✅ 系统监控（状态、健康检查、JVM）
- ✅ MinIO文件上传
- ✅ Redis + Caffeine 双重缓存
- ✅ 异步线程池处理
- ✅ 通知公告模块（WebSocket实时推送）
- ✅ 定时任务调度（Quartz）
- ✅ 在线用户管理
- ✅ 缓存监控与列表
- ✅ 代码生成器模块

### v1.1.0 - 已发布 (2026-01-15)
- ✅ 通知公告模块
- ✅ 定时任务调度
- ✅ 在线用户管理
- ✅ 缓存监控与列表
- ✅ 按钮级细粒度权限控制
- ✅ 代码生成器模块
- ✅ WebSocket实时通知
- ✅ 文件管理（MinIO桶监控）

### v1.2.0 计划中
- 📅 数据导入导出
- 📅 SQL监控
- 📅 多套首页模板
- 📅 国际化支持

### v2.0.0 长期规划
- 📅 多租户支持
- 📅 工作流引擎
- 📅 邮件/短信功能
- 📅 微服务架构改造

## 变更日志

### [1.1.0] - 2026-01-15
**新增**
- 按钮级细粒度权限控制
  - 新增权限组件：frontend/src/components/Authorized.tsx
  - 新增权限Hook：frontend/src/hooks/usePermission.ts
  - 新增权限状态管理：frontend/src/store/slices/permissionSlice.ts
  - 新增权限服务：frontend/src/services/permission.ts
  - 为所有管理页面添加权限控制（用户、角色、菜单、权限、字典、任务、日志等）
- 代码生成器模块
  - 新增 rsa-generator 模块，支持基于数据库表自动生成前后端代码
  - 提供代码生成器管理页面，支持导入表、预览代码、批量下载
  - 生成的前端代码自动包含权限控制
- 通知公告模块
  - 支持公告类型管理（系统公告、活动通知）
  - 支持发布范围控制（全部用户、指定角色）
  - 支持有效期管理和已读未读状态追踪
  - WebSocket实时推送通知
- 定时任务调度
  - 支持Cron表达式、固定延迟、固定速率三种任务类型
  - 提供任务调度中心页面
  - 支持任务暂停/恢复、立即执行、执行日志查看
- 在线用户管理
  - 实时在线用户列表
  - 支持强制下线、会话管理
  - 登录地点显示（基于IP定位）
- 缓存监控与列表
  - Redis服务器状态监控
  - Redis Key管理，支持搜索、查看、删除、TTL设置
  - 缓存数据统计

**优化**
- 修复 RolePermServiceImpl 循环依赖问题
- WebSocket 登出时立即断开连接
- 添加 ClientAbortException 处理器，降低日志级别
- 优化权限加载逻辑，用户认证成功后自动加载权限
- 使用现有的权限标识，避免新增细粒度权限

**重构**
- 将 RabbitMQ 消息队列替换为 Spring @Async + 自定义线程池
- 移除所有 MQ 相关依赖和配置
- 操作日志、登录日志改用异步方式记录
- 新增 AsyncConfig 异步线程池配置
- 优化单体应用架构，降低系统复杂度

**删除**
- RabbitMQ 依赖
- MQ 相关配置类（RabbitMqConfig、OperLogMqConfig、LoginLogMqConfig）
- MQ 生产者和消费者类
- MQ 消息模型类
- MQ 监控功能

### [1.0.0] - 2025-01-30
**新增**
- 完整的RBAC权限管理系统
- 用户、角色、菜单、权限管理模块
- JWT认证与滑块验证码
- 操作日志与登录日志
- 系统监控面板
- MinIO对象存储集成
- Redis字典缓存机制
- RabbitMQ消息队列支持

**优化**
- 前后端分离架构
- 响应式UI设计
- 明暗主题切换

## 测试说明

### 运行测试

#### 后端测试
```bash
cd backend
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 生成测试覆盖率报告
mvn test jacoco:report
```

#### 前端测试
```bash
cd frontend
# 运行单元测试
npm run test

# 运行测试并生成覆盖率报告
npm run test:coverage

# 运行E2E测试
npm run test:e2e
```

### 测试策略

#### 单元测试
- 后端：使用JUnit 5 + Mockito
- 前端：使用Vitest + React Testing Library
- 目标覆盖率：> 70%

#### 集成测试
- API接口测试
- 数据库操作测试
- 缓存一致性测试

#### E2E测试
- 关键业务流程测试
- 登录流程
- 权限验证流程

### 测试数据管理
- 使用H2内存数据库进行单元测试
- 测试数据通过Testcontainers容器化MySQL
- 测试完成后自动清理数据

## 故障排查指南

### 常见问题

#### 1. 数据库连接失败
**症状**：启动时报错 `Communications link failure`

**解决方案**：
- 检查MySQL服务是否启动：`net start mysql`
- 确认配置文件中的数据库地址、端口、用户名、密码是否正确
- 检查防火墙是否阻止了3306端口
- 尝试使用MySQL客户端工具连接测试

#### 2. Redis连接超时
**症状**：启动时报错 `Unable to connect to Redis`

**解决方案**：
- 检查Redis服务是否启动：`redis-cli ping`
- 确认配置文件中的Redis地址和端口（默认6379）
- 检查Redis是否设置了密码，如有需在配置中添加
- 尝试重启Redis服务

#### 4. 前端构建失败
**症状**：`npm run build` 报错

**解决方案**：
- 清除node_modules和缓存：
  ```bash
  rm -rf node_modules package-lock.json
  npm cache clean --force
  npm install
  ```
- 检查Node.js版本是否符合要求
- 查看具体错误信息，可能是依赖版本冲突

#### 5. 后端启动失败
**症状**：`mvn spring-boot:run` 报错

**解决方案**：
- 检查JDK版本是否为17或21：`java -version`
- 清理Maven缓存并重新构建：
  ```bash
  mvn clean install -U
  ```
- 检查端口8080是否被占用：`netstat -ano | findstr 8080`
- 查看详细错误日志，定位具体问题

#### 6. 验证码加载失败
**症状**：登录页验证码无法显示

**解决方案**：
- 检查 `backend/src/main/resources/captcha/slider` 目录下是否有模板文件
- 确认文件路径配置是否正确
- 检查文件权限

#### 7. 文件上传失败
**症状**：上传头像或文件时报错

**解决方案**：
- 检查MinIO服务是否启动
- 确认MinIO配置（endpoint、accessKey、secretKey、bucket）
- 检查bucket是否存在，不存在需手动创建
- 验证网络连接是否正常

### 日志查看

#### 后端日志
- 开发环境：控制台输出
- 生产环境：查看 `logs/application.log`
- 错误日志：`logs/error.log`

#### 前端日志
- 浏览器开发者工具 Console
- 网络请求：Network 标签页

### 性能问题排查

#### 响应慢
- 开启SQL日志：`logging.level.com.example.demo.dao.mapper=DEBUG`
- 检查慢查询
- 使用Redis缓存热点数据
- 优化数据库索引

#### 内存占用高
- 使用JVM参数调整堆内存：`-Xms512m -Xmx1024m`
- 检查内存泄漏：`jmap -histo <pid>`
- 分析堆转储文件

### 获取帮助
- 查看项目Issues：https://gitee.com/Illichitcat/web-template/issues
- 提交Bug报告时请提供：环境信息、错误日志、复现步骤

## 开发规范

### 后端开发规范

1. **包命名规范**
    - 控制器：`com.example.demo.api`
    - 服务层：`com.example.demo.service`
    - 数据访问：`com.example.demo.dao.mapper`
    - 实体类：`com.example.demo.model.entity`

2. **接口规范**
    - 统一返回格式：`Result<T>`
    - 异常处理：`GlobalExceptionHandler`
    - 参数验证：JSR-303注解

3. **数据库规范**
    - 表名前缀：`sys_`
    - 主键：`id`（自增）
    - 公共字段：`create_time`、`update_time`、`del_flag`

### 前端开发规范

1. **组件命名**
    - 页面组件：PascalCase
    - 工具函数：camelCase
    - 常量：UPPER_SNAKE_CASE

2. **文件组织**
    - 页面：`pages/模块名/index.tsx`
    - 组件：`components/组件名.tsx`
    - 服务：`services/模块名.ts`

3. **状态管理**
    - 使用Redux Toolkit
    - 异步操作：createAsyncThunk
    - 模块化slice

### Git 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

**提交格式**：
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type 类型**：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式（不影响代码运行的变动）
- `refactor`: 重构（既不是新增功能，也不是修改bug）
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

**示例**：
```bash
feat(user): 增加用户头像上传功能

- 支持图片格式：jpg、png、jpeg
- 限制文件大小：2MB
- 自动压缩图片

Closes #123
```

```bash
fix(auth): 修复JWT token过期时间计算错误

修复了token过期时间不正确的问题，导致用户会话异常提前结束
```

### API 设计规范

遵循 RESTful API 设计原则：

1. **URL 命名规范**
   - 使用小写字母和连字符：`/api/v1/user-management`
   - 使用复数形式表示资源：`/api/v1/users`
   - 版本控制：`/api/v1/`、`/api/v2/`

2. **HTTP 方法规范**
   - `GET`: 获取资源
   - `POST`: 创建资源
   - `PUT`: 更新整个资源
   - `PATCH`: 部分更新资源
   - `DELETE`: 删除资源

3. **接口示例**
   ```
   GET    /api/v1/users              # 获取用户列表
   GET    /api/v1/users/{id}         # 获取单个用户
   POST   /api/v1/users              # 创建用户
   PUT    /api/v1/users/{id}         # 更新用户
   PATCH  /api/v1/users/{id}         # 部分更新用户
   DELETE /api/v1/users/{id}         # 删除用户
   ```

4. **请求参数规范**
   - 查询参数：`?page=1&size=10&keyword=test`
   - 路径参数：`/api/v1/users/{id}`
   - 请求体：JSON格式，`Content-Type: application/json`

5. **响应格式规范**
   ```json
   {
     "code": 200,
     "message": "success",
     "data": {},
     "timestamp": 1706587200000
   }
   ```

6. **错误码规范**
   - 200: 成功
   - 400: 请求参数错误
   - 401: 未授权
   - 403: 禁止访问
   - 404: 资源不存在
   - 500: 服务器内部错误
   - 自定义业务错误码：10001-19999

## API文档

启动后端服务后，可通过以下地址访问API文档：

- Swagger UI：http://localhost:8080/doc.html
- OpenAPI JSON：http://localhost:8080/v3/api-docs

## 部署说明

### Docker部署（推荐）

1. 使用Docker Compose一键部署：
   ```bash
   cd script/docker
   docker-compose up -d
   ```

2. 查看服务状态：
   ```bash
   docker-compose ps
   ```

3. 查看日志：
   ```bash
   docker-compose logs -f backend
   ```

4. 停止服务：
   ```bash
   docker-compose down
   ```

5. 清理数据和容器：
   ```bash
   docker-compose down -v
   ```

**服务访问地址**：

- 后端API：http://localhost:8080
- 前端应用：http://localhost:3000
- API文档：http://localhost:8080/doc.html
- MinIO控制台：http://localhost:9001 (admin/12345678)

### 后端部署

1. 打包应用：
   ```bash
   mvn clean package
   ```

2. 运行JAR：
   ```bash
   java -jar target/backend-template.jar
   ```

### 前端部署

1. 构建生产版本：
   ```bash
   npm run build
   ```

2. 部署dist目录到Web服务器

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请提交 Issue 或联系项目维护者。