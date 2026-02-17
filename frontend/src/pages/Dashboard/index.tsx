import React, { useEffect, useState } from 'react';
import {
  App,
  Button,
  Card,
  Col,
  Form,
  Input,
  Modal,
  Row,
  Select,
  Space,
  Spin,
  Statistic,
} from 'antd';
import './Dashboard.css';
import {
  DesktopOutlined,
  FileTextOutlined,
  PlusOutlined,
  SafetyOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons';
import {
  getPermissionCount,
  getRoleCount,
  getSystemStatus,
  getTodayLoginCount,
  getUserCount,
  type SystemStatus,
} from '@/services/dashboard.ts';
import { createUser } from '@/services/user.ts';
import { useNavigate } from 'react-router-dom';

const Dashboard: React.FC = () => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(true);
  const [systemStatus, setSystemStatus] = useState<SystemStatus | null>(null);
  const [userCount, setUserCount] = useState(0);
  const [roleCount, setRoleCount] = useState(0);
  const [permissionCount, setPermissionCount] = useState(0);
  const [todayLoginCount, setTodayLoginCount] = useState(0);

  const [addUserModalVisible, setAddUserModalVisible] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [status, users, roles, permissions, logins] = await Promise.all([
          getSystemStatus(),
          getUserCount(),
          getRoleCount(),
          getPermissionCount(),
          getTodayLoginCount(),
        ]);

        setSystemStatus(status);
        setUserCount(users);
        setRoleCount(roles);
        setPermissionCount(permissions);
        setTodayLoginCount(logins);
      } catch {
        console.error('操作失败');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const formatUptime = (uptime: number) => {
    const days = Math.floor(uptime / (1000 * 60 * 60 * 24));
    const hours = Math.floor((uptime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((uptime % (1000 * 60 * 60)) / (1000 * 60));
    return `${days}天 ${hours}小时 ${minutes}分钟`;
  };

  const handleQuickAddUser = () => {
    setAddUserModalVisible(true);
  };

  const handleAddUserSubmit = async () => {
    try {
      const values = await form.validateFields();
      await createUser(values);
      message.success('用户创建成功');
      setAddUserModalVisible(false);
      form.resetFields();
      const newCount = await getUserCount();
      setUserCount(newCount);
    } catch {
      message.error('用户创建失败');
    }
  };

  const handleConfigureRoles = async () => {
    try {
      navigate('/system/role');
    } catch {
      message.error('跳转失败');
    }
  };

  const handleViewSystemLogs = async () => {
    try {
      navigate('/system/log/login');
    } catch {
      message.error('跳转失败');
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div>
      <h1 style={{ marginBottom: '24px' }}>仪表盘</h1>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="用户总数"
              value={userCount}
              prefix={<UserOutlined />}
              className="user-statistic"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="角色总数"
              value={roleCount}
              prefix={<TeamOutlined />}
              className="role-statistic"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="权限总数"
              value={permissionCount}
              prefix={<SafetyOutlined />}
              className="permission-statistic"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="今日登录"
              value={todayLoginCount}
              prefix={<FileTextOutlined />}
              className="login-statistic"
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} style={{ marginTop: '24px' }}>
        <Col xs={24} lg={12}>
          <Card title="系统信息" variant="borderless">
            <p>
              <DesktopOutlined /> 操作系统：{systemStatus?.osName} {systemStatus?.osVersion}
            </p>
            <p>CPU核心数：{systemStatus?.availableProcessors}</p>
            <p>系统负载：{systemStatus?.systemLoadAverage?.toFixed(2)}</p>
            <p>运行时间：{systemStatus ? formatUptime(systemStatus.uptime) : '-'}</p>
            <p>
              JVM版本：{systemStatus?.vmVendor} {systemStatus?.vmVersion}
            </p>
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="快速操作" variant="borderless">
            <Space orientation="vertical" size="middle" style={{ width: '100%' }}>
              <Button type="primary" icon={<PlusOutlined />} block onClick={handleQuickAddUser}>
                添加新用户
              </Button>
              <Button icon={<SafetyOutlined />} block onClick={handleConfigureRoles}>
                配置角色权限
              </Button>
              <Button icon={<FileTextOutlined />} block onClick={handleViewSystemLogs}>
                查看登录日志
              </Button>
            </Space>
          </Card>
        </Col>
      </Row>

      <Modal
        title="新增用户"
        open={addUserModalVisible}
        onOk={handleAddUserSubmit}
        onCancel={() => {
          setAddUserModalVisible(false);
          form.resetFields();
        }}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="password" label="密码" rules={[{ required: true }]}>
            <Input.Password />
          </Form.Item>
          <Form.Item name="nickname" label="昵称">
            <Input />
          </Form.Item>
          <Form.Item name="email" label="邮箱" rules={[{ type: 'email' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true }]} initialValue={1}>
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default Dashboard;
