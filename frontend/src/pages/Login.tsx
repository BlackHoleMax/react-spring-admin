import React, { useCallback, useEffect, useState } from 'react';
import { App, Button, Checkbox, Form, Input, Modal } from 'antd';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { login as loginAction } from '../store/slices/authSlice';
import type { Result } from '@/types';
import './Login.css';
import { fetchUserMenus } from '@/store/slices/menuSlice.ts';
import { getCaptchaSettings } from '@/services/settings';
import TianaiCaptcha from '../components/TianaiCaptcha';

interface LocationState {
  from?: {
    pathname: string;
  };
}

const Login: React.FC = () => {
  const { message } = App.useApp();
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useAppDispatch();
  const { loading, isAuthenticated, error } = useAppSelector((state) => state.auth);

  const [captchaEnabled, setCaptchaEnabled] = useState(true);
  const [showCaptchaModal, setShowCaptchaModal] = useState(false);
  const [form] = Form.useForm();
  const hasFetchedCaptchaConfig = React.useRef(false);

  useEffect(() => {
    if (isAuthenticated) {
      const from = (location.state as LocationState)?.from?.pathname || '/dashboard';
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, location.state]);

  useEffect(() => {
    if (error) {
      message.error(error);
    }
  }, [error, message]);

  useEffect(() => {
    const fetchCaptchaConfig = async () => {
      try {
        const settings = await getCaptchaSettings();
        // 只在第一次获取时打印
        if (!hasFetchedCaptchaConfig.current) {
          console.log('获取到的验证码配置:', settings);
          hasFetchedCaptchaConfig.current = true;
        }
        setCaptchaEnabled(settings.loginEnabled);
      } catch (error) {
        console.error('获取验证码配置失败:', error);
        // 默认开启验证码
        setCaptchaEnabled(true);
      }
    };
    fetchCaptchaConfig();
  }, []);

  const handleLogin = async () => {
    try {
      const values = await form.validateFields();
      console.log('当前验证码配置状态:', captchaEnabled);

      if (captchaEnabled) {
        console.log('验证码已开启，显示验证码模态框');
        setShowCaptchaModal(true);
      } else {
        console.log('验证码已关闭，直接登录');
        try {
          await dispatch(
            loginAction({
              username: values.username,
              password: values.password,
              rememberMe: values.remember,
            })
          ).unwrap();

          message.success('登录成功');

          await dispatch(fetchUserMenus());

          const from = (location.state as LocationState)?.from?.pathname || '/dashboard';
          navigate(from, { replace: true });
        } catch (error: unknown) {
          console.error('登录失败:', error);
          const errorMessage = error instanceof Error ? error.message : '登录失败，请重试';
          message.error(errorMessage);
        }
      }
    } catch (err) {
      console.log('表单验证失败', err);
      message.error('请输入用户名和密码');
    }
  };

  const handleCaptchaSuccess = useCallback(
    async (response: Result) => {
      console.log('验证码验证成功，响应数据:', response);
      setShowCaptchaModal(false);

      const values = form.getFieldsValue();

      try {
        await dispatch(
          loginAction({
            username: values.username,
            password: values.password,
            rememberMe: values.remember,
            captchaVerification: JSON.stringify(response.data || response),
          })
        ).unwrap();

        message.success('登录成功');

        await dispatch(fetchUserMenus()).unwrap();

        const from = (location.state as LocationState)?.from?.pathname || '/dashboard';
        navigate(from, { replace: true });
      } catch (error: unknown) {
        console.error('登录失败:', error);
        const errorMessage = error instanceof Error ? error.message : '登录失败，请重试';
        message.error(errorMessage);
      }
    },
    [dispatch, form, navigate, location.state, message]
  );

  const handleForgotPassword = () => {
    message.info('请联系管理员重置密码');
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1 className="login-title">React Spring Admin</h1>
          <p className="login-subtitle">Enterprise Management Platform</p>
        </div>

        <Form
          name="login"
          form={form}
          autoComplete="off"
          size="large"
          className="login-form"
          initialValues={{ remember: false }}
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
            className="login-input-item"
          >
            <Input
              prefix={<UserOutlined className="login-input-icon" />}
              placeholder="用户名 / 邮箱"
              className="login-input"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
            className="login-input-item"
          >
            <Input.Password
              prefix={<LockOutlined className="login-input-icon" />}
              placeholder="密码"
              className="login-input"
            />
          </Form.Item>

          <div className="login-options-item">
            <Form.Item name="remember" valuePropName="checked" noStyle>
              <Checkbox className="login-checkbox">记住我</Checkbox>
            </Form.Item>
            <Button type="link" className="login-forgot-link" onClick={handleForgotPassword}>
              忘记密码？
            </Button>
          </div>

          <Form.Item className="login-button-item">
            <Button
              type="primary"
              htmlType="submit"
              className="login-button"
              loading={loading}
              onClick={(e) => {
                e.preventDefault();
                handleLogin();
              }}
            >
              登录
            </Button>
          </Form.Item>
        </Form>
      </div>

      <Modal
        title="安全验证"
        open={showCaptchaModal}
        onCancel={() => setShowCaptchaModal(false)}
        footer={null}
        width={400}
        centered
        maskClosable={false}
      >
        <div style={{ padding: '20px 0' }}>
          <TianaiCaptcha onVerify={handleCaptchaSuccess} />
        </div>
      </Modal>
    </div>
  );
};

export default Login;
