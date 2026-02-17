import React, { useCallback, useEffect, useRef, useState } from 'react';
import { App, Divider, Drawer, Menu, Space, Switch, Typography } from 'antd';
import { BellOutlined, BgColorsOutlined, LockOutlined } from '@ant-design/icons';
import { useAppDispatch, useAppSelector } from '@/store/hooks.ts';
import { setCompactMode, setPrimaryColor, toggleTheme } from '@/store/slices/themeSlice.ts';
import { useSessionTimeout } from '@/hooks/useSessionTimeout';
import DayNightToggle from '../../components/DayNightToggle';
import { getCaptchaSettings, updateCaptchaSettings } from '@/services/settings';
import { requestNotificationPermission } from '../../utils/notificationHelper';
import './index.css';

const { Title, Text } = Typography;

type SettingSection = 'appearance' | 'notification' | 'security';

interface SettingsDrawerProps {
  open: boolean;
  onClose: () => void;
}

interface NotificationSettings {
  desktopEnabled: boolean;
  soundEnabled: boolean;
}

const SettingsDrawer: React.FC<SettingsDrawerProps> = ({ open, onClose }) => {
  const { message } = App.useApp();
  const [selectedSection, setSelectedSection] = useState<SettingSection>('appearance');
  const [captchaEnabled, setCaptchaEnabled] = useState(true);
  const [drawerWidth, setDrawerWidth] = useState(720);
  const [isResizing, setIsResizing] = useState(false);
  const [notificationSettings, setNotificationSettings] = useState<NotificationSettings>(() => {
    const savedSettings = localStorage.getItem('notificationSettings');
    if (savedSettings) {
      try {
        return JSON.parse(savedSettings);
      } catch {
        console.error('操作失败');
      }
    }
    return {
      desktopEnabled: true,
      soundEnabled: true,
    };
  });

  const drawerRef = useRef<HTMLDivElement>(null);
  const resizeHandleRef = useRef<HTMLDivElement>(null);

  const dispatch = useAppDispatch();
  const themeMode = useAppSelector((state) => state.theme.mode);
  const primaryColor = useAppSelector((state) => state.theme.primaryColor);
  const compactMode = useAppSelector((state) => state.theme.compactMode);
  const isDarkMode = themeMode === 'dark';

  const { toggleSessionTimeout, sessionTimeoutEnabled, sessionTimeoutMinutes } =
    useSessionTimeout();

  const colorOptions = [
    { color: '#1890ff', name: '默认蓝' },
    { color: '#52c41a', name: '成功绿' },
    { color: '#fa8c16', name: '警告橙' },
    { color: '#eb2f96', name: '错误红' },
    { color: '#722ed1', name: '紫色' },
    { color: '#13c2c2', name: '青色' },
  ];

  const handleThemeChange = useCallback(() => {
    dispatch(toggleTheme());
  }, [dispatch]);

  const handleColorChange = useCallback(
    (color: string) => {
      dispatch(setPrimaryColor(color));
      message.success('主题色已更新');
    },
    [dispatch, message]
  );

  const handleCompactModeChange = useCallback(
    (checked: boolean) => {
      dispatch(setCompactMode(checked));
      message.success(checked ? '已开启紧凑模式' : '已关闭紧凑模式');
    },
    [dispatch, message]
  );

  // 保存通知设置
  const saveNotificationSettings = useCallback((settings: NotificationSettings) => {
    localStorage.setItem('notificationSettings', JSON.stringify(settings));
    setNotificationSettings(settings);
  }, []);

  const handleDesktopNotificationChange = useCallback(
    async (checked: boolean) => {
      if (checked) {
        const granted = await requestNotificationPermission();
        if (granted) {
          saveNotificationSettings({ ...notificationSettings, desktopEnabled: checked });
          message.success('桌面通知已开启');
        } else {
          message.error('桌面通知权限被拒绝');
          saveNotificationSettings({ ...notificationSettings, desktopEnabled: false });
        }
      } else {
        saveNotificationSettings({ ...notificationSettings, desktopEnabled: checked });
        message.success('桌面通知已关闭');
      }
    },
    [notificationSettings, saveNotificationSettings, message]
  );

  const handleSoundNotificationChange = useCallback(
    (checked: boolean) => {
      saveNotificationSettings({ ...notificationSettings, soundEnabled: checked });
      message.success(checked ? '声音提示已开启' : '声音提示已关闭');
    },
    [notificationSettings, saveNotificationSettings, message]
  );

  useEffect(() => {
    if (open) {
      const fetchCaptchaSettings = async () => {
        try {
          const settings = await getCaptchaSettings();
          setCaptchaEnabled(settings.loginEnabled);
        } catch {
          message.error('获取验证码设置失败');
        }
      };
      fetchCaptchaSettings();
    }
  }, [open, message]);

  const handleCaptchaChange = useCallback(
    async (checked: boolean) => {
      try {
        await updateCaptchaSettings({ loginEnabled: checked });
        setCaptchaEnabled(checked);
        message.success('验证码设置已更新');
      } catch {
        message.error('更新验证码设置失败');
      }
    },
    [message]
  );

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      if (!isResizing) return;

      const newWidth = window.innerWidth - e.clientX;
      const minWidth = 500;
      const maxWidth = window.innerWidth - 100;

      if (newWidth >= minWidth && newWidth <= maxWidth) {
        setDrawerWidth(newWidth);
      }
    };

    const handleMouseUp = () => {
      setIsResizing(false);
    };

    if (isResizing) {
      document.addEventListener('mousemove', handleMouseMove);
      document.addEventListener('mouseup', handleMouseUp);
    }

    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };
  }, [isResizing]);

  const handleResizeStart = useCallback((e: React.MouseEvent) => {
    e.preventDefault();
    setIsResizing(true);
  }, []);

  const menuItems = [
    {
      key: 'appearance',
      icon: <BgColorsOutlined />,
      label: '外观设置',
    },
    {
      key: 'notification',
      icon: <BellOutlined />,
      label: '通知设置',
    },
    {
      key: 'security',
      icon: <LockOutlined />,
      label: '安全设置',
    },
  ];

  const renderContent = () => {
    switch (selectedSection) {
      case 'appearance':
        return (
          <div className="settings-content">
            <Title level={4}>外观设置</Title>
            <Divider />

            <div className="setting-item">
              <div className="setting-item-info">
                <Text strong>主题模式</Text>
                <Text type="secondary">切换明亮/暗黑主题</Text>
              </div>
              <DayNightToggle isDarkMode={isDarkMode} onToggle={handleThemeChange} />
            </div>

            <div className="setting-item">
              <div className="setting-item-info">
                <Text strong>主题色</Text>
                <Text type="secondary">自定义系统主题颜色</Text>
              </div>
              <Space size={8}>
                {colorOptions.map((option) => (
                  <div
                    key={option.color}
                    className={`color-picker-item ${primaryColor === option.color ? 'active' : ''}`}
                    style={{ background: option.color }}
                    onClick={() => handleColorChange(option.color)}
                    title={option.name}
                  />
                ))}
              </Space>
            </div>

            <div className="setting-item">
              <div className="setting-item-info">
                <Text strong>紧凑模式</Text>
                <Text type="secondary">减小界面元素间距</Text>
              </div>
              <Switch checked={compactMode} onChange={handleCompactModeChange} />
            </div>
          </div>
        );

      case 'notification':
        return (
          <div className="settings-content">
            <Title level={4}>通知设置</Title>
            <Divider />

            <div className="setting-item">
              <div className="setting-item-info">
                <Text strong>桌面通知</Text>
                <Text type="secondary">允许显示桌面通知</Text>
              </div>
              <Switch
                checked={notificationSettings.desktopEnabled}
                onChange={handleDesktopNotificationChange}
              />
            </div>

            <div className="setting-item">
              <div className="setting-item-info">
                <Text strong>声音提示</Text>
                <Text type="secondary">新消息声音提醒</Text>
              </div>
              <Switch
                checked={notificationSettings.soundEnabled}
                onChange={handleSoundNotificationChange}
              />
            </div>
          </div>
        );

      case 'security':
        return (
          <div className="settings-content">
            <Title level={4}>安全设置</Title>
            <Divider />

            <div className="setting-item">
              <div className="setting-item-info">
                <Text strong>登录验证码</Text>
                <Text type="secondary">开启登录时的验证码验证</Text>
              </div>
              <Switch checked={captchaEnabled} onChange={handleCaptchaChange} />
            </div>

            <div className="setting-item">
              <div className="setting-item-info">
                <Text strong>会话超时</Text>
                <Text type="secondary">{sessionTimeoutMinutes}分钟无操作自动登出</Text>
              </div>
              <Switch checked={sessionTimeoutEnabled} onChange={toggleSessionTimeout} />
            </div>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <Drawer
      title={
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <span style={{ marginRight: '12px' }}>⚙️</span>
          <span>系统设置</span>
        </div>
      }
      placement="right"
      size={drawerWidth}
      open={open}
      onClose={onClose}
      styles={{
        body: { padding: 0 },
      }}
    >
      <div ref={drawerRef} className="settings-drawer-container">
        <div className="settings-sidebar">
          <Menu
            mode="inline"
            selectedKeys={[selectedSection]}
            items={menuItems}
            onClick={({ key }) => setSelectedSection(key as SettingSection)}
            style={{ border: 'none' }}
          />
        </div>
        <div className="settings-main">{renderContent()}</div>
      </div>
      <div
        ref={resizeHandleRef}
        className="resize-handle"
        onMouseDown={handleResizeStart}
        style={{
          cursor: isResizing ? 'col-resize' : 'col-resize',
          userSelect: 'none',
        }}
      />
    </Drawer>
  );
};

export default SettingsDrawer;
