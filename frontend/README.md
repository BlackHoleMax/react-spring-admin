# React Spring Admin - 前端

基于 React 19 + TypeScript + Vite 的现代化前端管理系统，采用 Ant Design UI 框架，提供完整的用户权限管理、系统监控等功能。

## 项目简介

这是一个功能完善的前端管理系统模板，提供了：

- ✅ 基于 React 19 + TypeScript 5.9
- ✅ Ant Design 6.1 UI 框架（按需引入）
- ✅ Redux Toolkit 状态管理
- ✅ React Router 7 路由管理
- ✅ Tailwind CSS 4.1 样式框架
- ✅ 明暗主题切换
- ✅ 响应式设计
- ✅ 代码分割和性能优化
- ✅ WebSocket 实时通信
- ✅ 验证码组件集成
- ✅ 按钮级权限控制
- ✅ 会话超时管理

## 技术栈

### 核心框架
- **React**: 19.2.0
- **TypeScript**: 5.9.3
- **Vite**: 7.3.0

### UI 框架
- **Ant Design**: 6.1.1
- **@ant-design/icons**: 6.1.0
- **Tailwind CSS**: 4.1.18

### 状态管理
- **Redux Toolkit**: 2.11.2
- **react-redux**: 9.2.0

### 路由管理
- **react-router-dom**: 7.10.1

### HTTP 客户端
- **axios**: 1.13.2

### 工具库
- **dayjs**: 1.11.19 (日期处理)

### 构建优化
- **vite-plugin-compression**: Gzip/Brotli 压缩
- **vite-plugin-imp**: Antd 按需引入
- **vite-plugin-svgr**: SVG 组件化

## 项目结构

```
frontend/
├── public/                        # 静态资源
│   ├── vite.svg
│   ├── js/
│   │   └── load.min.js
│   └── tac/                      # 验证码组件资源
│       ├── css/
│       ├── images/
│       └── js/
├── src/
│   ├── components/               # 通用组件
│   │   ├── DayNightToggle.tsx    # 主题切换组件
│   │   ├── TianaiCaptcha.tsx     # 验证码组件
│   │   ├── PrivateRoute.tsx      # 路由守卫
│   │   ├── Authorized.tsx        # 权限控制组件
│   │   └── CodePreview.tsx       # 代码预览组件
│   ├── hooks/                    # 自定义 Hooks
│   │   ├── usePermission.ts      # 权限检查 Hook
│   │   └── useSessionTimeout.ts  # 会话超时 Hook
│   ├── layouts/                   # 布局组件
│   │   ├── MainLayout.tsx        # 主布局
│   │   └── MainLayout.css
│   ├── pages/                     # 页面组件
│   │   ├── Login.tsx             # 登录页
│   │   ├── Dashboard/            # 仪表盘
│   │   ├── Settings/             # 设置页
│   │   ├── User/                 # 用户管理
│   │   ├── Role/                 # 角色管理
│   │   ├── Menu/                 # 菜单管理
│   │   ├── Permission/           # 权限管理
│   │   ├── Dict/                 # 字典管理
│   │   ├── Job/                  # 定时任务
│   │   ├── JobLog/               # 任务日志
│   │   ├── LoginLog/             # 登录日志
│   │   ├── OperLog/              # 操作日志
│   │   ├── Online/               # 在线用户
│   │   ├── Notice/               # 通知公告
│   │   ├── Profile/              # 个人中心
│   │   ├── Monitor/              # 系统监控
│   │   ├── ApiDoc/               # API 文档
│   │   ├── CacheMonitor/         # 缓存监控
│   │   ├── CacheList/            # 缓存列表
│   │   ├── File/                 # 文件管理
│   │   ├── CodeGen/              # 代码生成器
│   │   ├── Category/             # 分类管理
│   │   ├── Product/              # 产品管理
│   │   ├── Orders/               # 订单管理
│   │   └── NotFound/             # 404页面
│   ├── services/                  # API 服务层
│   │   ├── auth.ts               # 认证服务
│   │   ├── user.ts               # 用户服务
│   │   ├── role.ts               # 角色服务
│   │   ├── menu.ts               # 菜单服务
│   │   ├── dict.ts               # 字典服务
│   │   ├── job.ts                # 定时任务服务
│   │   ├── jobLog.ts             # 任务日志服务
│   │   ├── notice.ts             # 通知服务
│   │   ├── loginLog.ts           # 登录日志服务
│   │   ├── operLog.ts            # 操作日志服务
│   │   ├── online.ts             # 在线用户服务
│   │   ├── cache.ts              # 缓存服务
│   │   ├── file.ts               # 文件服务
│   │   ├── gen.ts                # 代码生成服务
│   │   ├── permission.ts         # 权限服务
│   │   ├── profile.ts            # 个人中心服务
│   │   ├── settings.ts           # 设置服务
│   │   ├── monitor.ts            # 监控服务
│   │   └── dashboard.ts          # 仪表盘服务
│   ├── store/                     # Redux 状态管理
│   │   ├── index.ts              # Store 配置
│   │   ├── hooks.ts              # Redux Hooks
│   │   └── slices/               # Redux Slices
│   │       ├── authSlice.ts      # 认证状态
│   │       ├── menuSlice.ts      # 菜单状态
│   │       ├── permissionSlice.ts # 权限状态
│   │       ├── themeSlice.ts     # 主题状态
│   │       └── sessionSlice.ts   # 会话状态
│   ├── types/                     # TypeScript 类型定义
│   │   ├── index.ts              # 通用类型
│   │   └── notice.ts             # 通知类型
│   ├── utils/                     # 工具函数
│   │   ├── request.ts            # Axios 封装
│   │   ├── websocket.ts          # WebSocket 封装
│   │   ├── noticeMessage.ts      # 消息提示
│   │   ├── dict.ts               # 字典工具
│   │   ├── excel.ts              # Excel 工具
│   │   ├── language.ts           # 语言工具
│   │   └── notificationHelper.ts # 通知助手
│   ├── App.tsx                    # 根组件
│   ├── main.tsx                   # 入口文件
│   └── index.css                  # 全局样式
├── index.html                     # HTML 模板
├── package.json                   # 依赖配置
├── vite.config.ts                 # Vite 配置
├── tsconfig.json                  # TypeScript 配置
├── tailwind.config.js             # Tailwind 配置
├── eslint.config.js               # ESLint 配置
└── README.md                      # 项目文档
```

## 环境要求

- **Node.js**: 18.x 或 20.x LTS
- **npm**: 9.x 或 10.x
- **浏览器**: Chrome 90+、Firefox 88+、Edge 90+、Safari 14+

## 快速开始

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:5173

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览构建结果

```bash
npm run preview
```

访问 http://localhost:4173

### 5. 默认账号

- 用户名: `admin`
- 密码: `admin123`

## 核心功能

### 1. 用户权限管理
- 用户管理：增删改查、角色分配
- 角色管理：角色权限分配
- 菜单管理：动态菜单配置
- 权限管理：接口权限控制、按钮级细粒度权限控制

### 2. 系统管理
- 字典管理：系统字典配置
- 定时任务：任务调度管理
- 任务日志：执行日志查看
- 系统设置：参数配置

### 3. 日志管理
- 登录日志：登录记录查询
- 操作日志：操作记录查询
- 在线用户：在线用户管理

### 4. 系统监控
- 系统监控：系统状态查看
- 健康检查：服务健康状态
- 缓存监控：Redis 缓存状态
- 缓存列表：Redis Key 管理
- API 文档：接口文档查看

### 5. 通知公告
- 公告管理：发布公告
- 我的公告：查看公告
- 实时通知：WebSocket 推送

### 6. 文件管理
- 文件上传：MinIO 对象存储
- 文件预览：在线预览文件
- 文件下载：批量下载文件

### 7. 代码生成器
- 表导入：导入数据库表
- 代码预览：预览生成代码
- 批量下载：下载生成代码

### 8. 业务模块
- 分类管理：产品分类管理
- 产品管理：产品信息管理
- 订单管理：订单信息管理

### 9. 个人中心
- 个人信息：修改个人资料
- 修改密码：更新登录密码
- 头像上传：上传用户头像

## 开发指南

### 代码规范

1. **组件命名**
   - 页面组件: PascalCase（如 `UserManagement.tsx`）
   - 工具函数: camelCase（如 `formatDate.ts`）
   - 常量: UPPER_SNAKE_CASE（如 `API_BASE_URL`）

2. **文件组织**
   - 页面: `pages/模块名/index.tsx`
   - 组件: `components/组件名.tsx`
   - 服务: `services/模块名.ts`
   - 类型: `types/index.ts`

3. **状态管理**
   - 使用 Redux Toolkit
   - 异步操作: `createAsyncThunk`
   - 模块化 slice

4. **性能优化**
   - 使用 `React.lazy` 进行代码分割
   - 使用 `useMemo` 和 `useCallback` 优化性能
   - 使用虚拟列表优化长列表渲染

5. **类型安全**
   - 使用 TypeScript 严格模式
   - 避免使用 `any` 类型
   - 为所有变量、函数参数、返回值定义类型

### 添加新页面

1. **创建页面组件**
   ```tsx
   // pages/Example/index.tsx
   import React from 'react';
   import { Card, Table } from 'antd';

   const Example: React.FC = () => {
     return (
       <Card title="示例页面">
         <Table />
       </Card>
     );
   };

   export default Example;
   ```

2. **创建 API 服务**
   ```typescript
   // services/example.ts
   import request from '@/utils/request';

   export const getExampleList = (params: any) => {
     return request.get('/api/system/example/list', { params });
   };
   ```

3. **添加路由**
   ```tsx
   // App.tsx
   const Example = lazy(() => import('./pages/Example'));

   <Route path="system/example" element={
     <Suspense fallback={<PageLoading/>}>
       <Example/>
     </Suspense>
   }/>
   ```

4. **添加菜单**
   - 在数据库 `sys_menu` 表中添加菜单记录
   - 或通过菜单管理页面添加

### 使用 Ant Design 组件

```tsx
import { Button, Table, Form, Input } from 'antd';

// 按需引入，无需手动导入样式
const Example: React.FC = () => {
  return (
    <div>
      <Button type="primary">确定</Button>
      <Table />
    </div>
  );
};
```

### 权限控制

```tsx
import { Authorized } from '@/components/Authorized';
import { usePermission } from '@/hooks/usePermission';

// 方式一：使用 Authorized 组件
<Authorized permission="system:user:add">
  <Button type="primary">添加用户</Button>
</Authorized>

// 方式二：使用 usePermission Hook
const { hasPermission } = usePermission();

{hasPermission('system:user:edit') && (
  <Button>编辑</Button>
)}
```

### 状态管理

```typescript
// store/slices/exampleSlice.ts
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { getExampleList } from '@/services/example';

export const fetchExamples = createAsyncThunk(
  'example/fetchList',
  async (params: any) => {
    const response = await getExampleList(params);
    return response.data;
  }
);

const exampleSlice = createSlice({
  name: 'example',
  initialState: {
    list: [],
    loading: false,
  },
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchExamples.pending, (state) => {
        state.loading = true;
      })
      .addCase(fetchExamples.fulfilled, (state, action) => {
        state.loading = false;
        state.list = action.payload;
      });
  },
});

export default exampleSlice.reducer;
```

## 配置说明

### Vite 配置

`vite.config.ts`

```typescript
export default defineConfig({
  // 开发服务器代理
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      }
    }
  },

  // 路径别名
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },

  // 构建优化
  build: {
    // 代码分割
    rollupOptions: {
      output: {
        manualChunks: (id) => {
          if (id.includes('antd')) return 'vendor-antd';
          if (id.includes('react')) return 'vendor-react';
          if (id.includes('redux')) return 'vendor-redux';
        },
      },
    },
  },
});
```

### TypeScript 配置

`tsconfig.app.json`

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "useDefineForClassFields": true,
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noImplicitReturns": true,
    "noFallthroughCasesInSwitch": true,
    "exactOptionalPropertyTypes": true,
    "noUncheckedIndexedAccess": true,
    "useUnknownInCatchVariables": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["./src/*"]
    }
  },
  "include": ["src"]
}
```

## 代码质量检查

### 类型检查

```bash
npm run type-check          # 基础类型检查
npm run type-check:strict   # 严格类型检查
npm run type-check:all      # 所有类型检查
```

### ESLint 检查

```bash
npm run lint        # ESLint 检查
npm run lint:fix    # ESLint 自动修复
```

### 代码格式化

```bash
npm run format          # Prettier 格式化
npm run format:check    # Prettier 检查
```

### 综合检查

```bash
npm run check:all    # 运行所有检查（类型 + Lint + 格式）
```

## 性能优化

### 1. 代码分割

- 使用 `React.lazy` 实现路由级代码分割
- 手动分包：react、antd、redux 等库分离
- 预加载策略：延迟加载常用页面

### 2. 按需引入

- Antd 组件按需引入
- 图标按需引入
- 使用 `vite-plugin-imp` 插件

### 3. 构建优化

- Gzip 和 Brotli 压缩
- 删除 console 和 debugger
- 启用 CSS 代码分割
- 文件名哈希缓存

### 4. 运行时优化

- 使用 `useMemo` 缓存计算结果
- 使用 `useCallback` 缓存函数
- 使用 `React.memo` 优化组件渲染
- 虚拟列表优化长列表

## 部署

### Docker 部署

```bash
# 使用项目提供的 Docker Compose
cd ../script/docker
docker-compose up -d
```

### Podman 部署

```bash
# 使用项目提供的 Podman Compose
cd ../script/podman
podman-compose up -d
```

### 手动部署

```bash
# 构建生产版本
npm run build

# 将 dist 目录部署到 Web 服务器
# 例如：Nginx、Apache 等
```

### Nginx 配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /path/to/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 启用 Gzip 压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript;
}
```

## 常见问题

### 1. 依赖安装失败

```bash
# 清除缓存重新安装
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
```

### 2. 启动失败

检查端口 5173 是否被占用：

```bash
# Windows
netstat -ano | findstr 5173

# Linux/Mac
lsof -i :5173
```

### 3. 构建失败

检查 Node.js 版本是否符合要求：

```bash
node -v  # 应该是 18.x 或 20.x
```

### 4. API 请求失败

检查后端服务是否启动，确认代理配置是否正确。

### 5. 类型错误

运行类型检查查看详细的类型错误：

```bash
npm run type-check
```

## 性能监控

### Chrome DevTools

1. 打开开发者工具（F12）
2. 切换到 Performance 面板
3. 录制页面性能
4. 分析性能瓶颈

### Lighthouse

1. 打开 Chrome DevTools
2. 切换到 Lighthouse 面板
3. 运行性能审计
4. 查看优化建议

### Web Vitals

```typescript
import { getCLS, getFID, getFCP, getLCP, getTTFB } from 'web-vitals';

getCLS(console.log);
getFID(console.log);
getFCP(console.log);
getLCP(console.log);
getTTFB(console.log);
```

## 主题定制

### 修改主题色

```tsx
// App.tsx
const primaryColor = useAppSelector((state) => state.theme.primaryColor);

<ConfigProvider
  theme={{
    token: {
      colorPrimary: primaryColor,
    },
  }}
>
  <App />
</ConfigProvider>
```

### 添加自定义样式

```css
/* index.css */
:root {
  --primary-color: #1890ff;
  --border-radius: 4px;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
```

## 许可证

MIT License