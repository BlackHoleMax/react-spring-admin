import React, { useCallback, useEffect, useState } from 'react';
import {
  App,
  Button,
  Card,
  Col,
  DatePicker,
  Form,
  Input,
  Modal,
  Popconfirm,
  Row,
  Select,
  Space,
  Table,
  Tag,
} from 'antd';
import Authorized from '@/components/Authorized';
import {
  CheckOutlined,
  CloseOutlined,
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  batchDeleteNotice,
  createNotice,
  deleteNotice,
  getNoticeList,
  type Notice,
  type NoticeQuery,
  publishNotice,
  revokeNotice,
  updateNotice,
} from '@/services/notice';
import { getRoleList } from '@/services/role';
import type { Role } from '@/types';
import dayjs from 'dayjs';

const { TextArea } = Input;

const NoticeManagement: React.FC = () => {
  const { message } = App.useApp();
  const [data, setData] = useState<Notice[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingNotice, setEditingNotice] = useState<Notice | null>(null);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [searchParams, setSearchParams] = useState<NoticeQuery>({});
  const [roles, setRoles] = useState<Role[]>([]);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  useEffect(() => {
    const fetchRoles = async () => {
      try {
        const result = await getRoleList({ current: 1, size: 100 });
        setRoles(result.records || []);
      } catch {
        message.error('获取角色列表失败');
      }
    };
    fetchRoles();
  }, [message]);

  const fetchData = useCallback(
    async (page = 1, size = 10, params: NoticeQuery = {}) => {
      setLoading(true);
      try {
        const result = await getNoticeList({ current: page, size, ...params });
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

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    const params: NoticeQuery = {};
    if (values.title) params.title = values.title;
    if (values.type) params.type = values.type;
    if (values.status) params.status = values.status;
    if (values.priority) params.priority = values.priority;
    setSearchParams(params);
  };

  const handleReset = () => {
    searchForm.resetFields();
    setSearchParams({});
  };

  const handleAdd = () => {
    setEditingNotice(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Notice) => {
    setEditingNotice(record);
    form.setFieldsValue({
      ...record,
      targetRoles: record.targetRoles ?? [],
      startTime: record.startTime ? dayjs(record.startTime) : null,
      endTime: record.endTime ? dayjs(record.endTime) : null,
    });
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteNotice(id);
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
      await batchDeleteNotice(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch {
      message.error('批量删除失败');
    }
  };

  const handlePublish = async (id: number) => {
    try {
      await publishNotice(id);
      message.success('发布成功');
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch (error: any) {
      message.error(error.message || '发布失败');
    }
  };

  const handleRevoke = async (id: number) => {
    try {
      await revokeNotice(id);
      message.success('撤回成功');
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch (error: any) {
      message.error(error.message || '撤回失败');
    }
  };

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      const noticeData: Partial<Notice> = {
        ...values,
        targetRoles: values.targetRoles || [],
        startTime: values.startTime ? values.startTime.format('YYYY-MM-DD HH:mm:ss') : undefined,
        endTime: values.endTime ? values.endTime.format('YYYY-MM-DD HH:mm:ss') : undefined,
      };
      if (editingNotice && editingNotice.id) {
        await updateNotice({ ...noticeData, id: editingNotice.id });
        message.success('修改成功');
      } else {
        await createNotice(noticeData);
        message.success('新增成功');
      }
      setModalVisible(false);
      await fetchData(pagination.current, pagination.pageSize, searchParams);
    } catch (error: any) {
      message.error(error.message || '操作失败');
    }
  };

  const columns = [
    {
      title: '标题',
      dataIndex: 'title',
      key: 'title',
      width: 200,
    },
    {
      title: '类型',
      dataIndex: 'typeName',
      key: 'type',
      width: 100,
      render: (text: string, record: Notice) => (
        <Tag color={record.type === 1 ? 'blue' : 'orange'}>{text}</Tag>
      ),
    },
    {
      title: '优先级',
      dataIndex: 'priorityName',
      key: 'priority',
      width: 100,
      render: (text: string, record: Notice) => {
        const colorMap: Record<number, string> = { 1: 'default', 2: 'warning', 3: 'error' };
        const priority = record.priority ?? 1;
        const color = colorMap[priority] ?? 'default';
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '状态',
      dataIndex: 'statusName',
      key: 'status',
      width: 100,
      render: (text: string, record: Notice) => {
        const colorMap: Record<number, string> = { 1: 'default', 2: 'success', 3: 'warning' };
        const status = record.status ?? 1;
        const color = colorMap[status] ?? 'default';
        return <Tag color={color}>{text}</Tag>;
      },
    },
    {
      title: '发布范围',
      dataIndex: 'targetTypeName',
      key: 'targetType',
      width: 100,
    },
    {
      title: '已读/总数',
      key: 'readInfo',
      width: 120,
      render: (_: any, record: Notice) => (
        <span>
          {record.readCount || 0} / {record.totalCount || 0}
        </span>
      ),
    },
    {
      title: '发布时间',
      dataIndex: 'publishTime',
      key: 'publishTime',
      width: 160,
      render: (text: string) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '发布者',
      dataIndex: 'publisherName',
      key: 'publisherName',
      width: 100,
    },
    {
      title: '操作',
      key: 'action',
      width: 220,
      fixed: 'right' as const,
      render: (_: any, record: Notice) => (
        <Space size="small">
          <Authorized permission="notice:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
              disabled={record.status === 2}
            >
              编辑
            </Button>
          </Authorized>
          {record.status === 1 && (
            <Authorized permission="notice:publish">
              <Button
                type="link"
                size="small"
                icon={<CheckOutlined />}
                onClick={() => handlePublish(record.id!)}
              >
                发布
              </Button>
            </Authorized>
          )}
          {record.status === 2 && (
            <Authorized permission="notice:revoke">
              <Button
                type="link"
                size="small"
                icon={<CloseOutlined />}
                onClick={() => handleRevoke(record.id!)}
              >
                撤回
              </Button>
            </Authorized>
          )}
          <Authorized permission="notice:delete">
            <Popconfirm
              title="确认删除此通知公告吗？"
              onConfirm={() => handleDelete(record.id!)}
              okText="确认"
              cancelText="取消"
            >
              <Button type="link" size="small" danger icon={<DeleteOutlined />}>
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
              <Form.Item name="title" label="标题" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入标题" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="type" label="类型" style={{ marginBottom: 0 }}>
                <Select placeholder="请选择类型" allowClear style={{ width: '100%' }}>
                  <Select.Option value={1}>系统公告</Select.Option>
                  <Select.Option value={2}>活动通知</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="status" label="状态" style={{ marginBottom: 0 }}>
                <Select placeholder="请选择状态" allowClear style={{ width: '100%' }}>
                  <Select.Option value={1}>草稿</Select.Option>
                  <Select.Option value={2}>已发布</Select.Option>
                  <Select.Option value={3}>已撤回</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="priority" label="优先级" style={{ marginBottom: 0 }}>
                <Select placeholder="请选择优先级" allowClear style={{ width: '100%' }}>
                  <Select.Option value={1}>普通</Select.Option>
                  <Select.Option value={2}>重要</Select.Option>
                  <Select.Option value={3}>紧急</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={6}>
              <Space>
                <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                  查询
                </Button>
                <Button icon={<ReloadOutlined />} onClick={handleReset}>
                  重置
                </Button>
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
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>通知公告管理</h2>
        <Space>
          <Button
            icon={<ReloadOutlined />}
            onClick={() => fetchData(pagination.current, pagination.pageSize, searchParams)}
          >
            刷新
          </Button>
          {selectedRowKeys.length > 0 && (
            <Authorized permission="notice:delete">
              <Popconfirm title="确认删除选中的通知公告吗？" onConfirm={handleBatchDelete}>
                <Button danger icon={<DeleteOutlined />}>
                  批量删除 ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            </Authorized>
          )}
          <Authorized permission="notice:add">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增通知
            </Button>
          </Authorized>
        </Space>
      </div>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, pageSize) => {
            fetchData(page, pageSize, searchParams);
          },
        }}
        rowSelection={{
          selectedRowKeys,
          onChange: setSelectedRowKeys,
        }}
        scroll={{ x: 1200 }}
      />

      <Modal
        title={editingNotice ? '修改通知公告' : '新增通知公告'}
        open={modalVisible}
        onOk={handleModalOk}
        onCancel={() => setModalVisible(false)}
        width={800}
        destroyOnHidden
      >
        <Form form={form} layout="vertical">
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="title"
                label="标题"
                rules={[{ required: true, message: '请输入标题' }]}
              >
                <Input placeholder="请输入标题" maxLength={200} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="type"
                label="类型"
                rules={[{ required: true, message: '请选择类型' }]}
              >
                <Select placeholder="请选择类型">
                  <Select.Option value={1}>系统公告</Select.Option>
                  <Select.Option value={2}>活动通知</Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="targetType"
                label="发布范围"
                rules={[{ required: true, message: '请选择发布范围' }]}
              >
                <Select placeholder="请选择发布范围">
                  <Select.Option value={1}>全部用户</Select.Option>
                  <Select.Option value={2}>指定角色</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="priority"
                label="优先级"
                rules={[{ required: true, message: '请选择优先级' }]}
              >
                <Select placeholder="请选择优先级">
                  <Select.Option value={1}>普通</Select.Option>
                  <Select.Option value={2}>重要</Select.Option>
                  <Select.Option value={3}>紧急</Select.Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            noStyle
            shouldUpdate={(prevValues, currentValues) =>
              prevValues.targetType !== currentValues.targetType
            }
          >
            {({ getFieldValue }) => {
              const targetType = getFieldValue('targetType');
              return (
                <Form.Item
                  name="targetRoles"
                  label="选择角色"
                  rules={[{ required: targetType === 2, message: '请选择角色' }]}
                >
                  <Select
                    mode="multiple"
                    placeholder="请选择角色"
                    disabled={!targetType || targetType === 1}
                    options={roles.map((role) => ({
                      label: role.name,
                      value: role.id,
                    }))}
                  />
                </Form.Item>
              );
            }}
          </Form.Item>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name="startTime" label="生效开始时间">
                <DatePicker showTime style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="endTime" label="生效结束时间">
                <DatePicker showTime style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            name="content"
            label="公告内容"
            rules={[{ required: true, message: '请输入公告内容' }]}
          >
            <TextArea rows={6} placeholder="请输入公告内容" maxLength={5000} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default NoticeManagement;
