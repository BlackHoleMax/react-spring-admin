import React, { Suspense, lazy, useEffect } from 'react';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { App as AntdApp, ConfigProvider, Spin, theme } from 'antd';
import { useAppSelector } from './store/hooks';
import PrivateRoute from './components/PrivateRoute';

const Login = lazy(() => import(/* webpackChunkName: "login" */ './pages/Login'));
const Dashboard = lazy(() => import(/* webpackChunkName: "dashboard" */ './pages/Dashboard'));
const UserManagement = lazy(() => import(/* webpackChunkName: "system-user" */ './pages/User'));
const RoleManagement = lazy(() => import(/* webpackChunkName: "system-role" */ './pages/Role'));
const MenuManagement = lazy(() => import(/* webpackChunkName: "system-menu" */ './pages/Menu'));
const PermissionManagement = lazy(
  () => import(/* webpackChunkName: "system-permission" */ './pages/Permission')
);
const DictManagement = lazy(() => import(/* webpackChunkName: "system-dict" */ './pages/Dict'));
const LoginLogManagement = lazy(
  () => import(/* webpackChunkName: "log-login" */ './pages/LoginLog')
);
const OperLogManagement = lazy(() => import(/* webpackChunkName: "log-oper" */ './pages/OperLog'));
const OnlineUserManagement = lazy(
  () => import(/* webpackChunkName: "log-online" */ './pages/Online')
);
const ApiDoc = lazy(() => import(/* webpackChunkName: "api-doc" */ './pages/ApiDoc'));
const Monitor = lazy(() => import(/* webpackChunkName: "monitor" */ './pages/Monitor'));
const Profile = lazy(() => import(/* webpackChunkName: "user-profile" */ './pages/Profile'));
const NoticeManagement = lazy(() => import(/* webpackChunkName: "notice-list" */ './pages/Notice'));
const MyNotice = lazy(() => import(/* webpackChunkName: "notice-my" */ './pages/Notice/my'));
const JobManagement = lazy(() => import(/* webpackChunkName: "job-list" */ './pages/Job'));
const JobLogManagement = lazy(() => import(/* webpackChunkName: "job-log" */ './pages/JobLog'));
const CacheMonitor = lazy(
  () => import(/* webpackChunkName: "cache-monitor" */ './pages/CacheMonitor')
);
const CacheList = lazy(() => import(/* webpackChunkName: "cache-list" */ './pages/CacheList'));
const FileManagement = lazy(() => import(/* webpackChunkName: "file-management" */ './pages/File'));
const FileConfigManagement = lazy(
  () => import(/* webpackChunkName: "file-config" */ './pages/File/Config')
);
const CodeGen = lazy(() => import(/* webpackChunkName: "code-gen" */ './pages/CodeGen'));
const CodeGenEdit = lazy(
  () => import(/* webpackChunkName: "code-gen-edit" */ './pages/CodeGen/Edit')
);
const NotFound = lazy(() => import(/* webpackChunkName: "not-found" */ './pages/NotFound'));

const PageLoading: React.FC = () => (
  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '200px' }}>
    <Spin size="large" />
  </div>
);

const preloadComponent = (importFunc: () => Promise<any>) => {
  importFunc();
};

const App: React.FC = () => {
  const themeMode = useAppSelector((state) => state.theme.mode);
  const primaryColor = useAppSelector((state) => state.theme.primaryColor);
  const compactMode = useAppSelector((state) => state.theme.compactMode);

  useEffect(() => {
    const timer = setTimeout(() => {
      preloadComponent(() => import('./pages/Dashboard'));
    }, 1000);

    return () => clearTimeout(timer);
  }, []);

  return (
    <ConfigProvider
      theme={{
        algorithm: themeMode === 'dark' ? theme.darkAlgorithm : theme.defaultAlgorithm,
        token: {
          colorPrimary: primaryColor,
        },
        components: {
          Layout: compactMode
            ? {
                headerBg: themeMode === 'dark' ? '#141414' : '#fff',
              }
            : {},
          Menu: compactMode
            ? {
                itemHeight: 32,
                itemPaddingInline: 12,
              }
            : {},
          Switch: {
            colorPrimary: primaryColor,
            colorPrimaryHover: primaryColor,
          },
          Table: {
            rowHoverBg: themeMode === 'dark' ? '#262626' : '#f5f5f5',
            headerBg: themeMode === 'dark' ? '#1f1f1f' : '#fafafa',
          },
        },
      }}
    >
      <AntdApp>
        <BrowserRouter>
          <Routes>
            <Route
              path="/login"
              element={
                <Suspense fallback={<PageLoading />}>
                  <Login />
                </Suspense>
              }
            />
            <Route path="/" element={<PrivateRoute />}>
              <Route index element={<Navigate to="/dashboard" replace />} />
              <Route
                path="dashboard"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <Dashboard />
                  </Suspense>
                }
              />
              <Route
                path="profile"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <Profile />
                  </Suspense>
                }
              />
              <Route
                path="system/user"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <UserManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/role"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <RoleManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/menu"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <MenuManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/permission"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <PermissionManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/dict"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <DictManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/log/login"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <LoginLogManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/log/oper"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <OperLogManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/log/online"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <OnlineUserManagement />
                  </Suspense>
                }
              />
              <Route
                path="monitor/system"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <Monitor />
                  </Suspense>
                }
              />
              <Route
                path="system-tools/api-doc"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <ApiDoc />
                  </Suspense>
                }
              />
              <Route
                path="notice/list"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <NoticeManagement />
                  </Suspense>
                }
              />
              <Route
                path="notice/my"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <MyNotice />
                  </Suspense>
                }
              />
              <Route
                path="system/job/list"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <JobManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/job-log/list"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <JobLogManagement />
                  </Suspense>
                }
              />
              <Route
                path="monitor/cache"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <CacheMonitor />
                  </Suspense>
                }
              />
              <Route
                path="monitor/cache/list"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <CacheList />
                  </Suspense>
                }
              />
              <Route
                path="system/file"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <FileManagement />
                  </Suspense>
                }
              />
              <Route
                path="system/file/config"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <FileConfigManagement />
                  </Suspense>
                }
              />
              <Route
                path="system-tools/code-gen"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <CodeGen />
                  </Suspense>
                }
              />
              <Route
                path="system-tools/code-gen/edit/:tableId"
                element={
                  <Suspense fallback={<PageLoading />}>
                    <CodeGenEdit />
                  </Suspense>
                }
              />
            </Route>
            <Route
              path="*"
              element={
                <Suspense fallback={<PageLoading />}>
                  <NotFound />
                </Suspense>
              }
            />
          </Routes>
        </BrowserRouter>
      </AntdApp>
    </ConfigProvider>
  );
};

export default App;
