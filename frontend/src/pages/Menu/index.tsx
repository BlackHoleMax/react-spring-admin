import React, { useCallback, useEffect, useState } from 'react';
import {
  App,
  Button,
  Card,
  Col,
  Form,
  Input,
  InputNumber,
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
import Authorized from '@/components/Authorized';
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  ExportOutlined,
  ImportOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  UploadOutlined,
} from '@ant-design/icons';
import {
  batchDeleteMenu,
  createMenu,
  deleteMenu,
  getMenuTree,
  updateMenu,
  importMenu,
  downloadMenuTemplate,
} from '@/services/menu.ts';
import type { Menu } from '@/types';

const MenuManagement: React.FC = () => {
  const { message } = App.useApp();
  const [data, setData] = useState<Menu[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [importModalVisible, setImportModalVisible] = useState(false);
  const [importFileList, setImportFileList] = useState<any[]>([]);
  const [importing, setImporting] = useState(false);
  const [editingMenu, setEditingMenu] = useState<Menu | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [searchParams, setSearchParams] = useState<{ name?: string; status?: number }>({});
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();
  const { Text } = Typography;

  const fetchData = useCallback(
    async (params: { name?: string; status?: number } = {}) => {
      setLoading(true);
      try {
        let filteredData = await getMenuTree();

        if (params.name) {
          const searchName = params.name;
          filteredData = filteredData.filter((item: Menu) => item.name.includes(searchName));
        }

        if (params.status !== undefined && params.status !== null) {
          filteredData = filteredData.filter((item: Menu) => item.status === params.status);
        }

        setData(filteredData);
      } catch {
        message.error('获取数据失败');
      } finally {
        setLoading(false);
      }
    },
    [message]
  );

  useEffect(() => {
    void fetchData(searchParams);
  }, [fetchData, searchParams]);

  const handleSearch = async () => {
    const values = searchForm.getFieldsValue();
    setSearchParams(values);
    await fetchData(values);
  };

  const handleReset = async () => {
    searchForm.resetFields();
    setSearchParams({});
    await fetchData({});
  };

  const handleAdd = () => {
    setEditingMenu(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Menu) => {
    setEditingMenu(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteMenu(id);
      message.success('删除成功');
      await fetchData(searchParams);
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
      await batchDeleteMenu(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData(searchParams);
    } catch {
      message.error('批量删除失败');
    }
  };

  const handleExport = async () => {
    try {
      const response = await fetch('/api/system/menu/export', {
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
        a.download = `菜单数据_${new Date().toLocaleDateString()}.xlsx`;
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
      const blob = await downloadMenuTemplate();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = '菜单导入模板.xlsx';
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
      const result = await importMenu(importFileList[0].originFileObj);
      message.success(result || '导入成功');
      setImportModalVisible(false);
      setImportFileList([]);
      await fetchData(searchParams);
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

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingMenu) {
        await updateMenu({ ...values, id: editingMenu.id });
        message.success('更新成功');
      } else {
        await createMenu(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      await fetchData(searchParams);
    } catch {
      message.error('操作失败');
    }
  };

  const columns = [
    { title: '菜单名称', dataIndex: 'name', key: 'name' },
    { title: '路由路径', dataIndex: 'path', key: 'path' },
    { title: '组件路径', dataIndex: 'component', key: 'component' },
    { title: '图标', dataIndex: 'icon', key: 'icon' },
    { title: '排序', dataIndex: 'sort', key: 'sort', width: 80 },
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
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: unknown, record: Menu) => (
        <Space>
          <Authorized permission="menu:edit">
            <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
              编辑
            </Button>
          </Authorized>
          <Authorized permission="menu:delete">
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
              <Form.Item name="name" label="菜单名称" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入菜单名称" allowClear />
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
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>菜单管理</h2>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={() => fetchData(searchParams)}>
            刷新
          </Button>
          <Authorized permission="menu:import">
            <Button icon={<ImportOutlined />} onClick={() => setImportModalVisible(true)}>
              导入
            </Button>
          </Authorized>
          <Authorized permission="menu:export">
            <Button icon={<ExportOutlined />} onClick={handleExport}>
              导出
            </Button>
          </Authorized>
          {selectedRowKeys.length > 0 && (
            <Authorized permission="menu:delete">
              <Popconfirm title="确定删除选中的数据?" onConfirm={handleBatchDelete}>
                <Button danger icon={<DeleteOutlined />}>
                  批量删除 ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            </Authorized>
          )}
          <Authorized permission="menu:add">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增菜单
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
        pagination={false}
      />

      <Modal
        title={editingMenu ? '编辑菜单' : '新增菜单'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item name="parentId" label="父菜单ID" initialValue={0}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="name" label="菜单名称" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="path" label="路由路径" rules={[{ required: true }]}>
            <Input />
          </Form.Item>
          <Form.Item name="component" label="组件路径">
            <Input />
          </Form.Item>
          <Form.Item name="redirect" label="重定向地址">
            <Input />
          </Form.Item>
          <Form.Item name="icon" label="图标">
            <Input />
          </Form.Item>
          <Form.Item name="sort" label="排序" initialValue={0}>
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="hidden" label="是否隐藏" initialValue={0}>
            <Select>
              <Select.Option value={0}>显示</Select.Option>
              <Select.Option value={1}>隐藏</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="状态" rules={[{ required: true }]} initialValue={1}>
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      {/* 导入模态框 */}
      <Modal
        title="导入菜单"
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

export default MenuManagement;
