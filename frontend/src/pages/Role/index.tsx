import React, { useCallback, useEffect, useState } from 'react';
import {
  App,
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
  Tree,
  Typography,
  Upload,
} from 'antd';
import Authorized from '@/components/Authorized';
import {
  AppstoreOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  ExportOutlined,
  ImportOutlined,
  PlusOutlined,
  ReloadOutlined,
  SafetyOutlined,
  SearchOutlined,
  UploadOutlined,
} from '@ant-design/icons';
import {
  batchDeleteRole,
  createRole,
  deleteRole,
  getRoleList,
  getRoleMenus,
  getRolePerms,
  saveRoleMenus,
  saveRolePerms,
  updateRole,
  importRole,
  downloadRoleTemplate,
} from '@/services/role.ts';
import { getPermissionTree } from '@/services/permission.ts';
import { getMenuTree } from '@/services/menu.ts';
import type { Role, RoleQuery } from '@/types';
import type { DataNode } from 'antd/es/tree';
import dayjs from 'dayjs';

const RoleManagement: React.FC = () => {
  const { message } = App.useApp();
  const [data, setData] = useState<Role[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [permModalVisible, setPermModalVisible] = useState(false);
  const [menuModalVisible, setMenuModalVisible] = useState(false);
  const [importModalVisible, setImportModalVisible] = useState(false);
  const [importFileList, setImportFileList] = useState<any[]>([]);
  const [importing, setImporting] = useState(false);
  const [editingRole, setEditingRole] = useState<Role | null>(null);
  const [currentRole, setCurrentRole] = useState<Role | null>(null);
  const [permTree, setPermTree] = useState<DataNode[]>([]);
  const [menuTree, setMenuTree] = useState<DataNode[]>([]);
  const [checkedKeys, setCheckedKeys] = useState<React.Key[]>([]);
  const [checkedMenuKeys, setCheckedMenuKeys] = useState<React.Key[]>([]);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const [searchParams, setSearchParams] = useState<RoleQuery>({});
  const { Text } = Typography;

  const fetchData = useCallback(
    async (page = 1, size = 10, params = {}) => {
      setLoading(true);
      try {
        const result = await getRoleList({ current: page, size, ...params });
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
    fetchData();
  }, [fetchData]);

  const handleSearch = useCallback(() => {
    const values = searchForm.getFieldsValue();
    setSearchParams(values);
    fetchData(1, pagination.pageSize, values);
  }, [searchForm, pagination.pageSize, fetchData]);

  const handleReset = useCallback(() => {
    searchForm.resetFields();
    setSearchParams({});
    fetchData(1, pagination.pageSize, {});
  }, [searchForm, pagination.pageSize, fetchData]);

  const handleAdd = useCallback(() => {
    setEditingRole(null);
    form.resetFields();
    setModalVisible(true);
  }, [form]);

  const handleEdit = useCallback(
    (record: Role) => {
      setEditingRole(record);
      form.setFieldsValue(record);
      setModalVisible(true);
    },
    [form]
  );

  const handleDelete = useCallback(
    async (id: number) => {
      try {
        await deleteRole(id);
        message.success('删除成功');
        await fetchData(pagination.current, pagination.pageSize, searchParams);
      } catch {
        message.error('删除失败');
      }
    },
    [message, fetchData, pagination, searchParams]
  );

  const handleBatchDelete = useCallback(async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    try {
      await batchDeleteRole(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch {
      message.error('批量删除失败');
    }
  }, [selectedRowKeys, message, fetchData, pagination, searchParams]);

  const handleExport = useCallback(async () => {
    try {
      const response = await fetch('/api/system/role/export', {
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
        a.download = `角色数据_${new Date().toLocaleDateString()}.xlsx`;
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
  }, [selectedRowKeys, message]);

  const handleDownloadTemplate = async () => {
    try {
      const blob = await downloadRoleTemplate();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = '角色导入模板.xlsx';
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
      const result = await importRole(importFileList[0].originFileObj);
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

  const handleAssignPerms = useCallback(
    async (record: Role) => {
      setCurrentRole(record);
      try {
        const [tree, perms] = await Promise.all([getPermissionTree(), getRolePerms(record.id)]);
        setPermTree(tree as unknown as DataNode[]);
        setCheckedKeys(perms);
        setPermModalVisible(true);
      } catch {
        message.error('获取权限数据失败');
      }
    },
    [message]
  );

  const handleAssignMenus = useCallback(
    async (record: Role) => {
      setCurrentRole(record);
      try {
        const [tree, menus] = await Promise.all([getMenuTree(), getRoleMenus(record.id)]);
        setMenuTree(tree as unknown as DataNode[]);
        setCheckedMenuKeys(menus);
        setMenuModalVisible(true);
      } catch {
        message.error('获取菜单数据失败');
      }
    },
    [message]
  );

  const handleSubmit = useCallback(async () => {
    try {
      const values = await form.validateFields();
      if (editingRole) {
        await updateRole({ ...values, id: editingRole.id });
        message.success('更新成功');
      } else {
        await createRole(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch {
      message.error('操作失败');
    }
  }, [form, editingRole, message, fetchData, pagination, searchParams]);

  const handleSavePerms = useCallback(async () => {
    if (!currentRole) return;
    try {
      await saveRolePerms(currentRole.id, checkedKeys as number[]);
      message.success('权限分配成功');
      setPermModalVisible(false);
    } catch {
      message.error('权限分配失败');
    }
  }, [currentRole, checkedKeys, message]);

  const handleSaveMenus = useCallback(async () => {
    if (!currentRole) return;
    try {
      await saveRoleMenus(currentRole.id, checkedMenuKeys as number[]);
      message.success('菜单分配成功');
      setMenuModalVisible(false);
    } catch {
      message.error('菜单分配失败');
    }
  }, [currentRole, checkedMenuKeys, message]);

  const columns = [
    { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
    { title: '角色名称', dataIndex: 'name', key: 'name' },
    { title: '角色编码', dataIndex: 'code', key: 'code' },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 100 },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
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
      width: 370,
      render: (_: unknown, record: Role) => (
        <Space>
          <Authorized permission="role:edit">
            <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
              编辑
            </Button>
          </Authorized>
          <Authorized permission="role:edit">
            <Button type="link" icon={<SafetyOutlined />} onClick={() => handleAssignPerms(record)}>
              分配权限
            </Button>
          </Authorized>
          <Authorized permission="role:edit">
            <Button
              type="link"
              icon={<AppstoreOutlined />}
              onClick={() => handleAssignMenus(record)}
            >
              分配菜单
            </Button>
          </Authorized>
          <Authorized permission="role:delete">
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
              <Form.Item name="name" label="角色名称" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入角色名称" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="code" label="角色编码" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入角色编码" allowClear />
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
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>角色管理</h2>
        <Space>
          <Button
            icon={<ReloadOutlined />}
            onClick={() => fetchData(pagination.current, pagination.pageSize, searchParams)}
          >
            刷新
          </Button>
          <Authorized permission="role:import">
            <Button icon={<ImportOutlined />} onClick={() => setImportModalVisible(true)}>
              导入
            </Button>
          </Authorized>
          <Authorized permission="role:export">
            <Button icon={<ExportOutlined />} onClick={handleExport}>
              导出
            </Button>
          </Authorized>
          {selectedRowKeys.length > 0 && (
            <Authorized permission="role:delete">
              <Popconfirm title="确定删除选中的数据?" onConfirm={handleBatchDelete}>
                <Button danger icon={<DeleteOutlined />}>
                  批量删除 ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            </Authorized>
          )}
          <Authorized permission="role:add">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增角色
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
        title={editingRole ? '编辑角色' : '新增角色'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="name" label="角色名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="code" label="角色编码" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="sort" label="排序">
            <Input type="number" />
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true }]}>
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="分配权限"
        open={permModalVisible}
        onOk={handleSavePerms}
        onCancel={() => setPermModalVisible(false)}
        width={600}
        styles={{ body: { maxHeight: '60vh', overflowY: 'auto', padding: '16px' } }}
      >
        <Tree
          checkable
          checkedKeys={checkedKeys}
          onCheck={(keys) => setCheckedKeys(Array.isArray(keys) ? keys : keys.checked)}
          treeData={permTree}
          fieldNames={{ title: 'name', key: 'id', children: 'children' }}
          height={400}
        />
      </Modal>

      <Modal
        title="分配菜单"
        open={menuModalVisible}
        onOk={handleSaveMenus}
        onCancel={() => setMenuModalVisible(false)}
        width={600}
        styles={{ body: { maxHeight: '60vh', overflowY: 'auto', padding: '16px' } }}
      >
        <Tree
          checkable
          checkedKeys={checkedMenuKeys}
          onCheck={(keys) => setCheckedMenuKeys(Array.isArray(keys) ? keys : keys.checked)}
          treeData={menuTree}
          fieldNames={{ title: 'name', key: 'id', children: 'children' }}
          height={400}
        />
      </Modal>

      {/* 导入模态框 */}
      <Modal
        title="导入角色"
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

export default RoleManagement;
