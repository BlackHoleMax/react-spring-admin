import React, { useCallback, useEffect, useState } from 'react';
import type { UploadProps } from 'antd';
import { App, Badge, Button, Form, Input, Modal, Radio, Statistic, Upload } from 'antd';
import {
  CameraOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  EditOutlined,
  KeyOutlined,
  LockOutlined,
  MailOutlined,
  PhoneOutlined,
  QuestionCircleOutlined,
  SafetyOutlined,
  UserOutlined,
  WomanOutlined,
} from '@ant-design/icons';
import { getProfile, updateAvatar, updatePassword, updateProfile } from '@/services/profile.ts';
import type { UserProfile } from '@/types';
import { useAppDispatch, useAppSelector } from '@/store/hooks.ts';
import { setUser } from '@/store/slices/authSlice.ts';
import './index.css';
import dayjs from 'dayjs';

const Profile: React.FC = () => {
  const { message } = App.useApp();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(false);
  const [passwordModalVisible, setPasswordModalVisible] = useState(false);
  const [profileModalVisible, setProfileModalVisible] = useState(false);
  const [passwordForm] = Form.useForm();
  const [profileForm] = Form.useForm();
  const dispatch = useAppDispatch();
  const themeMode = useAppSelector((state) => state.theme.mode);

  const loadProfile = useCallback(async () => {
    try {
      const data = await getProfile();
      setProfile(data);
      dispatch(
        setUser({
          id: data.id,
          username: data.username,
          nickname: data.nickname,
          avatar: data.avatar,
          email: data.email,
          phone: data.phone,
          status: 1,
        })
      );
    } catch {
      message.error('加载用户信息失败');
    }
  }, [dispatch, message]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  const handleAvatarUpload: UploadProps['customRequest'] = async (options) => {
    const { file, onSuccess, onError } = options;
    const formData = new FormData();
    formData.append('file', file as File);

    try {
      const response = await fetch('/api/profile/upload/avatar', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: formData,
      });

      const result = await response.json();
      if (result.code === 200) {
        await updateAvatar(result.data.url);
        message.success('头像上传成功');
        await loadProfile();
        onSuccess?.(result);
      } else {
        message.error(result.msg || '头像上传失败');
        onError?.(new Error(result.msg));
      }
    } catch (error) {
      message.error('头像上传失败');
      onError?.(error as Error);
    }
  };

  const handlePasswordSubmit = async (values: { oldPassword: string; newPassword: string }) => {
    setLoading(true);
    try {
      await updatePassword(values);
      message.success('密码修改成功');
      passwordForm.resetFields();
      setPasswordModalVisible(false);
    } catch (error: unknown) {
      const err = error as { response?: { data?: { msg?: string } } };
      message.error(err.response?.data?.msg || '密码修改失败');
    } finally {
      setLoading(false);
    }
  };

  const handleEditProfile = () => {
    profileForm.setFieldsValue({
      nickname: profile?.nickname,
      email: profile?.email,
      phone: profile?.phone,
      gender: profile?.gender,
    });
    setProfileModalVisible(true);
  };

  const handleProfileSubmit = async (values: {
    nickname: string;
    email: string;
    phone: string;
    gender?: number;
  }) => {
    setLoading(true);
    try {
      const data = await updateProfile(values);
      setProfile(data);
      dispatch(
        setUser({
          id: data.id,
          username: data.username,
          nickname: data.nickname,
          avatar: data.avatar,
          email: data.email,
          phone: data.phone,
          status: 1,
        })
      );
      message.success('个人资料更新成功');
      profileForm.resetFields();
      setProfileModalVisible(false);
    } catch (error: unknown) {
      const err = error as { response?: { data?: { msg?: string } } };
      message.error(err.response?.data?.msg || '更新失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={`profile-container ${themeMode === 'dark' ? 'dark' : ''}`}>
      <div className="profile-grid">
        <div className="profile-left">
          <div className="glass-card profile-card">
            <div className="avatar-section">
              <div className="avatar-wrapper">
                <Upload customRequest={handleAvatarUpload} showUploadList={false} accept="image/*">
                  <div className="avatar-container">
                    {profile?.avatar ? (
                      <img src={profile.avatar} alt="avatar" className="avatar-image" />
                    ) : (
                      <div className="avatar-placeholder">
                        <UserOutlined className="avatar-icon" />
                      </div>
                    )}
                    <div className="avatar-overlay">
                      <CameraOutlined className="camera-icon" />
                      <span className="upload-text">更换头像</span>
                    </div>
                  </div>
                </Upload>
              </div>
              <div className="user-title">
                <h2 className="user-nickname">{profile?.nickname || profile?.username}</h2>
                <Badge status="success" text="在线" className="user-status" />
              </div>
            </div>

            <div className="info-divider" />

            <div className="info-section">
              <div className="info-item">
                <div className="info-icon">
                  <UserOutlined />
                </div>
                <div className="info-content">
                  <div className="info-label">用户名</div>
                  <div className="info-value">{profile?.username}</div>
                </div>
              </div>

              <div className="info-item">
                <div className="info-icon">
                  <MailOutlined />
                </div>
                <div className="info-content">
                  <div className="info-label">邮箱</div>
                  <div className="info-value">{profile?.email || '未设置'}</div>
                </div>
              </div>

              <div className="info-item">
                <div className="info-icon">
                  <PhoneOutlined />
                </div>
                <div className="info-content">
                  <div className="info-label">手机号</div>
                  <div className="info-value">{profile?.phone || '未设置'}</div>
                </div>
              </div>

              <div className="info-item">
                <div className="info-icon">
                  {profile?.gender === 0 ? (
                    <UserOutlined />
                  ) : profile?.gender === 1 ? (
                    <WomanOutlined />
                  ) : (
                    <QuestionCircleOutlined />
                  )}
                </div>
                <div className="info-content">
                  <div className="info-label">性别</div>
                  <div className="info-value">
                    {profile?.gender === 0 ? '男' : profile?.gender === 1 ? '女' : '未设置'}
                  </div>
                </div>
              </div>

              <div className="info-item">
                <div className="info-icon">
                  <ClockCircleOutlined />
                </div>
                <div className="info-content">
                  <div className="info-label">注册时间</div>
                  <div className="info-value">
                    {dayjs(profile?.createTime).format('YYYY-MM-DD HH:mm:ss')}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div className="profile-right">
          <div className="glass-card stats-card">
            <h3 className="card-title">
              <CheckCircleOutlined className="title-icon" />
              账户统计
            </h3>
            <div className="stats-grid">
              <div className="stat-item">
                <Statistic
                  title="登录次数"
                  value={profile?.loginCount || 0}
                  suffix="次"
                  styles={{ content: { color: '#1890ff', fontSize: '24px', fontWeight: 600 } }}
                />
              </div>
              <div className="stat-item">
                <Statistic
                  title="账户状态"
                  value="正常"
                  styles={{ content: { color: '#52c41a', fontSize: '24px', fontWeight: 600 } }}
                />
              </div>
            </div>
          </div>

          <div className="glass-card actions-card">
            <h3 className="card-title">
              <SafetyOutlined className="title-icon" />
              快捷操作
            </h3>
            <div className="actions-grid">
              <button className="action-button" onClick={() => setPasswordModalVisible(true)}>
                <div className="action-icon">
                  <KeyOutlined />
                </div>
                <div className="action-content">
                  <div className="action-title">修改密码</div>
                  <div className="action-desc">定期更换密码保障安全</div>
                </div>
              </button>

              <button className="action-button" onClick={handleEditProfile}>
                <div className="action-icon">
                  <EditOutlined />
                </div>
                <div className="action-content">
                  <div className="action-title">编辑资料</div>
                  <div className="action-desc">完善个人信息</div>
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>

      <Modal
        title={
          <div className="modal-title">
            <LockOutlined className="modal-title-icon" />
            修改密码
          </div>
        }
        open={passwordModalVisible}
        onCancel={() => {
          setPasswordModalVisible(false);
          passwordForm.resetFields();
        }}
        footer={null}
        width={480}
        centered
      >
        <Form
          form={passwordForm}
          layout="vertical"
          onFinish={handlePasswordSubmit}
          className="password-form"
        >
          <Form.Item
            name="oldPassword"
            label="旧密码"
            rules={[{ required: true, message: '请输入旧密码' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请输入旧密码" size="large" />
          </Form.Item>
          <Form.Item
            name="newPassword"
            label="新密码"
            rules={[
              { required: true, message: '请输入新密码' },
              { min: 6, message: '密码长度不能少于6位' },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请输入新密码（至少6位）"
              size="large"
            />
          </Form.Item>
          <Form.Item
            name="confirmPassword"
            label="确认密码"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="请再次输入新密码" size="large" />
          </Form.Item>
          <Form.Item className="form-actions">
            <Button type="primary" htmlType="submit" loading={loading} size="large" block>
              确认修改
            </Button>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={
          <div className="modal-title">
            <EditOutlined className="modal-title-icon" />
            编辑个人资料
          </div>
        }
        open={profileModalVisible}
        onCancel={() => {
          setProfileModalVisible(false);
          profileForm.resetFields();
        }}
        footer={null}
        width={480}
        centered
      >
        <Form
          form={profileForm}
          layout="vertical"
          onFinish={handleProfileSubmit}
          className="password-form"
        >
          <Form.Item
            name="nickname"
            label="昵称"
            rules={[{ required: true, message: '请输入昵称' }]}
          >
            <Input prefix={<UserOutlined />} placeholder="请输入昵称" size="large" />
          </Form.Item>
          <Form.Item
            name="email"
            label="邮箱"
            rules={[{ type: 'email', message: '请输入有效的邮箱地址' }]}
          >
            <Input prefix={<MailOutlined />} placeholder="请输入邮箱" size="large" />
          </Form.Item>
          <Form.Item
            name="phone"
            label="手机号"
            rules={[{ pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }]}
          >
            <Input prefix={<PhoneOutlined />} placeholder="请输入手机号" size="large" />
          </Form.Item>
          <Form.Item name="gender" label="性别">
            <Radio.Group>
              <Radio value={0}>男</Radio>
              <Radio value={1}>女</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item className="form-actions">
            <Button type="primary" htmlType="submit" loading={loading} size="large" block>
              保存修改
            </Button>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Profile;
