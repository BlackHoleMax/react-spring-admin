import React, { useCallback, useEffect, useState } from 'react';
import {
  App,
  Button,
  Card,
  Col,
  Form,
  Input,
  Popconfirm,
  Row,
  Space,
  Table,
  Tag,
  Typography,
} from 'antd';
import Authorized from '@/components/Authorized';
import { ClearOutlined, DeleteOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import { onlineUserApi, type UserOnline } from '@/services/online';
import type { ColumnsType } from 'antd/es/table';

const { Title } = Typography;

const OnlineUserManagement: React.FC = () => {
  const { message: messageApi } = App.useApp();
  const [data, setData] = useState<UserOnline[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [size, setSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);
  const [onlineCount, setOnlineCount] = useState(0);
  const [searchForm] = Form.useForm();

  const fetchData = useCallback(
    async (params: { current: number; size: number; username?: string; ip?: string }) => {
      setLoading(true);
      try {
        const response = await onlineUserApi.page(params);
        setData(response.records || []);
        setTotal(response.total || 0);
        setCurrent(response.current || 1);
        setSize(response.size || 10);
      } catch (error) {
        console.error('获取在线用户列表失败:', error);
        const errorMessage = error instanceof Error ? error.message : '未知错误';
        messageApi.error(`获取数据失败: ${errorMessage}`);
      } finally {
        setLoading(false);
      }
    },
    [messageApi]
  );

  const fetchOnlineCount = useCallback(async () => {
    try {
      const response = await onlineUserApi.count();
      setOnlineCount(response || 0);
    } catch {
      console.error('操作失败');
    }
  }, []);

  const handleRefresh = useCallback(() => {
    const values = searchForm.getFieldsValue();
    fetchData({ current, size, ...values });
    fetchOnlineCount();
  }, [current, size, fetchData, fetchOnlineCount, searchForm]);

  const handleSearch = useCallback(() => {
    const values = searchForm.getFieldsValue();
    setCurrent(1);
    fetchData({ current: 1, size, ...values });
  }, [fetchData, size, searchForm]);

  const handleReset = useCallback(() => {
    searchForm.resetFields();
    setCurrent(1);
    fetchData({ current: 1, size });
  }, [fetchData, size, searchForm]);

  const handleKickout = useCallback(
    async (sessionId: string) => {
      try {
        await onlineUserApi.kickout(sessionId);
        messageApi.success('踢出成功');
        handleRefresh();
      } catch {
        messageApi.error('踢出失败');
      }
    },
    [messageApi, handleRefresh]
  );

  const handleBatchKickout = useCallback(async () => {
    if (selectedRowKeys.length === 0) {
      messageApi.warning('请选择要踢出的会话');
      return;
    }

    try {
      await onlineUserApi.batchKickout(selectedRowKeys);
      messageApi.success('批量踢出成功');
      setSelectedRowKeys([]);
      handleRefresh();
    } catch {
      messageApi.error('批量踢出失败');
    }
  }, [selectedRowKeys, messageApi, handleRefresh]);

  const handleCleanExpired = useCallback(async () => {
    try {
      const response = await onlineUserApi.cleanExpiredSessions();
      messageApi.success(`清理完成，共清理 ${response} 个过期会话`);
      handleRefresh();
    } catch {
      messageApi.error('清理失败');
    }
  }, [messageApi, handleRefresh]);

  const handleTableChange = useCallback(
    (pagination: any) => {
      const values = searchForm.getFieldsValue();
      fetchData({
        current: pagination.current || 1,
        size: pagination.pageSize || 10,
        ...values,
      });
    },
    [fetchData, searchForm]
  );

  const formatOnlineTime = (minutes: number) => {
    if (minutes < 60) {
      return `${minutes}分钟`;
    } else if (minutes < 1440) {
      return `${Math.floor(minutes / 60)}小时${minutes % 60}分钟`;
    } else {
      const days = Math.floor(minutes / 1440);
      const hours = Math.floor((minutes % 1440) / 60);
      const mins = minutes % 60;
      return `${days}天${hours}小时${mins}分钟`;
    }
  };

  const columns: ColumnsType<UserOnline> = [
    {
      title: '会话ID',
      dataIndex: 'id',
      key: 'id',
      width: 200,
      ellipsis: true,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 120,
    },
    {
      title: '显示名称',
      dataIndex: 'nickname',
      key: 'nickname',
      width: 120,
    },
    {
      title: '登录IP',
      dataIndex: 'ip',
      key: 'ip',
      width: 120,
    },
    {
      title: '登录地点',
      dataIndex: 'location',
      key: 'location',
      width: 120,
    },
    {
      title: '浏览器',
      dataIndex: 'browser',
      key: 'browser',
      width: 100,
    },
    {
      title: '操作系统',
      dataIndex: 'os',
      key: 'os',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string) => (
        <Tag color={status === 'online' ? 'green' : 'red'}>
          {status === 'online' ? '在线' : '离线'}
        </Tag>
      ),
    },
    {
      title: '登录时间',
      dataIndex: 'startTime',
      key: 'startTime',
      width: 180,
    },
    {
      title: '最后访问',
      dataIndex: 'lastTime',
      key: 'lastTime',
      width: 180,
    },
    {
      title: '在线时长',
      dataIndex: 'onlineMinutes',
      key: 'onlineMinutes',
      width: 100,
      render: (minutes: number) => formatOnlineTime(minutes),
    },
    {
      title: '操作',
      key: 'action',
      width: 100,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Authorized permission="online:kickout">
            <Popconfirm
              title="确定要踢出该用户吗？"
              onConfirm={() => handleKickout(record.id)}
              okText="确定"
              cancelText="取消"
            >
              <Button type="link" size="small" danger icon={<DeleteOutlined />}>
                踢出
              </Button>
            </Popconfirm>
          </Authorized>
        </Space>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: React.Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys as string[]);
    },
  };

  useEffect(() => {
    fetchData({ current, size });
    fetchOnlineCount();
  }, [current, size, fetchData, fetchOnlineCount]);

  return (
    <div style={{ padding: 24 }}>
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <Card>
            <Row justify="space-between" align="middle">
              <Col>
                <Title level={4} style={{ margin: 0 }}>
                  在线用户管理
                </Title>
              </Col>
              <Col>
                <Space>
                  <Tag color="blue">当前在线：{onlineCount} 人</Tag>
                </Space>
              </Col>
            </Row>
          </Card>
        </Col>

        <Col span={24}>
          <Card>
            <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
              <Form.Item name="username" label="用户名">
                <Input placeholder="请输入用户名" allowClear />
              </Form.Item>
              <Form.Item name="ip" label="IP地址">
                <Input placeholder="请输入IP地址" allowClear />
              </Form.Item>
              <Form.Item>
                <Space>
                  <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                    搜索
                  </Button>
                  <Button icon={<ReloadOutlined />} onClick={handleReset}>
                    重置
                  </Button>
                </Space>
              </Form.Item>
            </Form>

            <Row style={{ marginBottom: 16 }}>
              <Col>
                <Space>
                  <Authorized permission="online:kickout">
                    <Button
                      type="primary"
                      danger
                      icon={<DeleteOutlined />}
                      onClick={handleBatchKickout}
                      disabled={selectedRowKeys.length === 0}
                    >
                      批量踢出
                    </Button>
                  </Authorized>
                  <Authorized permission="online:kickout">
                    <Button icon={<ClearOutlined />} onClick={handleCleanExpired}>
                      清理过期会话
                    </Button>
                  </Authorized>
                  <Button icon={<ReloadOutlined />} onClick={handleRefresh}>
                    刷新
                  </Button>
                </Space>
              </Col>
            </Row>

            <Table
              columns={columns}
              dataSource={data}
              rowKey="id"
              loading={loading}
              pagination={{
                current,
                pageSize: size,
                total,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`,
              }}
              rowSelection={rowSelection}
              scroll={{ x: 1500 }}
              onChange={handleTableChange}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default OnlineUserManagement;
