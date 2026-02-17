import React, { useCallback, useEffect, useState } from 'react';
import { App, Button, Card, Col, Form, Input, Modal, Popconfirm, Row, Space, Table } from 'antd';
import Authorized from '@/components/Authorized';
import {
  DeleteOutlined,
  EditOutlined,
  ExportOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  batchDeletePermission,
  createPermission,
  deletePermission,
  getPermissionList,
  updatePermission,
} from '@/services/permission.ts';
import type { Permission, PermissionQuery } from '@/types';

const PermissionPage: React.FC = () => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Permission[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingPerm, setEditingPerm] = useState<Permission | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [searchParams, setSearchParams] = useState<PermissionQuery>({});
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  const fetchData = useCallback(
    async (params = searchParams) => {
      setLoading(true);
      try {
        const result = await getPermissionList(params);
        setDataSource(result.records);
        setTotal(result.total);
      } catch (_error) {
        console.error('获取权限列表失败:', _error);
      } finally {
        setLoading(false);
      }
    },
    [searchParams]
  );

  useEffect(() => {
    fetchData(searchParams);
  }, [fetchData, searchParams]);

  const handleSearch = useCallback(() => {
    const values = searchForm.getFieldsValue();
    setSearchParams(values);
  }, [searchForm]);

  const handleReset = useCallback(() => {
    searchForm.resetFields();
    setSearchParams({});
  }, [searchForm]);

  const handleAdd = useCallback(() => {
    setEditingPerm(null);
    form.resetFields();
    setModalVisible(true);
  }, [form]);

  const handleEdit = useCallback(
    (record: Permission) => {
      setEditingPerm(record);
      form.setFieldsValue(record);
      setModalVisible(true);
    },
    [form]
  );

  const handleDelete = useCallback(
    async (id: number) => {
      try {
        await deletePermission(id);
        message.success('删除成功');
        await fetchData(searchParams);
      } catch {
        console.error('操作失败');
      }
    },
    [message, fetchData, searchParams]
  );

  const handleBatchDelete = useCallback(async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    try {
      await batchDeletePermission(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData(searchParams);
    } catch {
      message.error('批量删除失败');
    }
  }, [selectedRowKeys, message, fetchData, searchParams]);

  const handleExport = useCallback(async () => {
    try {
      const response = await fetch('/api/system/permission/export', {
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
        a.download = `权限数据_${new Date().toLocaleDateString()}.xlsx`;
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

  const handleOk = useCallback(async () => {
    try {
      const values = await form.validateFields();
      if (editingPerm) {
        await updatePermission({ ...values, id: editingPerm.id });
        message.success('更新成功');
      } else {
        await createPermission(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      await fetchData(searchParams);
    } catch {
      console.error('操作失败');
    }
  }, [form, editingPerm, message, fetchData, searchParams]);

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '权限名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '权限标识',
      dataIndex: 'perm',
      key: 'perm',
    },
    {
      title: '关联菜单ID',
      dataIndex: 'menuId',
      key: 'menuId',
    },
    {
      title: '关联菜单名称',
      dataIndex: 'menuName',
      key: 'menuName',
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: unknown, record: Permission) => (
        <Space>
          <Authorized permission="permission:edit">
            <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
              编辑
            </Button>
          </Authorized>
          <Authorized permission="permission:delete">
            <Popconfirm title="确定要删除吗？" onConfirm={() => handleDelete(record.id)}>
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
              <Form.Item name="name" label="权限名称" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入权限名称" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="perm" label="权限标识" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入权限标识" allowClear />
              </Form.Item>
            </Col>
            <Col span={12}>
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
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>权限管理</h2>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={() => fetchData(searchParams)}>
            刷新
          </Button>
          <Authorized permission="permission:export">
            <Button icon={<ExportOutlined />} onClick={handleExport}>
              导出
            </Button>
          </Authorized>
          {selectedRowKeys.length > 0 && (
            <Authorized permission="permission:delete">
              <Popconfirm title="确定删除选中的数据?" onConfirm={handleBatchDelete}>
                <Button danger icon={<DeleteOutlined />}>
                  批量删除 ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            </Authorized>
          )}
          <Authorized permission="permission:add">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增权限
            </Button>
          </Authorized>
        </Space>
      </div>

      <Table
        loading={loading}
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        rowSelection={{
          selectedRowKeys,
          onChange: setSelectedRowKeys,
        }}
        pagination={{
          current,
          pageSize,
          total,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, size) => {
            setCurrent(page);
            setPageSize(size);
          },
        }}
      />

      <Modal
        title={editingPerm ? '编辑权限' : '新增权限'}
        open={modalVisible}
        onOk={handleOk}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="权限名称"
            name="name"
            rules={[{ required: true, message: '请输入权限名称' }]}
          >
            <Input placeholder="请输入权限名称" />
          </Form.Item>

          <Form.Item
            label="权限标识"
            name="perm"
            rules={[{ required: true, message: '请输入权限标识' }]}
          >
            <Input placeholder="请输入权限标识，如：user:list" />
          </Form.Item>

          <Form.Item label="关联菜单ID" name="menuId">
            <Input type="number" placeholder="请输入关联菜单ID" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default PermissionPage;
