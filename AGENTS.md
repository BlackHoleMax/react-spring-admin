# 项目上下文 - React Spring Admin

## 项目概述

**项目名称**: React Spring Admin

**项目类型**: 全栈管理系统（前后端分离架构）

**项目描述**: 这是一个基于前后端分离架构的企业级管理系统模板，提供了完整的用户权限管理、系统监控、配置管理等基础功能模块。采用
React + Spring Boot 技术栈，支持明暗主题切换、JWT认证、滑块验证码、操作日志等安全机制。

**技术架构**:

- **前端**: React 19.2.0 + TypeScript 5.9.3 + Ant Design 6.1.1 + Tailwind CSS 4.1.18 + Redux Toolkit 2.11.2 + React
  Router 7.10.1
- **后端**: Spring Boot 3.5.9 + MyBatis Plus 3.5.5 + Spring Security + JWT 0.12.3 + Quartz
- **数据库**: MySQL 9.3.0
- **缓存**: Redis + Caffeine（双重缓存机制）
- **对象存储**: MinIO 8.5.7
- **API文档**: Swagger/Knife4j 4.4.0
- **限流**: Bucket4j 8.10.1
- **IP定位**: ip2region 3.3.1

**包名/命名空间**:

- 后端 Java 包: `dev.illichitcat`
- 前端包名: `frontend`

## 项目结构

```
react-spring-admin/
├── backend/                          # 后端项目（Spring Boot 多模块）
│   ├── pom.xml                       # 父 POM
│   ├── rsa-common/                   # 通用模块（常量、工具类、异常处理）
│   ├── rsa-admin/                    # 管理模块（启动模块、控制器）
│   │   └── src/main/java/dev/illichitcat/
│   │       ├── BackendApplication.java  # 主启动类
│   │       └── api/                  # 控制器层
│   │           ├── AuthController.java      # 认证接口
│   │           ├── CaptchaController.java   # 验证码接口
│   │           ├── FileController.java      # 文件接口
│   │           ├── CommonController.java    # 通用接口
│   │           ├── SystemHealthController.java  # 系统健康检查
│   │           ├── monitor/            # 监控接口
│   │           ├── system/             # 系统管理接口
│   │           └── user/               # 用户管理接口
│   ├── rsa-system/                   # 系统模块（Service、Mapper、Config）
│   │   └── src/main/java/dev/illichitcat/system/
│   │       ├── config/               # 配置类
│   │       ├── dao/mapper/           # 数据访问层
│   │       ├── listener/task/        # 定时任务监听器
│   │       ├── model/                # 数据模型
│   │       └── service/              # 业务逻辑层
│   ├── rsa-generator/               # 代码生成器模块
│   │   └── src/main/java/dev/illichitcat/generator/
│   │       ├── controller/           # 代码生成控制器
│   │       ├── service/              # 代码生成服务
│   │       └── utils/                # 代码生成工具类
│   └── rsa-quartz/                  # 定时任务模块
│       └── src/main/java/dev/illichitcat/quartz/
│           ├── config/               # Quartz 配置
│           └── utils/                # 任务工具类
├── frontend/                         # 前端项目（React + TypeScript）
│   ├── src/
│   │   ├── components/               # 通用组件
│   │   │   ├── DayNightToggle.tsx    # 主题切换
│   │   │   ├── TianaiCaptcha.tsx     # 验证码组件
│   │   │   ├── PrivateRoute.tsx      # 路由守卫
│   │   │   ├── Authorized.tsx        # 权限控制组件
│   │   │   └── CodePreview.tsx       # 代码预览组件
│   │   ├── hooks/                    # 自定义Hook
│   │   │   ├── usePermission.ts      # 权限检查Hook
│   │   │   └── useSessionTimeout.ts  # 会话超时Hook
│   │   ├── layouts/                  # 布局组件
│   │   │   └── MainLayout.tsx        # 主布局
│   │   ├── pages/                    # 页面组件
│   │   │   ├── Login.tsx             # 登录页
│   │   │   ├── Dashboard/            # 仪表盘
│   │   │   ├── User/                 # 用户管理
│   │   │   ├── Role/                 # 角色管理
│   │   │   ├── Menu/                 # 菜单管理
│   │   │   ├── Permission/           # 权限管理
│   │   │   ├── Dict/                 # 字典管理
│   │   │   ├── Job/                  # 定时任务
│   │   │   ├── JobLog/               # 任务日志
│   │   │   ├── Notice/               # 通知公告
│   │   │   ├── LoginLog/             # 登录日志
│   │   │   ├── OperLog/              # 操作日志
│   │   │   ├── Online/               # 在线用户
│   │   │   ├── Monitor/              # 系统监控
│   │   │   ├── ApiDoc/               # API文档
│   │   │   ├── Settings/             # 系统设置
│   │   │   ├── Profile/              # 个人中心
│   │   │   ├── CacheMonitor/         # 缓存监控
│   │   │   ├── CacheList/            # 缓存列表
│   │   │   ├── File/                 # 文件管理
│   │   │   ├── CodeGen/              # 代码生成器
│   │   │   ├── Category/             # 分类管理
│   │   │   ├── Product/              # 产品管理
│   │   │   ├── Orders/               # 订单管理
│   │   │   └── NotFound/             # 404页面
│   │   ├── services/                 # API服务层
│   │   │   ├── auth.ts
│   │   │   ├── cache.ts
│   │   │   ├── dashboard.ts
│   │   │   ├── dict.ts
│   │   │   ├── file.ts
│   │   │   ├── gen.ts
│   │   │   ├── job.ts
│   │   │   ├── jobLog.ts
│   │   │   ├── loginLog.ts
│   │   │   ├── menu.ts
│   │   │   ├── monitor.ts
│   │   │   ├── notice.ts
│   │   │   ├── online.ts
│   │   │   ├── operLog.ts
│   │   │   ├── permission.ts
│   │   │   ├── profile.ts
│   │   │   ├── role.ts
│   │   │   ├── settings.ts
│   │   │   └── user.ts
│   │   ├── store/                    # Redux状态管理
│   │   │   ├── slices/               # Redux Slices
│   │   │   │   ├── authSlice
│   │   │   │   ├── menuSlice
│   │   │   │   ├── permissionSlice
│   │   │   │   ├── themeSlice
│   │   │   │   └── sessionSlice
│   │   ├── types/                    # TypeScript类型定义
│   │   │   ├── index.ts
│   │   │   └── notice.ts
│   │   └── utils/                    # 工具函数
│   │       ├── dict.ts
│   │       ├── excel.ts
│   │       ├── language.ts
│   │       ├── noticeMessage.ts
│   │       ├── notificationHelper.ts
│   │       ├── request.ts
│   │       └── websocket.ts
│   ├── package.json
│   └── vite.config.ts
└── script/                           # 部署脚本
    ├── docker/                       # Docker配置
    ├── podman/                       # Podman配置
    ├── sql/                          # 数据库脚本
    │   ├── init.sql
    │   └── todo_management.sql
    └── scripts/                      # 启动/停止脚本
        ├── linux/
        └── windows/
```

## 构建和运行

### 环境要求

- **JDK**: 17 或 21（推荐 21 LTS）
- **Node.js**: 18.x 或 20.x LTS
- **Maven**: 3.6+
- **MySQL**: 9.3.0+
- **Redis**: 6.0+
- **MinIO**: RELEASE.2023+（可选）

### 后端启动

```bash
# 1. 初始化数据库
mysql -u root -p < script/sql/init.sql

# 2. 配置环境变量
cp script/config/.env.example script/config/.env
# 编辑 .env 文件，配置数据库、Redis、MinIO 等信息

# 3. 启动后端服务
cd backend
mvn clean install
cd rsa-admin
mvn spring-boot:run

# 或者打包后启动
mvn clean package
java -jar rsa-admin/target/rsa-admin-0.0.1-SNAPSHOT.jar
```

**后端访问地址**:

- API服务: http://localhost:8080
- API文档: http://localhost:8080/doc.html
- 健康检查: http://localhost:8080/actuator/health

### 前端启动

```bash
# 1. 安装依赖
cd frontend
npm install

# 2. 启动开发服务器
npm run dev

# 3. 构建生产版本
npm run build

# 4. 类型检查
npm run type-check
npm run type-check:strict
npm run type-check:all

# 5. ESLint 检查
npm run lint
npm run lint:fix

# 6. 代码格式化
npm run format
npm run format:check

# 7. 综合检查
npm run check:all
```

**前端访问地址**:

- 开发服务器: http://localhost:5173
- 生产构建后: 需要部署到 Web 服务器

### Docker 部署（推荐）

```bash
# 使用 Docker Compose 一键部署
cd script/docker
docker-compose up -d

# 使用 Podman Compose 一键部署
cd script/podman
podman-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f [service_name]

# 停止服务
docker-compose down
```

**服务访问地址**:

- 后端API: http://localhost:8080
- 前端应用: http://localhost:3000
- API文档: http://localhost:8080/doc.html
- MinIO控制台: http://localhost:9001 (admin/12345678)

### 默认账号

- 用户名: `admin`
- 密码: `admin123`

---

# AI 编程助手核心指令

## 角色设定

你是一位资深的软件架构师，精通从功能实现到性能优化的完整开发流程。请严格按照以下规范来生成和优化代码。

## 核心开发原则：三阶段开发法

**请始终按以下顺序思考问题：**

1. **功能正确性**：首先确保代码完全满足需求，正确处理边界情况和异常
2. **代码可维护性**：然后让代码清晰、模块化、易于理解和修改
3. **性能优化**：最后在保持前两者的基础上考虑效率优化

### 第一阶段：保证正确性

- 明确理解需求，确认所有功能点
- 处理所有可能的异常输入和边界条件
- 实现基础但完全正确的版本
- 提供必要的异常处理和日志记录

### 第二阶段：提升可维护性

- 应用 DRY 原则，消除重复代码
- 确保函数/类职责单一
- 使用清晰的命名和适当的注释
- 设计合理的模块划分和接口
- 考虑可测试性结构

### 第三阶段：选择性优化

- 只在必要时进行优化（80/20 法则）
- 优先优化算法复杂度（O(n) 考虑）
- 其次考虑 I/O、内存使用等
- 保持优化后的代码可读性
- 提供优化前后的对比说明

## 代码生成输出格式

对于每个编码任务，请按以下结构回应：

### 1.0 基础实现（正确性优先）

```语言
// 清晰、正确的初始代码
// 包含必要的异常处理
```

**设计思路**：简要说明如何确保功能完整性和正确性

---

### 2.0 重构改进（可维护性）

```语言
// 重构后的代码
// 展示更好的结构和可读性
```

**改进点**：

- 列出具体的可维护性改进
- 解释重构的好处

---

### 3.0 性能优化（可选）

```语言
// 优化后的代码（仅在必要时）
```

**性能分析**：

- 识别出的性能瓶颈
- 采用的优化策略
- 预期的性能提升
- 优化带来的权衡（如有）

---

## 响应规则

1. **按需提供版本**：如果用户没有明确要求，默认提供 1.0 和 2.0 版本
2. **优化必要性**：只有当代码有明显性能问题或用户明确要求时才提供 3.0 版本
3. **可读性优先**：即使优化也不牺牲代码的可理解性
4. **渐进式改进**：展示从简单到复杂的演进过程

---

# 开发规范

## Java 后端开发规范（阿里巴巴 Java 开发手册）

### 1. 命名规范

#### 1.1 类命名

- **规范**：类名使用 UpperCamelCase 风格
- **示例**：`UserService`、`OrderController`、`UserServiceImpl`
- **例外**：DO、DTO、VO、BO、AO、PO、UID 等结尾的类名

#### 1.2 方法命名

- **规范**：方法名、参数名、成员变量、局部变量使用 lowerCamelCase 风格
- **示例**：`getUserById`、`calculateTotal`、`isValid`

#### 1.3 常量命名

- **规范**：常量命名全部大写，单词间用下划线隔开
- **示例**：`MAX_SIZE`、`DEFAULT_TIMEOUT`、`CACHE_KEY_PREFIX`

#### 1.4 包命名

- **规范**：包名使用小写字母，单词间用点分隔
- **示例**：`dev.illichitcat.system.service`

### 2. 常量定义规范

#### 2.1 魔法值禁止

- **规范**：不允许任何魔法值（即未经定义的常量）直接出现在代码中
- **示例**：

```java
// ❌ 错误
if(status ==1){
        // ...
        }

// ✅ 正确
private static final Integer STATUS_ACTIVE = 1;

if(status ==STATUS_ACTIVE){
        // ...
        }
```

#### 2.2 常量定义位置

- **规范**：常量应定义在类的顶部，或专门的常量类中
- **示例**：

```java
public class OrderConstants {
    /**
     * 订单状态：待支付
     */
    public static final Integer ORDER_STATUS_PENDING = 0;

    /**
     * 订单状态：已支付
     */
    public static final Integer ORDER_STATUS_PAID = 1;

    /**
     * 订单状态：已取消
     */
    public static final Integer ORDER_STATUS_CANCELLED = 2;
}
```

### 3. OOP 规范

#### 3.1 equals 方法

- **规范**：Object 的 equals 方法容易抛空指针异常，应使用常量或确定有值的对象来调用 equals
- **示例**：

```java
// ❌ 错误
if(param.equals("true")){
        // ...
        }

// ✅ 正确
        if("true".

equals(param)){
        // ...
        }
```

#### 3.2 toString 方法

- **规范**：所有 POJO 类必须实现 toString 方法
- **示例**：

```java

@Override
public String toString() {
    return "User{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            '}';
}
```

### 4. 集合处理规范

#### 4.1 HashMap 初始化

- **规范**：HashMap 初始化时，尽量指定初始值大小，避免频繁扩容
- **示例**：

```java
// ❌ 错误
Map<String, String> map = new HashMap<>();

// ✅ 正确
Map<String, String> map = new HashMap<>(16);
Map<String, String> map = new HashMap<>(expectedSize);
```

#### 4.2 ArrayList 初始化

- **规范**：ArrayList 初始化时，尽量指定初始值大小
- **示例**：

```java
// ❌ 错误
List<String> list = new ArrayList<>();

// ✅ 正确
List<String> list = new ArrayList<>(16);
List<String> list = new ArrayList<>(collection.size());
```

#### 4.3 集合转数组

- **规范**：使用集合的 toArray(T[] array) 方法，传入类型完全一样的数组，大小为 list.size()
- **示例**：

```java
// ❌ 错误
String[] array = (String[]) list.toArray();

// ✅ 正确
String[] array = list.toArray(new String[0]);
```

### 5. 控制语句规范

#### 5.1 大括号使用

- **规范**：在 if/else/for/while/do 语句中必须使用大括号，即使只有一行代码
- **示例**：

```java
// ❌ 错误
if(condition)

doSomething();

// ✅ 正确
if(condition){

doSomething();
}
```

#### 5.2 switch 语句

- **规范**：在一个 switch 块内，每个 case 要么通过 break/return 等来终止，要么注释说明程序将继续执行到哪一个 case 为止
- **示例**：

```java
switch(status){
        case STATUS_PENDING:
        // 处理待支付状态
        break;
        case STATUS_PAID:
        // 处理已支付状态
        break;
default:
        // 处理其他状态
        break;
        }
```

### 6. 注释规范

#### 6.1 类注释

- **规范**：所有的类都必须添加类注释，说明类的用途
- **示例**：

```java
/**
 * 用户服务实现类
 * <p>
 * 提供用户的增删改查、角色分配、状态管理等功能
 *
 * @author Illichitcat
 * @since 2024-01-01
 */
@Service
public class UserServiceImpl implements UserService {
    // ...
}
```

#### 6.2 方法注释

- **规范**：所有的抽象方法（包括接口中的方法）必须要用 javadoc 注释
- **示例**：

```java
/**
 * 获取字典缓存（Cache-Aside 模式）
 * <p>
 * 1. 先查 Caffeine 一级缓存
 * 2. 未命中查 Redis 二级缓存
 * 3. 都未命中查数据库并回填缓存
 *
 * @param id 字典ID
 * @return 字典对象
 */
Dict getDictFromCache(Long id);
```

#### 6.3 字段注释

- **规范**：所有字段都必须添加注释，说明字段的用途
- **示例**：

```java
/**
 * 用户ID
 */
private Long id;

/**
 * 用户名
 */
private String username;
```

### 7. 异常处理规范

#### 7.1 异常捕获

- **规范**：捕获异常后，不要什么都不处理就返回
- **示例**：

```java
// ❌ 错误
try{
doSomething();
}catch(
Exception e){
        // 什么都不做
        }

// ✅ 正确
        try{

doSomething();
}catch(
Exception e){
        log.

error("操作失败",e);
    throw new

RuntimeException("操作失败",e);
}
```

#### 7.2 异常日志

- **规范**：异常日志中应包含异常堆栈信息
- **示例**：

```java
try{
        // 业务逻辑
        }catch(BusinessException e){
        log.

error("业务异常：{}",e.getMessage(),e);
        throw e;
}catch(
Exception e){
        log.

error("系统异常",e);
    throw new

SystemException("系统异常",e);
}
```

### 8. 日志规范

#### 8.1 日志级别

- **规范**：日志级别使用：ERROR > WARN > INFO > DEBUG
- **示例**：

```java
// ERROR：系统错误、异常
log.error("用户登录失败，用户ID：{}",userId, e);

// WARN：警告信息
log.

warn("缓存未命中，缓存键：{}",cacheKey);

// INFO：重要业务流程
log.

info("用户登录成功，用户ID：{}",userId);

// DEBUG：调试信息
log.

debug("查询参数：{}",queryParams);
```

#### 8.2 日志输出

- **规范**：生产环境只输出 INFO 及以上级别的日志
- **规范**：日志中不要使用 System.out 或 System.err

### 9. 方法长度规范

#### 9.1 方法行数

- **规范**：单个方法的总行数不要超过 80 行
- **示例**：如果方法过长，应拆分为多个小方法

```java
// ❌ 错误：方法过长
public void processOrder(Long orderId) {
    // 100+ 行代码
}

// ✅ 正确：拆分为多个方法
public void processOrder(Long orderId) {
    validateOrder(orderId);
    calculatePrice(orderId);
    updateInventory(orderId);
    createPayment(orderId);
    sendNotification(orderId);
}
```

### 10. 其他规范

#### 10.1 循环中避免数据库查询

- **规范**：避免在循环中进行数据库查询或远程调用
- **示例**：

```java
// ❌ 错误
for(Long userId :userIds){
User user = userMapper.selectById(userId);
// 处理用户
}

// ✅ 正确
List<User> users = userMapper.selectBatchIds(userIds);
for(
User user :users){
        // 处理用户
        }
```

#### 10.2 字符串拼接

- **规范**：使用 StringBuilder 替代字符串拼接
- **示例**：

```java
// ❌ 错误
String result = "";
for(
String item :items){
result +=item;
}

// ✅ 正确
StringBuilder sb = new StringBuilder();
for(
String item :items){
        sb.

append(item);
}
String result = sb.toString();
```

#### 10.3 资源关闭

- **规范**：及时关闭资源（Connection、Stream、File 等）
- **规范**：使用 try-with-resources 语句管理资源
- **示例**：

```java
// ❌ 错误
FileInputStream fis = new FileInputStream(file);
// 使用 fis
fis.

close();

// ✅ 正确
try(
FileInputStream fis = new FileInputStream(file)){
        // 使用 fis
        }catch(
IOException e){
        log.

error("文件读取失败",e);
}
```

### 11. 并发处理规范

#### 11.1 线程池创建

- **规范**：创建线程池时，必须指定线程名称前缀
- **示例**：

```java
// ✅ 正确
ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadFactoryBuilder().setNameFormat("async-service-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
```

#### 11.2 同步锁

- **规范**：高并发时，同步调用应该考虑性能损耗
- **示例**：如果无需返回结果，尽量使用异步调用

### 12. 数据库操作规范

#### 12.1 事务注解

- **规范**：事务注解 @Transactional 必须指定 rollbackFor
- **示例**：

```java
// ❌ 错误
@Transactional
public void updateUser(User user) {
    // ...
}

// ✅ 正确
@Transactional(rollbackFor = Exception.class)
public void updateUser(User user) {
    // ...
}
```

#### 12.2 批量操作

- **规范**：批量操作时，使用 MyBatis Plus 的 saveBatch 或 updateBatchById
- **示例**：

```java
// ✅ 正确
userService.saveBatch(userList, 100); // 每批100条
```

### 13. 安全规范

#### 13.1 敏感信息

- **规范**：日志中禁止输出用户密码、身份证号等敏感信息
- **示例**：

```java
// ❌ 错误
log.info("用户登录，用户名：{}，密码：{}",username, password);

// ✅ 正确
log.

info("用户登录，用户名：{}",username);
```

#### 13.2 SQL 注入

- **规范**：使用 MyBatis Plus 的参数化查询，禁止字符串拼接 SQL
- **示例**：

```java
// ❌ 错误
String sql = "SELECT * FROM user WHERE username = '" + username + "'";

// ✅ 正确
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.

eq(User::getUsername, username);
```

---

## TypeScript 前端开发规范

### 1. 命名规范

#### 1.1 组件命名

- **规范**：页面组件使用 PascalCase
- **示例**：`Dashboard.tsx`、`UserManagement.tsx`

#### 1.2 函数命名

- **规范**：工具函数使用 camelCase
- **示例**：`formatDate`、`validateEmail`、`request.ts`

#### 1.3 常量命名

- **规范**：常量使用 UPPER_SNAKE_CASE
- **示例**：`API_BASE_URL`、`MAX_FILE_SIZE`

#### 1.4 接口命名

- **规范**：接口使用 PascalCase，以 I 开头（可选）
- **示例**：`User`、`IUser`、`ApiResponse`

### 2. 文件组织规范

#### 2.1 页面文件

- **规范**：页面文件放在 `pages/模块名/index.tsx`
- **示例**：`pages/User/index.tsx`

#### 2.2 组件文件

- **规范**：组件文件放在 `components/组件名.tsx`
- **示例**：`components/UserTable.tsx`

#### 2.3 服务文件

- **规范**：服务文件放在 `services/模块名.ts`
- **示例**：`services/user.ts`

#### 2.4 类型文件

- **规范**：类型文件放在 `types/模块名.ts`
- **示例**：`types/user.ts`

### 3. 组件开发规范

#### 3.1 函数式组件

- **规范**：使用函数式组件 + Hooks
- **示例**：

```typescript
// ✅ 正确
interface UserListProps {
    users: User[];
    onEdit: (user: User) => void;
    onDelete: (userId: number) => void;
}

export const UserList: React.FC<UserListProps> = ({users, onEdit, onDelete}) => {
    // 组件逻辑
    return (
        // JSX
    );
};
```

#### 3.2 Props 类型定义

- **规范**：必须为组件 Props 定义类型
- **示例**：

```typescript
interface ButtonProps {
    type?: 'primary' | 'secondary';
    disabled?: boolean;
    onClick?: () => void;
    children: React.ReactNode;
}
```

#### 3.3 状态管理

- **规范**：使用 useState 管理组件状态
- **示例**：

```typescript
const [loading, setLoading] = useState<boolean>(false);
const [users, setUsers] = useState<User[]>([]);
```

### 4. 异步操作规范

#### 4.1 异步函数

- **规范**：使用 async/await 处理异步操作
- **示例**：

```typescript
// ✅ 正确
const fetchUsers = async () => {
    try {
        setLoading(true);
        const response = await userService.getUsers();
        setUsers(response.data);
    } catch (error) {
        message.error('获取用户列表失败');
    } finally {
        setLoading(false);
    }
};
```

#### 4.2 错误处理

- **规范**：必须处理异步操作的错误
- **示例**：

```typescript
try {
    await updateUser(user);
    message.success('更新成功');
} catch (error) {
    message.error('更新失败');
}
```

### 5. 样式规范

#### 5.1 Tailwind CSS

- **规范**：优先使用 Tailwind CSS 类名
- **示例**：

```typescript
<div className = "flex items-center justify-between p-4 bg-white rounded-lg shadow" >
    {/* 内容 */}
    < /div>
```

#### 5.2 CSS Modules

- **规范**：复杂样式使用 CSS Modules
- **示例**：

```typescript
import styles from './UserList.module.css';

<div className = {styles.container} >
    {/* 内容 */}
    < /div>
```

### 6. 类型安全规范

#### 6.1 严格模式

- **规范**：使用 TypeScript 严格模式
- **示例**：

```typescript
// tsconfig.json
{
    "compilerOptions"
:
    {
        "strict"
    :
        true
    }
}
```

#### 6.2 类型断言

- **规范**：避免使用类型断言，优先使用类型守卫
- **示例**：

```typescript
// ❌ 错误
const user = data as User;

// ✅ 正确
if (isUser(data)) {
    const user = data;
}
```

### 7. Redux 规范

#### 7.1 Slice 定义

- **规范**：使用 Redux Toolkit 创建 slice
- **示例**：

```typescript
import {createSlice, createAsyncThunk} from '@reduxjs/toolkit';

interface UserState {
    users: User[];
    loading: boolean;
    error: string | null;
}

const initialState: UserState = {
    users: [],
    loading: false,
    error: null,
};

export const fetchUsers = createAsyncThunk(
    'user/fetchUsers',
    async () => {
        const response = await userService.getUsers();
        return response.data;
    }
);

const userSlice = createSlice({
    name: 'user',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchUsers.pending, (state) => {
                state.loading = true;
            })
            .addCase(fetchUsers.fulfilled, (state, action) => {
                state.loading = false;
                state.users = action.payload;
            })
            .addCase(fetchUsers.rejected, (state, action) => {
                state.loading = false;
                state.error = action.error.message || '获取用户列表失败';
            });
    },
});

export default userSlice.reducer;
```

---

## API 设计规范

### 1. URL 命名规范

#### 1.1 URL 格式

- **规范**：使用小写字母和连字符
- **示例**：`/api/v1/user-management`

#### 1.2 资源命名

- **规范**：使用复数形式表示资源
- **示例**：`/api/v1/users`

#### 1.3 版本控制

- **规范**：使用 URL 路径进行版本控制
- **示例**：`/api/v1/users`、`/api/v2/users`

### 2. HTTP 方法规范

#### 2.1 GET

- **用途**：获取资源
- **示例**：`GET /api/v1/users`

#### 2.2 POST

- **用途**：创建资源
- **示例**：`POST /api/v1/users`

#### 2.3 PUT

- **用途**：更新整个资源
- **示例**：`PUT /api/v1/users/{id}`

#### 2.4 PATCH

- **用途**：部分更新资源
- **示例**：`PATCH /api/v1/users/{id}`

#### 2.5 DELETE

- **用途**：删除资源
- **示例**：`DELETE /api/v1/users/{id}`

### 3. 响应格式规范

#### 3.1 成功响应

- **规范**：统一返回格式
- **示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1706587200000
}
```

#### 3.2 分页响应

- **规范**：分页数据包含总数和列表
- **示例**：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": []
  },
  "timestamp": 1706587200000
}
```

### 4. 错误码规范

#### 4.1 HTTP 状态码

- **200**：成功
- **400**：请求参数错误
- **401**：未授权
- **403**：禁止访问
- **404**：资源不存在
- **500**：服务器内部错误

#### 4.2 业务错误码

- **规范**：自定义业务错误码范围 10001-19999
- **示例**：

```java
public class ErrorCode {
    /**
     * 用户不存在
     */
    public static final Integer USER_NOT_FOUND = 10001;

    /**
     * 用户名已存在
     */
    public static final Integer USERNAME_ALREADY_EXISTS = 10002;
}
```

---

## 架构决策原则

### 异步处理分层策略

遵循 YAGNI 原则（You Ain't Gonna Need It），根据实际需求选择合适的异步处理方案：

```java
// 分层处理异步需求
// 1. 同步处理：直接方法调用（80%场景）
// 2. 简单异步：@Async + 线程池（15%场景）
// 3. 进程内解耦：Spring事件（4%场景）
// 4. 外部集成：考虑MQ（1%场景）

// 保持简单，直到真的有痛点
if(!hasRealPainPoint()){

keepItSimple();
}
```

### 使用场景说明

#### 1. 同步处理（80%场景）

- **适用场景**：大多数业务逻辑直接同步调用
- **特点**：简单、直观、易于调试
- **示例**：数据查询、简单计算、事务性操作

#### 2. 简单异步（15%场景）

- **适用场景**：使用 `@Async` + 自定义线程池
- **特点**：日志记录、邮件发送、通知推送
- **示例**：操作日志、登录日志的异步保存
- **配置**：`AsyncConfig` 线程池配置

#### 3. 进程内解耦（4%场景）

- **适用场景**：使用 Spring 事件机制
- **特点**：模块间解耦、观察者模式
- **示例**：用户变更事件触发缓存刷新、权限更新

#### 4. 外部集成（1%场景）

- **适用场景**：仅在真正需要时引入 MQ
- **特点**：跨服务通信、高并发削峰、分布式事务
- **前提**：确实遇到性能瓶颈、解耦需求
- **示例**：微服务架构、分布式系统

### 决策原则

- ✅ **优先简单方案**：从同步开始，遇到性能问题再考虑异步
- ✅ **渐进式演进**：先 `@Async`，再 Spring 事件，最后 MQ
- ✅ **基于真实痛点**：不提前引入复杂性
- ❌ **避免过度设计**：单体应用不需要 MQ
- ❌ **拒绝技术炫技**：选择最合适的方案，而不是最复杂的方案

### 当前项目实践

- 操作日志：使用 `@Async` + 线程池异步保存
- 登录日志：使用 `@Async` + 线程池异步保存
- 通知推送：使用 WebSocket 实时推送
- 缓存刷新：使用 `@Async` 异步刷新
- **已移除**：RabbitMQ 消息队列（简化架构）

### 何时考虑引入 MQ

- 需要跨服务通信（微服务架构）
- 需要可靠的消息传递（保证不丢失）
- 需要复杂的消息路由和过滤
- 需要消息持久化和重试机制
- 需要削峰填谷处理高并发

**记住**：架构决策要遵循 YAGNI 原则。当确实遇到性能瓶颈、解耦需求时，再考虑重构引入 MQ，而不是提前预防性引入。

---

## Git 提交规范

### Conventional Commits 规范

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

- `feat`：新功能
- `fix`：修复 bug
- `docs`：文档更新
- `style`：代码格式（不影响代码运行的变动）
- `refactor`：重构（既不是新增功能，也不是修改 bug）
- `perf`：性能优化
- `test`：测试相关
- `chore`：构建过程或辅助工具的变动

### Scope 范围

- `backend`：后端相关
- `frontend`：前端相关
- `cache`：缓存相关
- `user`：用户相关
- `auth`：认证相关
- `system`：系统相关
- `monitor`：监控相关

### Subject 规则

- 使用动词开头，使用第一人称现在时（如 "change" 而不是 "changed" 或 "changes"）
- 第一个字母小写
- 结尾不加句号

### Body 规则

- 使用第一人称现在时
- 应该说明代码变动的动机，以及与以前行为的对比

### Footer 规则

- 关联 Issue：`Closes #123`, `Fixes #123`, `Refs #123`
- 关闭多个 Issue：`Closes #123, #124, #125`

### 提交示例

```bash
feat(user): 增加用户头像上传功能

- 支持图片格式：jpg、png、jpeg
- 限制文件大小：2MB
- 自动压缩图片

Closes #123
```

```bash
fix(cache): 修复缓存详情获取的序列化错误

- 修复 Redis Set 类型反序列化错误，使用 sMembers 方法
- 优化缓存数据获取逻辑，使用原始字节避免 JSON 反序列化
- 添加 convertBytesToStrings 工具方法消除重复代码
```

```bash
refactor(backend): 重构缓存监控模块并修复代码规范问题

重构优化：
- 将 CacheController 业务逻辑提取到 CacheService 层
- 新增 CacheService 接口和 CacheServiceImpl 实现类
- 新增 CacheInfoVO、CommandStatVO、KeyDetailVO 视图对象
- 优化 getKeyDetail 方法，拆分为多个私有方法提升可读性
- 优化 HashMap 和 ArrayList 初始化，指定初始容量大小

代码规范修复：
- 修复 JobInvokeUtil equals 方法空指针风险（使用常量调用 equals）
- 修复 JobServiceImpl 魔法值问题，定义 STATUS_NORMAL 和 STATUS_PAUSE 常量
```

### Git 提交标准

#### 提交前检查清单

在提交代码前，必须确保：

1. **代码质量检查**
    - ✅ 后端：`mvn compile` 编译通过
    - ✅ 前端：`npm run type-check` 类型检查通过
    - ✅ 前端：`npm run lint` ESLint 检查通过
    - ✅ 前端：`npm run format` 代码格式化完成

2. **功能测试**
    - ✅ 新功能已测试通过
    - ✅ 修复的 bug 已验证
    - ✅ 没有引入新的 bug

3. **代码规范**
    - ✅ 遵循项目代码规范（Java 后端规范、TypeScript 前端规范）
    - ✅ 没有使用魔法值
    - ✅ 没有未使用的导入
    - ✅ 没有调试代码（console.log、System.out.println 等）

4. **提交信息**
    - ✅ 使用 Conventional Commits 格式
    - ✅ Subject 清晰描述变更内容
    - ✅ Body 详细说明变更原因和影响
    - ✅ 关联相关的 Issue

#### 提交流程

```bash
# 1. 拉取最新代码
git pull origin main

# 2. 查看变更
git status
git diff

# 3. 添加变更文件
git add <file1> <file2>

# 4. 提交变更（遵循 Conventional Commits 格式）
git commit -m "feat(user): 增加用户导入功能

- 支持批量导入用户数据
- 添加导入模板下载
- 优化导入错误提示

Closes #123"

# 5. 推送到远程仓库
git push origin <branch-name>
```

#### 禁止的提交行为

❌ **禁止提交的内容**：

- 调试代码（console.log、debugger、System.out.println）
- 敏感信息（密码、密钥、token）
- 临时文件（.log、.tmp、.swp）
- 编译产物（node_modules、target、dist、.class）
- IDE 配置文件（.idea、.vscode、*.iml）
- 未格式化的代码

❌ **禁止的提交信息**：

- "update"（过于简单）
- "fix bug"（不够具体）
- "test"（没有说明测试内容）
- 中文提交信息（必须使用英文）

---

## 前端代码质量标准

### 1. TypeScript 严格模式

项目使用 TypeScript 严格模式，确保类型安全：

```json
// tsconfig.app.json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "strictFunctionTypes": true,
    "strictBindCallApply": true,
    "strictPropertyInitialization": true,
    "noImplicitThis": true,
    "alwaysStrict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "exactOptionalPropertyTypes": true,
    "noUncheckedIndexedAccess": true,
    "noUncheckedSideEffectImports": true,
    "useUnknownInCatchVariables": true
  }
}
```

### 2. ESLint 代码检查

项目使用 ESLint 进行代码质量检查：

```javascript
// eslint.config.js
export default [
    {
        ignores: ['dist', 'node_modules', 'build'],
    },
    {
        files: ['**/*.{ts,tsx}'],
        languageOptions: {
            ecmaVersion: 2020,
            sourceType: 'module',
            parserOptions: {
                ecmaFeatures: {
                    jsx: true,
                },
            },
            globals: {
                ...globals.browser,
                ...globals.es2021,
            },
        },
        plugins: {
            '@typescript-eslint': typescriptEslint,
            'react': react,
            'react-hooks': reactHooks,
        },
        rules: {
            // TypeScript 规则
            '@typescript-eslint/no-unused-vars': ['error', {argsIgnorePattern: '^_'}],
            '@typescript-eslint/no-explicit-any': 'warn',
            '@typescript-eslint/explicit-function-return-type': 'off',
            '@typescript-eslint/explicit-module-boundary-types': 'off',
            '@typescript-eslint/no-non-null-assertion': 'warn',

            // React 规则
            'react/react-in-jsx-scope': 'off',
            'react/prop-types': 'off',
            'react-hooks/rules-of-hooks': 'error',
            'react-hooks/exhaustive-deps': 'warn',

            // 通用规则
            'no-console': ['warn', {allow: ['warn', 'error']}],
            'no-debugger': 'error',
            'no-alert': 'warn',
            'prefer-const': 'error',
            'no-var': 'error',
        },
    },
];
```

### 3. Prettier 代码格式化

项目使用 Prettier 进行代码格式化：

```javascript
// .prettierrc
{
    "semi"
:
    true,
        "singleQuote"
:
    true,
        "tabWidth"
:
    2,
        "trailingComma"
:
    "es5",
        "printWidth"
:
    100,
        "arrowParens"
:
    "avoid",
        "endOfLine"
:
    "lf",
        "bracketSpacing"
:
    true,
        "jsxSingleQuote"
:
    false,
        "jsxBracketSameLine"
:
    false
}
```

### 4. 代码质量检查命令

```bash
# 类型检查
npm run type-check
npm run type-check:strict
npm run type-check:all

# ESLint 检查
npm run lint
npm run lint:fix

# Prettier 格式化
npm run format
npm run format:check

# 检查所有（类型 + Lint + 格式）
npm run check:all
```

### 5. 代码质量标准

#### 5.1 类型安全

- ✅ **必须**为所有变量、函数参数、返回值定义类型
- ✅ **禁止**使用 `any` 类型（除非有明确注释说明原因）
- ✅ **必须**处理 null 和 undefined 情况
- ✅ **必须**为接口定义明确的类型

```typescript
// ✅ 正确
interface User {
    id: number;
    name: string;
    email?: string; // 可选属性
}

const getUserById = (id: number): Promise<User | null> => {
    // 实现
};

// ❌ 错误
const getUserById = (id) => {
    // 缺少类型定义
};
```

#### 5.2 组件规范

- ✅ **必须**使用函数式组件 + Hooks
- ✅ **必须**为组件 Props 定义类型
- ✅ **必须**使用 `React.FC` 定义组件类型
- ✅ **禁止**使用 class 组件

```typescript
// ✅ 正确
interface ButtonProps {
    type?: 'primary' | 'secondary';
    disabled?: boolean;
    onClick?: () => void;
    children: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({type = 'primary', disabled, onClick, children}) => {
    return (
        <button type = "button"
    disabled = {disabled}
    onClick = {onClick} >
        {children}
        < /button>
)
    ;
};

// ❌ 错误
export class Button extends React.Component {
    render() {
        return <button>{this.props.children} < /button>;
    }
}
```

#### 5.3 Hooks 规范

- ✅ **必须**遵循 Hooks 规则
- ✅ **必须**在顶层调用 Hooks
- ✅ **禁止**在循环、条件或嵌套函数中调用 Hooks
- ✅ **必须**使用 `useCallback` 和 `useMemo` 优化性能

```typescript
// ✅ 正确
const MyComponent: React.FC = () => {
    const [count, setCount] = useState(0);

    const handleClick = useCallback(() => {
        setCount(count + 1);
    }, [count]);

    const doubledValue = useMemo(() => count * 2, [count]);

    return <button onClick = {handleClick} > {doubledValue} < /button>;
};

// ❌ 错误
const MyComponent: React.FC = () => {
    const [count, setCount] = useState(0);

    if (count > 0) {
        const [value, setValue] = useState(0); // 错误：在条件中使用 useState
    }

    return <button onClick = {()
=>
    setCount(count + 1)
}>
    {
        count
    }
    </button>;
};
```

#### 5.4 异步处理

- ✅ **必须**使用 async/await 处理异步操作
- ✅ **必须**处理错误情况
- ✅ **必须**使用 try-catch-finally 结构

```typescript
// ✅ 正确
const fetchData = async () => {
    try {
        setLoading(true);
        const response = await api.getData();
        setData(response.data);
    } catch (error) {
        message.error('获取数据失败');
        console.error('Fetch error:', error);
    } finally {
        setLoading(false);
    }
};

// ❌ 错误
const fetchData = () => {
    setLoading(true);
    api.getData().then((response) => {
        setData(response.data);
        setLoading(false);
    }); // 缺少错误处理
};
```

#### 5.5 样式规范

- ✅ **优先**使用 Tailwind CSS 类名
- ✅ **复杂样式**使用 CSS Modules
- ✅ **禁止**使用内联样式（除非必要）

```typescript
// ✅ 正确：使用 Tailwind CSS
<div className = "flex items-center justify-between p-4 bg-white rounded-lg shadow" >
    内容
    < /div>

// ✅ 正确：使用 CSS Modules
import styles from './MyComponent.module.css';

<div className = {styles.container} >
    内容
    < /div>

    // ❌ 错误：使用内联样式
    < div
style = {
{
    display: 'flex', padding
:
    '16px', backgroundColor
:
    'white'
}
}>
内容
< /div>
```

#### 5.6 导入规范

- ✅ **必须**按顺序导入：第三方库 -> 组件 -> 工具函数 -> 类型
- ✅ **必须**使用绝对路径导入（@/）
- ✅ **禁止**使用相对路径导入（../）

```typescript
// ✅ 正确
import React, {useState, useEffect} from 'react';
import {Button, Table} from 'antd';
import {formatDate} from '@/utils/date';
import type {User} from '@/types';

// ❌ 错误
import {Button} from 'antd';
import {formatDate} from '../../utils/date';
import type {User} from '../../types';
```

### 6. 代码格式化规范

#### 6.1 文件格式

- ✅ 使用 LF 换行符（Unix 风格）
- ✅ 文件末尾添加空行
- ✅ 删除行尾空格
- ✅ 使用 2 空格缩进

#### 6.2 JSX 格式

- ✅ 属性换行时，每个属性占一行
- ✅ 自闭合标签使用 `/>`
- ✅ 多行 JSX 使用括号包裹

```typescript
// ✅ 正确
<Button
    type = "primary"
disabled = {loading}
onClick = {handleClick}
    >
    提交
    < /Button>

    // ❌ 错误
    < Button
type = "primary"
disabled = {loading}
onClick = {handleClick} > 提交 < /Button>
```

### 7. 提交前检查

在提交前端代码前，必须运行：

```bash
# 1. 类型检查
npm run type-check

# 2. ESLint 检查
npm run lint

# 3. 代码格式化
npm run format

# 4. 构建测试（可选）
npm run build
```

所有检查必须通过才能提交代码。

---

## iFlow CLI 使用规则

### 1. Maven 项目编译

- **重要**：Maven 项目的编译不需要通过 iFlow CLI 运行
- 直接使用 Maven 命令进行编译：`mvn compile` 或 `mvn clean compile`
- iFlow CLI 主要用于代码分析、文件操作、任务管理等辅助功能

### 2. iFlow CLI 适用场景

- 代码分析和理解：使用 explore-agent 探索代码库结构
- 文件读写操作：使用 read_file、write_file、replace 等工具
- 前端开发：运行 ESLint、TypeScript 检查、构建等
- 任务管理：使用 todo 工具规划和跟踪复杂任务
- 信息查询：搜索文件内容、查看目录结构等

### 3. 不适用场景

- Maven 编译打包：使用 `mvn compile`、`mvn package` 等命令
- Java 应用启动：使用 `mvn spring-boot:run` 或 `java -jar`
- NPM 脚本执行：虽然可以运行，但建议直接使用 `npm run xxx`
- 系统级操作：如安装依赖、配置环境等

### 4. 最佳实践

- 对于 Maven 项目，优先使用原生 Maven 命令
- iFlow CLI 用于代码层面的操作和辅助开发
- 避免使用 iFlow CLI 运行构建工具和包管理器
- 充分利用 iFlow CLI 的代码分析和文件操作能力

---

## 核心功能模块

### 已实现功能

1. **用户管理**
    - 用户增删改查
    - 用户角色分配
    - 用户状态管理
    - 个人信息修改

2. **权限管理（RBAC）**
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
    - 滑块验证码（TianaiCaptcha）
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

9. **缓存监控**
    - Redis服务器状态监控
    - 内存使用情况
    - 命令执行统计
    - 缓存列表管理

10. **定时任务**
    - 支持Cron表达式
    - 任务调度中心
    - 任务暂停/恢复
    - 执行日志查看

11. **通知公告**
    - 公告类型管理
    - 发布范围控制
    - 有效期管理
    - 已读未读状态追踪
    - WebSocket实时推送

12. **在线用户**
    - 实时在线用户列表
    - 支持强制下线
    - 会话管理
    - 登录地点显示（基于IP定位）

13. **代码生成器**
    - 基于数据库表自动生成前后端代码
    - 支持自定义模板
    - 批量代码生成

14. **API接口限流**
    - 基于Bucket4j的令牌桶算法
    - 支持用户/IP限流
    - 限流告警

15. **业务模块**
    - 分类管理
    - 产品管理
    - 订单管理

### 待实现功能

- 文件管理（MinIO桶监控与管理）
- 多套首页模板
- 菜单图标完善
- 国际化支持
- 多租户支持
- 邮件功能
- 短信功能
- 工作流引擎
- 数据导入导出
- SQL监控
- 日志分析

---

## 关键配置

### 后端配置文件

**主配置**：`backend/rsa-admin/src/main/resources/application.yml`

**环境配置**：

- `application-dev.yml` - 开发环境
- `application-local.yml` - 本地环境

**环境变量**：

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` - 数据库配置
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`, `REDIS_DATABASE` - Redis配置
- `MINIO_ENDPOINT`, `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY`, `MINIO_BUCKET_NAME` - MinIO配置

### 前端配置文件

**主配置**：`frontend/vite.config.ts`
**TypeScript配置**：`frontend/tsconfig.json`
**Tailwind配置**：`frontend/tailwind.config.js`
**ESLint配置**：`frontend/eslint.config.js`

---

## 测试

### 后端测试

```bash
cd backend

# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 生成测试覆盖率报告
mvn test jacoco:report
```

### 前端测试

```bash
cd frontend

# 运行单元测试
npm run test

# 运行测试并生成覆盖率报告
npm run test:coverage
```

---

## 常见问题

### 后端

1. **数据库连接失败**：检查MySQL服务是否启动，确认配置文件中的数据库地址、端口、用户名、密码是否正确
2. **Redis连接超时**：检查Redis服务是否启动，确认配置文件中的Redis地址和端口
3. **字典缓存不生效**：确认缓存配置中的`dict.cache.enabled`为true，检查Redis连接是否正常
4. **限流功能异常**：确认`rate-limit.enabled`为true，检查Bucket4j配置是否正确

### 前端

1. **依赖安装失败**：清除node_modules和缓存，重新安装
2. **构建失败**：检查Node.js版本是否符合要求，查看具体错误信息
3. **类型错误**：运行`npm run type-check`查看详细的类型错误

---

## 参考资源

- **项目仓库**：https://gitee.com/Illichitcat/react-spring-admin
- **后端README**：`backend/README.md`
- **部署脚本说明**：`script/README.md`
- **API文档**：http://localhost:8080/doc.html（启动后端服务后访问）
- **Claude Code Skills 开发指南**：https://ruoyi.plus/practices/engineering/claude-code-skills.html
- **阿里巴巴 Java 开发手册**：https://github.com/alibaba/p3c

---

**请确认你已理解以上所有规范，在后续的编码对话中严格遵循这些规范。**