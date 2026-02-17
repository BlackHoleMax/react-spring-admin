import React, { useState, useEffect, useCallback } from 'react';
import {
  Table,
  Button,
  Input,
  Select,
  Modal,
  Form,
  message,
  Popconfirm,
  Space,
  Tag,
  Radio,
  Switch,
} from 'antd';
import {
  PlusOutlined,
  EditOutlined,
  DeleteOutlined,
  EyeInvisibleOutlined,
  EyeTwoTone,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { storageConfigService } from '../../services/file';

interface ConfigRecord {
  id: number;
  configKey: string;
  endpoint: string;
  domain: string | null;
  bucketName: string;
  prefix: string | null;
  region: string | null;
  bucketAcl: string;
  accessKey: string;
  secretKey: string;
  status: number | null;
  isHttps: number;
  storageProvider: string;
  isDefault: number;
  remark: string;
  createBy: string | null;
  createTime: string;
  updateBy: string | null;
  updateTime: string;
}

interface SearchParams {
  bucketName?: string;
  isDefault?: number | undefined;
}

const FileConfigManagement: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<ConfigRecord[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<ConfigRecord | null>(null);
  const [form] = Form.useForm();
  const [searchParams, setSearchParams] = useState<SearchParams>({});

  const columns: ColumnsType<ConfigRecord> = [
    {
      title: '序号',
      key: 'index',
      width: 80,
      render: (_: any, __: any, index: number) => index + 1,
    },
    {
      title: '访问站点',
      dataIndex: 'endpoint',
      key: 'endpoint',
      width: 200,
      ellipsis: true,
    },
    {
      title: '自定义域名',
      dataIndex: 'domain',
      key: 'domain',
      width: 200,
      ellipsis: true,
    },
    {
      title: '桶名称',
      dataIndex: 'bucketName',
      key: 'bucketName',
      width: 120,
    },
    {
      title: '前缀',
      dataIndex: 'prefix',
      key: 'prefix',
      width: 100,
    },
    {
      title: '域',
      dataIndex: 'region',
      key: 'region',
      width: 100,
    },
    {
      title: '桶权限类型',
      dataIndex: 'bucketAcl',
      key: 'bucketAcl',
      width: 120,
      render: (acl: string) => {
        const aclMap: Record<string, { text: string; color: string }> = {
          private: { text: '私有', color: 'red' },
          'public-read': { text: 'public', color: 'green' },
          'public-read-write': { text: 'custom', color: 'blue' },
        };
        const config = aclMap[acl] || { text: acl, color: 'default' };
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '是否默认',
      dataIndex: 'isDefault',
      key: 'isDefault',
      width: 100,
      render: (isDefault: number, record: ConfigRecord) => (
        <Switch
          checked={isDefault === 1}
          onChange={(checked) => handleToggleDefault(record.id, checked)}
          disabled={record.isDefault === 1}
          checkedChildren={isDefault === 1 ? '开启' : '关闭'}
        />
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            修改
          </Button>
          {record.isDefault !== 1 && (
            <Popconfirm
              title="确定删除吗？"
              onConfirm={() => handleDelete(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" size="small" danger icon={<DeleteOutlined />}>
                删除
              </Button>
            </Popconfirm>
          )}
        </Space>
      ),
    },
  ];

  const loadConfigs = useCallback(async () => {
    setLoading(true);
    try {
      const response = await storageConfigService.getConfigPage(currentPage, pageSize);
      const data = response.data;
      if (data) {
        setDataSource(data.records || []);
        setTotal(data.total || 0);
      }
    } catch {
      message.error('加载配置列表失败');
    } finally {
      setLoading(false);
    }
  }, [currentPage, pageSize]);

  const handleSearch = () => {
    setCurrentPage(1);
  };

  const handleReset = () => {
    setSearchParams({});
    setCurrentPage(1);
  };

  const handleAdd = () => {
    setEditingRecord(null);
    form.resetFields();
    form.setFieldsValue({
      storageProvider: 'minio',
      bucketPermission: 'public-read',
      isHttps: 0,
      isDefault: 0,
      status: 1,
    });
    setModalVisible(true);
  };

  const handleEdit = (record: ConfigRecord) => {
    setEditingRecord(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await storageConfigService.deleteConfig(id);
      message.success('删除成功');
      loadConfigs();
    } catch {
      message.error('删除失败');
    }
  };

  const handleToggleDefault = async (id: number, checked: boolean) => {
    if (checked) {
      try {
        await storageConfigService.setDefaultConfig(id);
        message.success('设置默认配置成功');
        loadConfigs();
      } catch {
        message.error('设置默认配置失败');
      }
    }
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      if (editingRecord) {
        await storageConfigService.updateConfig({ ...values, id: editingRecord.id });
        message.success('更新成功');
      } else {
        await storageConfigService.saveConfig(values);
        message.success('新增成功');
      }
      setModalVisible(false);
      loadConfigs();
    } catch {
      message.error('保存失败');
    }
  };

  const handleModalCancel = () => {
    setModalVisible(false);
    form.resetFields();
  };

  useEffect(() => {
    loadConfigs();
  }, [loadConfigs]);

  return (
    <div style={{ padding: 24 }}>
      <div style={{ marginBottom: 16 }}>
        <Space>
          <Input
            placeholder="桶名称"
            style={{ width: 150 }}
            value={searchParams.bucketName}
            onChange={(e) => setSearchParams({ ...searchParams, bucketName: e.target.value })}
            onPressEnter={handleSearch}
          />
          <Select
            placeholder="是否默认"
            style={{ width: 120 }}
            value={searchParams.isDefault as number | null | undefined}
            onChange={(value) => setSearchParams({ ...searchParams, isDefault: value })}
            allowClear
          >
            <Select.Option value={1}>是</Select.Option>
            <Select.Option value={0}>否</Select.Option>
          </Select>
          <Button type="primary" onClick={handleSearch}>
            搜索
          </Button>
          <Button onClick={handleReset}>重置</Button>
        </Space>
      </div>

      <div style={{ marginBottom: 16 }}>
        <Space>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增
          </Button>
          <Button type="primary" icon={<EditOutlined />} disabled={true}>
            修改
          </Button>
          <Button danger icon={<DeleteOutlined />} disabled={true}>
            删除
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={dataSource}
        rowKey="id"
        loading={loading}
        pagination={{
          current: currentPage,
          pageSize,
          total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (totalCount) => `共 ${totalCount} 条`,
          onChange: (current, newPageSize) => {
            setCurrentPage(current);
            setPageSize(newPageSize || 10);
          },
        }}
        scroll={{ x: 1200 }}
      />

      <Modal
        title={
          <div style={{ textAlign: 'center', color: '#ff0000', fontWeight: 'bold', fontSize: 18 }}>
            文件服务配置编辑
          </div>
        }
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={handleModalCancel}
        width={600}
        closeIcon={<span style={{ fontSize: 20, cursor: 'pointer' }}>×</span>}
      >
        <div style={{ marginBottom: 16, color: '#999', fontSize: 14 }}>
          {editingRecord ? '修改对象存储配置' : '新增对象存储配置'}
        </div>
        <Form form={form} layout="vertical">
          <Form.Item
            name="configKey"
            label="配置key"
            rules={[{ required: true, message: '请输入配置key' }]}
          >
            <Input placeholder="请输入配置key" />
          </Form.Item>
          <Form.Item
            name="endpoint"
            label="访问站点"
            rules={[{ required: true, message: '请输入访问站点' }]}
          >
            <Input placeholder="请输入访问站点" />
          </Form.Item>
          <Form.Item name="domain" label="自定义域名">
            <Input placeholder="请输入自定义域名" />
          </Form.Item>
          <Form.Item
            name="accessKey"
            label="accessKey"
            rules={[{ required: true, message: '请输入accessKey' }]}
          >
            <Input placeholder="请输入accessKey" />
          </Form.Item>
          <Form.Item
            name="secretKey"
            label="secretKey"
            rules={[{ required: true, message: '请输入secretKey' }]}
          >
            <Input.Password
              placeholder="请输入secretKey"
              iconRender={(visible) => (visible ? <EyeTwoTone /> : <EyeInvisibleOutlined />)}
            />
          </Form.Item>
          <Form.Item
            name="bucketName"
            label="桶名称"
            rules={[{ required: true, message: '请输入桶名称' }]}
          >
            <Input placeholder="请输入桶名称" />
          </Form.Item>
          <Form.Item name="prefix" label="前缀">
            <Input placeholder="请输入前缀" />
          </Form.Item>
          <Form.Item
            name="isHttps"
            label="是否HTTPS"
            rules={[{ required: true, message: '请选择是否HTTPS' }]}
          >
            <Radio.Group>
              <Radio value={0}>否</Radio>
              <Radio value={1}>是</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item
            name="bucketAcl"
            label="桶权限类型"
            rules={[{ required: true, message: '请选择桶权限类型' }]}
          >
            <Radio.Group>
              <Radio value="private">private</Radio>
              <Radio value="public-read">public</Radio>
              <Radio value="public-read-write">custom</Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item name="region" label="域">
            <Input placeholder="请输入域" />
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} placeholder="请输入内容" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default FileConfigManagement;
