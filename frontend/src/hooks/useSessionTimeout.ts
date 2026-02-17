import { useCallback, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Modal, App } from 'antd';
import { useNavigate } from 'react-router-dom';
import type { RootState } from '@/store';
import {
  resetSession,
  setSessionTimeoutEnabled,
  setWarningShown,
  updateLastActivity,
} from '@/store/slices/sessionSlice';
import { logout } from '@/store/slices/authSlice';

export const useSessionTimeout = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { message } = App.useApp();
  const { sessionTimeoutEnabled, sessionTimeoutMinutes, lastActivityTime, warningShown } =
    useSelector((state: RootState) => state.session);
  const { isAuthenticated } = useSelector((state: RootState) => state.auth);

  const handleUserActivity = useCallback(() => {
    if (sessionTimeoutEnabled && isAuthenticated) {
      dispatch(updateLastActivity());
      if (warningShown) {
        dispatch(setWarningShown(false));
      }
    }
  }, [dispatch, sessionTimeoutEnabled, isAuthenticated, warningShown]);

  const checkSessionTimeout = useCallback(() => {
    if (!sessionTimeoutEnabled || !isAuthenticated) return;

    const now = Date.now();
    const inactiveTime = now - lastActivityTime;
    const timeoutMs = sessionTimeoutMinutes * 60 * 1000;
    const warningTimeMs = (sessionTimeoutMinutes - 5) * 60 * 1000;

    if (inactiveTime >= warningTimeMs && inactiveTime < timeoutMs && !warningShown) {
      dispatch(setWarningShown(true));
      Modal.warning({
        title: '会话即将过期',
        content: `您的会话将在5分钟后过期，请继续操作以延长会话时间。`,
        okText: '我知道了',
        onOk: () => {
          dispatch(updateLastActivity());
          dispatch(setWarningShown(false));
        },
      });
      return;
    }

    if (inactiveTime >= timeoutMs) {
      message.warning('会话已过期，请重新登录');
      dispatch(logout() as any);
      navigate('/login');
      return;
    }
  }, [
    dispatch,
    sessionTimeoutEnabled,
    isAuthenticated,
    lastActivityTime,
    sessionTimeoutMinutes,
    warningShown,
    navigate,
    message,
  ]);

  useEffect(() => {
    if (!sessionTimeoutEnabled || !isAuthenticated) return;

    const events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];

    events.forEach((event) => {
      document.addEventListener(event, handleUserActivity, true);
    });

    return () => {
      events.forEach((event) => {
        document.removeEventListener(event, handleUserActivity, true);
      });
    };
  }, [handleUserActivity, sessionTimeoutEnabled, isAuthenticated]);

  useEffect(() => {
    if (!sessionTimeoutEnabled || !isAuthenticated) return;

    const interval = setInterval(checkSessionTimeout, 30 * 1000);

    return () => clearInterval(interval);
  }, [checkSessionTimeout, sessionTimeoutEnabled, isAuthenticated]);

  const resetSessionActivity = useCallback(() => {
    dispatch(resetSession());
  }, [dispatch]);

  const toggleSessionTimeout = useCallback(
    (enabled: boolean) => {
      dispatch(setSessionTimeoutEnabled(enabled));
      if (enabled) {
        dispatch(resetSession());
        message.success('会话超时功能已开启');
      } else {
        message.info('会话超时功能已关闭');
      }
    },
    [dispatch, message]
  );

  return {
    resetSessionActivity,
    toggleSessionTimeout,
    sessionTimeoutEnabled,
    sessionTimeoutMinutes,
  };
};
