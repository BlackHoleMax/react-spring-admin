import React, { useCallback, useEffect, useMemo, useState } from 'react';
import type { MenuProps } from 'antd';
import { App, Avatar, Badge, Button, Dropdown, Layout, Menu, Skeleton, Space } from 'antd';
import './MainLayout.css';
import {
  ApiOutlined,
  AppstoreOutlined,
  AuditOutlined,
  BellOutlined,
  BookOutlined,
  ClockCircleOutlined,
  CrownOutlined,
  DashboardOutlined,
  DatabaseOutlined,
  FileOutlined,
  FileTextOutlined,
  FolderOutlined,
  HistoryOutlined,
  KeyOutlined,
  LoginOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  MonitorOutlined,
  NotificationOutlined,
  OrderedListOutlined,
  SettingOutlined,
  TeamOutlined,
  ToolOutlined,
  UserOutlined,
  CodeOutlined,
} from '@ant-design/icons';
import { Outlet, useLocation, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { fetchUserMenus, toggleCollapsed } from '../store/slices/menuSlice';
import { logout as logoutAction } from '../store/slices/authSlice';
import { fetchUserPermissions } from '../store/slices/permissionSlice';
import type { Menu as MenuType } from '../types';
import { getProfile } from '../services/profile';
import { useSessionTimeout } from '../hooks/useSessionTimeout';
import { websocketManager } from '../utils/websocket';
import { getUnreadCount } from '@/services/notice';
import { showNotification } from '../utils/notificationHelper';
import SettingsDrawer from '../pages/Settings';

const { Header, Sider, Content } = Layout;

type MenuItem = Required<MenuProps>['items'][number];

const ICON_MAP: Record<string, React.ReactNode> = {
  dashboard: <DashboardOutlined />,
  user: <UserOutlined />,
  role: <CrownOutlined />,
  menu: <FolderOutlined />,
  permission: <KeyOutlined />,
  dict: <BookOutlined />,
  'login-log': <LoginOutlined />,
  'oper-log': <HistoryOutlined />,
  log: <AuditOutlined />,
  monitoring: <MonitorOutlined />,
  monitor: <MonitorOutlined />,
  api: <ApiOutlined />,
  setting: <SettingOutlined />,
  system: <DatabaseOutlined />,
  online: <TeamOutlined />,
  notification: <NotificationOutlined />,
  'file-text': <FileTextOutlined />,
  bell: <BellOutlined />,
  'clock-circle': <ClockCircleOutlined />,
  database: <DatabaseOutlined />,
  'unordered-list': <OrderedListOutlined />,
  file: <FileOutlined />,
  tool: <ToolOutlined />,
  code: <CodeOutlined />,
};

const DEFAULT_ICON = <AppstoreOutlined />;

const formatMenus = (menus: MenuType[]): MenuItem[] => {
  return menus.map((menu) => {
    const baseItem: MenuItem & { children?: MenuItem[] } = {
      key: menu.path || `/${menu.id}`,
      label: menu.name,
    } as MenuItem & { children?: MenuItem[] };

    if (menu.icon) {
      Object.assign(baseItem, { icon: ICON_MAP[menu.icon] || DEFAULT_ICON });
    }

    if (menu.children && menu.children.length > 0) {
      Object.assign(baseItem, { children: formatMenus(menu.children) });
    }

    return baseItem;
  });
};

const MainLayout: React.FC = () => {
  const { message } = App.useApp();
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const wsConnectedRef = React.useRef(false);
  const isAuthenticatedRef = React.useRef(false);
  const [settingsDrawerOpen, setSettingsDrawerOpen] = useState(false);
  const { menus, loading, collapsed } = useAppSelector((state) => state.menu);
  const { isAuthenticated, user } = useAppSelector((state) => state.auth);
  const { unreadCount } = useAppSelector((state) => state.session);
  const themeMode = useAppSelector((state) => state.theme.mode);
  const isDark = themeMode === 'dark';

  // 同步 isAuthenticated 到 ref
  React.useEffect(() => {
    isAuthenticatedRef.current = isAuthenticated;
  }, [isAuthenticated]);

  useSessionTimeout();

  const handleOpenSettings = useCallback(() => {
    setSettingsDrawerOpen(true);
  }, []);

  const handleCloseSettings = useCallback(() => {
    setSettingsDrawerOpen(false);
  }, []);

  const handleNoticeMessage = useCallback(
    (notice: any) => {
      console.log('[WebSocket] 收到通知:', notice.id, notice.title, '已读状态:', notice.readStatus);

      // 检查通知是否已读，只有未读通知才需要提示
      if (notice.readStatus === 1) {
        console.log('[WebSocket] 通知已读，跳过提示:', notice.id);
        return;
      }

      // 检查是否已经显示过这个通知（使用 sessionStorage 防止重复提示）
      const notifiedKey = `notice_notified_${notice.id}`;
      const alreadyNotified = sessionStorage.getItem(notifiedKey);
      if (alreadyNotified) {
        console.log('[WebSocket] 通知已显示过，跳过重复提示:', notice.id);
        return;
      }

      console.log('[WebSocket] 显示通知提示:', notice.id);

      // 标记通知已显示
      sessionStorage.setItem(notifiedKey, 'true');

      // 显示桌面通知和声音提示
      showNotification(notice.typeName || '新通知', notice.title || '您有一条新消息', () =>
        navigate('/notice/my')
      );

      // 显示应用内通知
      message.open({
        type: 'info',
        content: (
          <div>
            <div style={{ fontWeight: 'bold', marginBottom: 4 }}>{notice.typeName}</div>
            <div style={{ marginBottom: 4 }}>{notice.title}</div>
            <Button
              type="link"
              size="small"
              onClick={() => navigate('/notice/my')}
              style={{ padding: 0 }}
            >
              查看详情
            </Button>
          </div>
        ),
        duration: 5,
      });

      // 增加未读数量
      dispatch({ type: 'session/incrementUnread' });
    },
    [message, navigate, dispatch]
  );

  const handleWebSocketStateChange = useCallback(
    (state: any) => {
      console.log('[WebSocket] 状态变化:', state);
      dispatch({ type: 'websocket/setConnectionState', payload: state });
      // 当连接断开时，重置连接标志，允许重新连接
      if (state === 'disconnected' || state === 'error') {
        wsConnectedRef.current = false;
      }
    },
    [dispatch]
  );

  const handleWebSocketError = useCallback((error: Error) => {
    console.error('WebSocket连接失败:', error);
  }, []);

  // 初始化 WebSocket 连接
  useEffect(() => {
    let connectTimer: NodeJS.Timeout | null = null;

    // 检查认证状态
    if (!isAuthenticatedRef.current) {
      console.log('[WebSocket] 用户未认证，跳过连接');
      return;
    }

    if (isAuthenticated && user?.id && !wsConnectedRef.current) {
      console.log('[WebSocket] 准备连接 WebSocket，用户ID:', user.id, '认证状态:', isAuthenticated);
      connectTimer = setTimeout(() => {
        try {
          // 再次检查认证状态，防止在定时器执行期间用户已退出登录
          if (!isAuthenticatedRef.current) {
            console.log('[WebSocket] 用户已退出登录，取消连接');
            return;
          }
          wsConnectedRef.current = true;
          console.log('[WebSocket] 开始连接 WebSocket，用户ID:', user.id);
          websocketManager.connect(user.id, {
            onMessage: handleNoticeMessage,
            onStateChange: handleWebSocketStateChange,
            onError: handleWebSocketError,
          });
          console.log('[WebSocket] WebSocket 连接请求已发送');
        } catch (error) {
          console.error('初始化WebSocket失败:', error);
          wsConnectedRef.current = false;
        }
      }, 500);
    }

    // 清理函数：当组件卸载或认证状态改变时断开连接
    return () => {
      if (connectTimer) {
        clearTimeout(connectTimer);
      }
      // 使用 ref 检查最新的认证状态
      if (!isAuthenticatedRef.current) {
        try {
          console.log('[WebSocket] 认证状态改变，断开 WebSocket 连接');
          websocketManager.disconnect();
          wsConnectedRef.current = false;
          // 重置 WebSocket 状态
          dispatch({ type: 'websocket/resetWebSocketState' });
        } catch {
          console.error('操作失败');
        }
      }
    };
  }, [
    isAuthenticated,
    user?.id,
    handleNoticeMessage,
    handleWebSocketStateChange,
    handleWebSocketError,
    dispatch,
  ]);

  // 加载用户权限
  useEffect(() => {
    if (isAuthenticated && user?.id) {
      console.log('[权限] 开始加载用户权限');
      dispatch(fetchUserPermissions())
        .unwrap()
        .then((permissions) => {
          console.log('[权限] 权限加载成功，数量:', permissions.length);
        })
        .catch((error) => {
          console.error('[权限] 权限加载失败:', error);
        });
    }
  }, [isAuthenticated, user?.id, dispatch]);

  useEffect(() => {
    if (isAuthenticated && user?.id) {
      const storageKey = `unreadNotified_${user.id}`;
      const hasNotified = sessionStorage.getItem(storageKey);

      if (!hasNotified) {
        // 立即设置 sessionStorage，防止 React Strict Mode 重复执行
        sessionStorage.setItem(storageKey, 'true');
        console.log('[未读通知] 开始获取未读通知数量');

        // 初始化音频上下文和请求通知权限
        const initNotification = async () => {
          try {
            // 初始化音频上下文
            const AudioContextClass = window.AudioContext || (window as any).webkitAudioContext;
            if (AudioContextClass) {
              const audioContext = new AudioContextClass();
              console.log('[通知] 音频上下文初始化成功，状态:', audioContext.state);
            }

            // 检查通知权限
            if ('Notification' in window) {
              console.log('[通知] 桌面通知权限状态:', Notification.permission);
              if (Notification.permission === 'default') {
                console.log('[通知] 桌面通知权限未授予，将在首次开启时请求');
              }
            }
          } catch {
            console.error('操作失败');
          }
        };

        initNotification();

        getUnreadCount()
          .then((count) => {
            dispatch({ type: 'session/setUnreadCount', payload: count });
            if (count > 0) {
              console.log('[未读通知] 显示未读通知提示，数量:', count);

              // 显示桌面通知和声音提示
              showNotification('未读通知', `您有 ${count} 条未读通知`, () =>
                navigate('/notice/my')
              );

              // 显示应用内通知
              message.info({
                content: `您有 ${count} 条未读通知，点击查看`,
                duration: 5,
                onClick: () => navigate('/notice/my'),
              });
            }
          })
          .catch((error) => {
            console.error('获取未读通知数量失败:', error);
          });
      }
    }
  }, [isAuthenticated, user?.id, dispatch, message, navigate]);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login', { replace: true });
      return;
    }

    if (menus.length === 0) {
      dispatch(fetchUserMenus())
        .unwrap()
        .catch((error) => {
          console.error('加载菜单失败:', error);
          if (
            error.includes('Token 无效') ||
            error.includes('认证已失效') ||
            error.includes('未授权')
          ) {
            navigate('/login', { replace: true });
          } else {
            message.error('加载菜单失败');
          }
        });
    }

    if (!user) {
      getProfile()
        .then((data) => {
          dispatch({
            type: 'auth/setUser',
            payload: {
              id: data.id,
              username: data.username,
              nickname: data.nickname,
              avatar: data.avatar,
              email: data.email,
              phone: data.phone,
              status: 1,
            },
          });
        })
        .catch((error) => {
          console.error('加载用户信息失败:', error);
        });
    }
  }, [dispatch, navigate, isAuthenticated, menus.length, user, message]);

  const handleLogout = useCallback(async () => {
    try {
      // 立即更新认证状态 ref，防止 WebSocket 重连
      isAuthenticatedRef.current = false;

      // 立即关闭 WebSocket 连接
      console.log('[WebSocket] 退出登录，关闭 WebSocket 连接');
      websocketManager.disconnect();
      wsConnectedRef.current = false;

      // 重置 WebSocket 状态
      dispatch({ type: 'websocket/resetWebSocketState' });

      // 清除 sessionStorage 中的未读通知标记
      if (user?.id) {
        sessionStorage.removeItem(`unreadNotified_${user.id}`);
      }

      // 执行退出登录
      await dispatch(logoutAction()).unwrap();
      message.success('退出成功');

      // 导航到登录页
      navigate('/login', { replace: true });
    } catch (error) {
      console.error('退出失败:', error);
      // 即使退出失败，也要确保 WebSocket 已关闭
      isAuthenticatedRef.current = false;
      websocketManager.disconnect();
      wsConnectedRef.current = false;
      dispatch({ type: 'websocket/resetWebSocketState' });
      navigate('/login', { replace: true });
    }
  }, [dispatch, message, navigate, user]);

  const userMenuItems: MenuProps['items'] = useMemo(
    () => [
      {
        key: 'profile',
        icon: <UserOutlined />,
        label: '个人中心',
        onClick: () => navigate('/profile'),
      },
      {
        key: 'my-notice',
        icon: <BellOutlined />,
        label: '我的通知',
        onClick: () => navigate('/notice/my'),
      },
      {
        key: 'settings',
        icon: <SettingOutlined />,
        label: '设置',
        onClick: handleOpenSettings,
      },
      {
        type: 'divider',
      },
      {
        key: 'logout',
        icon: <LogoutOutlined />,
        label: '退出登录',
        onClick: handleLogout,
      },
    ],
    [navigate, handleLogout, handleOpenSettings]
  );

  const renderSkeletonMenu = useCallback(() => {
    return Array(6)
      .fill(null)
      .map((_, index) => ({
        key: `skeleton-${index}`,
        label: (
          <div className="skeleton-menu-item" style={{ display: 'flex', alignItems: 'center' }}>
            <Skeleton.Avatar active size="small" style={{ marginRight: 8 }} />
            <Skeleton.Input active size="small" style={{ width: 100 }} />
          </div>
        ),
      }));
  }, []);

  const formatMenuItemsWithAnimation = useCallback((items: MenuItem[]): MenuItem[] => {
    return items.map((item, index) => {
      if (typeof item === 'object' && item !== null) {
        return {
          ...item,
          className: `menu-item-enter`,
          style: {
            animationDelay: `${index * 0.05}s`,
            ...item.style,
          },
        };
      }
      return item;
    });
  }, []);

  const menuItems = useMemo(() => formatMenus(menus), [menus]);

  const animatedMenuItems = useMemo(
    () => formatMenuItemsWithAnimation(menuItems),
    [menuItems, formatMenuItemsWithAnimation]
  );

  const handleMenuClick = useCallback(
    ({ key }: { key: string }) => {
      if (key.startsWith('/')) {
        navigate(key);
      }
    },
    [navigate]
  );

  const handleToggleCollapsed = useCallback(() => {
    dispatch(toggleCollapsed());
  }, [dispatch]);

  const handleNoticeClick = useCallback(() => {
    navigate('/notice/my');
  }, [navigate]);

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        trigger={null}
        collapsible
        collapsed={collapsed}
        style={{
          transition: 'all 0.2s',
        }}
      >
        <div
          style={{
            height: '64px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontSize: '18px',
            fontWeight: 'bold',
            transition: 'all 0.2s',
          }}
        >
          {collapsed ? 'WS' : 'Web System'}
        </div>
        <div
          style={{
            opacity: loading ? 0.3 : 1,
            transition: 'opacity 0.3s ease-in-out',
          }}
        >
          {loading ? (
            <Menu theme="dark" mode="inline" items={renderSkeletonMenu()} />
          ) : (
            <Menu
              theme="dark"
              mode="inline"
              selectedKeys={[location.pathname]}
              items={animatedMenuItems}
              onClick={handleMenuClick}
              style={{
                transition: 'all 0.3s ease-in-out',
              }}
            />
          )}
        </div>
      </Sider>
      <Layout>
        <Header
          style={{
            padding: '0 16px',
            background: isDark ? '#141414' : '#fff',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            boxShadow: '0 1px 4px rgba(0,21,41,.08)',
            color: isDark ? 'rgba(255, 255, 255, 0.85)' : 'rgba(0, 0, 0, 0.85)',
          }}
        >
          <div>
            {React.createElement(collapsed ? MenuUnfoldOutlined : MenuFoldOutlined, {
              style: {
                fontSize: '18px',
                cursor: 'pointer',
                color: isDark ? 'rgba(255, 255, 255, 0.85)' : 'rgba(0, 0, 0, 0.85)',
              },
              onClick: handleToggleCollapsed,
            })}
          </div>
          <Space>
            <Badge count={unreadCount} overflowCount={99} size="small">
              <Button
                type="text"
                icon={<BellOutlined />}
                onClick={handleNoticeClick}
                style={{
                  color: isDark ? 'rgba(255, 255, 255, 0.85)' : 'rgba(0, 0, 0, 0.85)',
                }}
              />
            </Badge>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <div
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  cursor: 'pointer',
                  color: isDark ? 'rgba(255, 255, 255, 0.85)' : 'rgba(0, 0, 0, 0.85)',
                }}
              >
                {user?.avatar ? <Avatar src={user.avatar} /> : <Avatar icon={<UserOutlined />} />}
                <span style={{ marginLeft: '8px' }}>{user?.nickname || '加载中...'}</span>
              </div>
            </Dropdown>
          </Space>
        </Header>
        <Content
          style={{
            margin: '24px',
            padding: '24px',
            background: isDark ? '#1f1f1f' : '#fff',
            borderRadius: '8px',
            minHeight: 'calc(100vh - 112px)',
          }}
        >
          <Outlet />
        </Content>
      </Layout>
      <SettingsDrawer open={settingsDrawerOpen} onClose={handleCloseSettings} />
    </Layout>
  );
};

export default MainLayout;
