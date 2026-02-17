import React, { useCallback, useEffect, useState } from 'react';
import {
  App,
  Avatar,
  Button,
  Card,
  Col,
  Form,
  Input,
  Modal,
  Popconfirm,
  Row,
  Select,
  Space,
  Table,
  Tag,
  Typography,
  Upload,
} from 'antd';
import {
  CopyOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  ExportOutlined,
  ImportOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  UploadOutlined,
  UserOutlined,
} from '@ant-design/icons';
import Authorized from '@/components/Authorized';
import {
  assignRoles,
  batchDeleteUser,
  changePassword,
  createUser,
  deleteUser,
  getUserList,
  getUserRoles,
  importUser,
  resetPassword,
  updateUser,
  downloadUserTemplate,
} from '@/services/user.ts';
import { getRoleList } from '@/services/role.ts';
import type { Role, User, UserQuery } from '@/types';
import dayjs from 'dayjs';

const { Text } = Typography;

const UserManagement: React.FC = () => {
  const { message } = App.useApp();
  const [data, setData] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [passwordModalVisible, setPasswordModalVisible] = useState(false);
  const [avatarPreviewVisible, setAvatarPreviewVisible] = useState(false);
  const [previewAvatarUrl, setPreviewAvatarUrl] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [editablePassword, setEditablePassword] = useState('');
  const [isEditingPassword, setIsEditingPassword] = useState(false);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [allRoles, setAllRoles] = useState<Role[]>([]);
  const [selectedRoleIds, setSelectedRoleIds] = useState<number[]>([]);
  const [editingUser, setEditingUser] = useState<User | null>(null);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [searchParams, setSearchParams] = useState<UserQuery>({});
  const [importModalVisible, setImportModalVisible] = useState(false);
  const [importFileList, setImportFileList] = useState<any[]>([]);
  const [importing, setImporting] = useState(false);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  const fetchData = useCallback(
    async (page = 1, size = 10, params: UserQuery = {}) => {
      setLoading(true);
      try {
        const result = await getUserList({ current: page, size, ...params });
        setData(result.records);
        setPagination({ current: page, pageSize: size, total: result.total });
      } catch {
        message.error('获取数据失败');
      } finally {
        setLoading(false);
      }
    },
    [message]
  );

  useEffect(() => {
    fetchData(1, 10, searchParams);
  }, [fetchData, searchParams]);

  const copyPassword = () => {
    navigator.clipboard.writeText(isEditingPassword ? editablePassword : newPassword);
    message.success('密码已复制到剪贴板');
  };

  const startEditPassword = () => {
    setIsEditingPassword(true);
  };

  const cancelEditPassword = () => {
    setEditablePassword(newPassword);
    setIsEditingPassword(false);
  };

  const savePassword = async () => {
    if (!editablePassword || editablePassword.length < 6) {
      message.error('密码长度不能少于6位');
      return;
    }
    try {
      await changePassword(currentUserId!, editablePassword);
      setNewPassword(editablePassword);
      setIsEditingPassword(false);
      message.success('密码修改成功');
    } catch {
      message.error('密码修改失败');
    }
  };

  const handleAdd = async () => {
    setEditingUser(null);
    form.resetFields();
    setSelectedRoleIds([]);

    try {
      const roleResult = await getRoleList({ current: 1, size: 100 });
      setAllRoles(roleResult.records);
    } catch {
      message.error('获取角色列表失败');
    }

    setModalVisible(true);
  };

  const handleEdit = async (record: User) => {
    setEditingUser(record);
    form.setFieldsValue(record);

    try {
      const roleResult = await getRoleList({ current: 1, size: 100 });
      setAllRoles(roleResult.records);
      const currentRoles = await getUserRoles(record.id);
      setSelectedRoleIds(currentRoles.map((role) => role.id));
    } catch {
      message.error('获取角色信息失败');
    }

    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteUser(id);
      message.success('删除成功');
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch {
      message.error('删除失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    try {
      await batchDeleteUser(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch {
      message.error('批量删除失败');
    }
  };

  const handleExport = async () => {
    try {
      const response = await fetch('/api/user/export', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(selectedRowKeys.length > 0 ? selectedRowKeys : null),
      });

      if (response.ok) {
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `用户数据_${new Date().toLocaleDateString()}.xlsx`;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
        message.success('导出成功');
      } else {
        message.error('导出失败');
      }
    } catch {
      message.error('导出失败');
    }
  };

  const handleDownloadTemplate = async () => {
    try {
      const blob = await downloadUserTemplate();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = '用户导入模板.xlsx';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      message.success('模板下载成功');
    } catch {
      message.error('模板下载失败');
    }
  };

  const handleImport = async () => {
    if (importFileList.length === 0) {
      message.warning('请选择要导入的文件');
      return;
    }

    setImporting(true);
    try {
      const result = await importUser(importFileList[0].originFileObj);
      message.success(result || '导入成功');
      setImportModalVisible(false);
      setImportFileList([]);
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch (error: any) {
      message.error(error.message || '导入失败');
    } finally {
      setImporting(false);
    }
  };

  const handleImportCancel = () => {
    setImportModalVisible(false);
    setImportFileList([]);
  };

  const handleResetPassword = async (id: number) => {
    try {
      const password = await resetPassword(id);
      setNewPassword(password);
      setEditablePassword(password);
      setCurrentUserId(id);
      setIsEditingPassword(false);
      setPasswordModalVisible(true);
      message.success('密码重置成功');
    } catch {
      message.error('密码重置失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (editingUser) {
        await updateUser({ ...values, id: editingUser.id });
        message.success('更新成功');

        if (selectedRoleIds.length > 0) {
          try {
            await assignRoles(editingUser.id, selectedRoleIds);
            message.success('角色分配成功');
          } catch {
            message.error('角色分配失败');
          }
        }
      } else {
        await createUser(values);
        message.success('创建成功');
      }

      setModalVisible(false);
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch {
      message.error('操作失败');
    }
  };

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    const params: UserQuery = {
      username: values.username,
      nickname: values.nickname,
      status: values.status,
    };
    setSearchParams(params);
    fetchData(1, pagination.pageSize, params);
  };

  const handleReset = () => {
    searchForm.resetFields();
    setSearchParams({});
    fetchData(1, pagination.pageSize, {});
  };

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    {
      title: '头像',
      dataIndex: 'avatar',
      key: 'avatar',
      width: 80,
      render: (avatar: string) => (
        <div style={{ cursor: avatar ? 'zoom-in' : 'default' }}>
          <Avatar
            src={avatar}
            icon={!avatar ? <UserOutlined /> : undefined}
            size={40}
            style={{ backgroundColor: !avatar ? '#1890ff' : undefined }}
            onClick={() => {
              if (avatar) {
                setPreviewAvatarUrl(avatar);
                setAvatarPreviewVisible(true);
              }
            }}
          />
        </div>
      ),
    },
    { title: '用户名', dataIndex: 'username', key: 'username' },
    { title: '昵称', dataIndex: 'nickname', key: 'nickname' },
    { title: '邮箱', dataIndex: 'email', key: 'email' },
    { title: '手机号', dataIndex: 'phone', key: 'phone' },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'}>{status === 1 ? '启用' : '禁用'}</Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      render: (text: string | number | Date | dayjs.Dayjs | null | undefined) => {
        return dayjs(text).format('YYYY-MM-DD HH:mm:ss');
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 240,
      render: (_: unknown, record: User) => (
        <Space>
          <Authorized permission="user:edit">
            <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
              编辑
            </Button>
          </Authorized>

          <Authorized permission="user:edit">
            <Popconfirm title="确定重置密码?" onConfirm={() => handleResetPassword(record.id)}>
              <Button type="link">重置密码</Button>
            </Popconfirm>
          </Authorized>

          <Authorized permission="user:delete">
            <Popconfirm title="确定删除?" onConfirm={() => handleDelete(record.id)}>
              <Button type="link" danger icon={<DeleteOutlined />}>
                删除
              </Button>
            </Popconfirm>
          </Authorized>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Form form={searchForm} layout="inline">
          <Row gutter={[16, 16]} style={{ width: '100%' }}>
            <Col span={6}>
              <Form.Item name="username" label="用户名" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入用户名" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="nickname" label="昵称" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入昵称" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="status" label="状态" style={{ marginBottom: 0 }}>
                <Select placeholder="请选择状态" allowClear style={{ width: '100%' }}>
                  <Select.Option value={1}>启用</Select.Option>
                  <Select.Option value={0}>禁用</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Space>
                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                  搜索
                </Button>
                <Button onClick={handleReset}>重置</Button>
              </Space>
            </Col>
          </Row>
        </Form>
      </Card>
      <div
        style={{
          marginBottom: '16px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
        }}
      >
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>用户管理</h2>
        <Space>
          <Button
            icon={<ReloadOutlined />}
            onClick={() => fetchData(pagination.current, pagination.pageSize, searchParams)}
          >
            刷新
          </Button>
          <Authorized permission="user:import">
            <Button icon={<ImportOutlined />} onClick={() => setImportModalVisible(true)}>
              导入
            </Button>
          </Authorized>
          <Authorized permission="user:export">
            <Button icon={<ExportOutlined />} onClick={handleExport}>
              导出
            </Button>
          </Authorized>
          <Authorized permission="user:delete">
            {selectedRowKeys.length > 0 && (
              <Popconfirm title="确定删除选中的数据?" onConfirm={handleBatchDelete}>
                <Button danger icon={<DeleteOutlined />}>
                  批量删除 ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            )}
          </Authorized>
          <Authorized permission="user:add">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增用户
            </Button>
          </Authorized>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        rowSelection={{
          selectedRowKeys,
          onChange: setSelectedRowKeys,
        }}
        pagination={{
          ...pagination,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
        onChange={(pagination) => fetchData(pagination.current, pagination.pageSize, searchParams)}
      />
      <Modal
        title={editingUser ? '编辑用户' : '新增用户'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="username" label="用户名" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          {!editingUser && (
            <Form.Item name="password" label="密码" rules={[{ required: true }]}>
              <Input.Password />
            </Form.Item>
          )}
          <Form.Item name="nickname" label="昵称">
            <Input />
          </Form.Item>
          <Form.Item name="email" label="邮箱" rules={[{ type: 'email' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="phone" label="手机号">
            <Input />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true }]}>
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
          {editingUser && (
            <Form.Item label="角色">
              <Select
                mode="multiple"
                placeholder="请选择角色"
                value={selectedRoleIds}
                onChange={(values) => setSelectedRoleIds(values)}
                style={{ width: '100%' }}
                options={allRoles.map((role) => ({
                  label: role.name,
                  value: role.id,
                }))}
              />
            </Form.Item>
          )}
        </Form>
      </Modal>
      <Modal
        title="密码重置成功"
        open={passwordModalVisible}
        onCancel={() => {
          setPasswordModalVisible(false);
          setIsEditingPassword(false);
        }}
        footer={[
          <Button key="copy" icon={<CopyOutlined />} onClick={copyPassword}>
            复制密码
          </Button>,
          isEditingPassword ? (
            <>
              <Button key="cancel" onClick={cancelEditPassword}>
                取消
              </Button>
              <Button key="save" type="primary" onClick={savePassword}>
                保存修改
              </Button>
            </>
          ) : (
            <>
              <Button key="edit" onClick={startEditPassword}>
                修改密码
              </Button>
              <Button
                key="close"
                onClick={() => {
                  setPasswordModalVisible(false);
                  setIsEditingPassword(false);
                }}
              >
                关闭
              </Button>
            </>
          ),
        ]}
        width={500}
      >
        <div style={{ textAlign: 'center', padding: '20px 0' }}>
          <div style={{ marginBottom: '16px' }}>
            <Text strong>新密码：</Text>
          </div>
          {isEditingPassword ? (
            <Input.Password
              value={editablePassword}
              onChange={(e) => setEditablePassword(e.target.value)}
              placeholder="请输入新密码（至少6位）"
              style={{
                marginBottom: '16px',
                fontSize: '16px',
                fontFamily: 'monospace',
              }}
              autoFocus
            />
          ) : (
            <div
              style={{
                background: '#f5f5f5',
                padding: '12px 16px',
                borderRadius: '6px',
                fontSize: '18px',
                fontWeight: 'bold',
                color: '#1890ff',
                fontFamily: 'monospace',
                marginBottom: '16px',
                cursor: 'pointer',
              }}
              onClick={copyPassword}
            >
              {newPassword}
            </div>
          )}
          <div style={{ marginTop: '16px' }}>
            <Text type="secondary">
              {isEditingPassword
                ? '请输入新的密码，点击保存修改后生效'
                : '点击密码可复制，建议修改为您熟悉的密码'}
            </Text>
          </div>
        </div>
      </Modal>

      {/* 头像预览遮罩层 */}
      {avatarPreviewVisible && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.9)',
            zIndex: 1000,
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            cursor: 'pointer',
          }}
          onClick={() => setAvatarPreviewVisible(false)}
        >
          <img
            src={previewAvatarUrl}
            alt="头像预览"
            style={{
              maxWidth: '90vw',
              maxHeight: '90vh',
              objectFit: 'contain',
              cursor: 'default',
            }}
            onClick={(e) => e.stopPropagation()}
          />
        </div>
      )}

      {/* 导入模态框 */}
      <Modal
        title="导入用户"
        open={importModalVisible}
        onOk={handleImport}
        onCancel={handleImportCancel}
        confirmLoading={importing}
        width={600}
      >
        <div style={{ marginBottom: 16 }}>
          <Button type="link" icon={<DownloadOutlined />} onClick={handleDownloadTemplate}>
            下载导入模板
          </Button>
        </div>
        <div style={{ marginBottom: 16 }}>
          <Text type="secondary">
            请先下载模板，按照模板格式填写数据后再上传。支持 .xlsx 格式文件。
          </Text>
        </div>
        <Upload
          fileList={importFileList}
          onChange={({ fileList }) => setImportFileList(fileList)}
          beforeUpload={() => false}
          maxCount={1}
          accept=".xlsx,.xls"
        >
          <Button icon={<UploadOutlined />}>选择文件</Button>
        </Upload>
      </Modal>
    </div>
  );
};

export default UserManagement;
