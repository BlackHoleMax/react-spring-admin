interface NotificationSettings {
  desktopEnabled: boolean;
  soundEnabled: boolean;
}

// 全局音频上下文，用于避免重复创建
let audioContext: AudioContext | null = null;

/**
 * 获取通知设置
 */
export const getNotificationSettings = (): NotificationSettings => {
  const savedSettings = localStorage.getItem('notificationSettings');
  if (savedSettings) {
    try {
      return JSON.parse(savedSettings);
    } catch (error) {
      console.error('加载通知设置失败:', error);
    }
  }
  return {
    desktopEnabled: true,
    soundEnabled: true,
  };
};

/**
 * 初始化音频上下文
 */
const initAudioContext = (): AudioContext | null => {
  if (!audioContext) {
    try {
      const AudioContextClass = window.AudioContext || (window as any).webkitAudioContext;
      if (AudioContextClass) {
        audioContext = new AudioContextClass();
      }
    } catch (error) {
      console.error('初始化音频上下文失败:', error);
    }
  }
  return audioContext;
};

/**
 * 播放通知声音
 */
export const playNotificationSound = async () => {
  const settings = getNotificationSettings();
  if (!settings.soundEnabled) {
    return;
  }

  try {
    const ctx = initAudioContext();
    if (!ctx) {
      return;
    }

    // 如果音频上下文被挂起，尝试恢复
    if (ctx.state === 'suspended') {
      try {
        await ctx.resume();
      } catch {
        return;
      }
    }

    // 使用 requestAnimationFrame 确保在合适的时机播放音频
    requestAnimationFrame(() => {
      const oscillator = ctx.createOscillator();
      const gainNode = ctx.createGain();

      oscillator.connect(gainNode);
      gainNode.connect(ctx.destination);

      oscillator.frequency.value = 800;
      oscillator.type = 'sine';

      gainNode.gain.setValueAtTime(0.3, ctx.currentTime);
      gainNode.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + 0.5);

      oscillator.start(ctx.currentTime);
      oscillator.stop(ctx.currentTime + 0.5);
    });
  } catch {
    // 静默失败，不影响用户体验
  }
};

/**
 * 显示桌面通知
 */
export const showDesktopNotification = (title: string, body: string, onClick?: () => void) => {
  const settings = getNotificationSettings();
  if (!settings.desktopEnabled) {
    return;
  }

  if (!('Notification' in window)) {
    return;
  }

  if (Notification.permission === 'granted') {
    try {
      const notification = new Notification(title, {
        body,
        icon: '/vite.svg',
        badge: '/vite.svg',
        tag: `notification-${Date.now()}`,
      });

      if (onClick) {
        notification.onclick = () => {
          onClick();
          window.focus();
          notification.close();
        };
      }

      // 自动关闭通知
      setTimeout(() => {
        notification.close();
      }, 5000);
    } catch {
      // 静默失败，不影响用户体验
    }
  }
};

/**
 * 显示完整通知（桌面通知 + 声音提示）
 */
export const showNotification = (title: string, body: string, onClick?: () => void) => {
  showDesktopNotification(title, body, onClick);
  playNotificationSound();
};

/**
 * 请求桌面通知权限
 */
export const requestNotificationPermission = async (): Promise<boolean> => {
  if (!('Notification' in window)) {
    return false;
  }

  if (Notification.permission === 'granted') {
    return true;
  }

  if (Notification.permission !== 'denied') {
    const permission = await Notification.requestPermission();
    return permission === 'granted';
  }

  return false;
};
