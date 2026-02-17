import React, { useCallback, useEffect, useState } from 'react';
import {
  App,
  Badge,
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
  Tag,
} from 'antd';
import { CheckOutlined, EyeOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import {
  batchMarkAsRead,
  getMyNoticeList,
  getUnreadCount,
  markAllAsRead,
  markAsRead,
  type MyNoticeQuery,
  type Notice,
} from '@/services/notice';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import dayjs from 'dayjs';

const MyNotice: React.FC = () => {
  const { message } = App.useApp();
  const dispatch = useAppDispatch();
  const themeMode = useAppSelector((state) => state.theme.mode);
  const [data, setData] = useState<Notice[]>([]);
  const [loading, setLoading] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [searchParams, setSearchParams] = useState<MyNoticeQuery>({});
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentNotice, setCurrentNotice] = useState<Notice | null>(null);
  const [searchForm] = Form.useForm();

  const fetchData = useCallback(
    async (params: MyNoticeQuery = {}) => {
      setLoading(true);
      try {
        const result = await getMyNoticeList({ current: 1, size: 100, ...params });
        setData(result.records);
      } catch {
        message.error('获取数据失败');
      } finally {
        setLoading(false);
      }
    },
    [message]
  );

  const fetchUnreadCount = useCallback(async () => {
    try {
      const count = await getUnreadCount();
      setUnreadCount(count);
      dispatch({ type: 'session/setUnreadCount', payload: count });
    } catch {
      message.error('获取未读数量失败');
    }
  }, [message, dispatch]);

  useEffect(() => {
    fetchData(searchParams);
    fetchUnreadCount();
  }, [fetchData, fetchUnreadCount, searchParams]);

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    const params: MyNoticeQuery = {};
    if (values.title) params.title = values.title;
    if (values.type) params.type = values.type;
    if (values.readStatus !== undefined) params.readStatus = values.readStatus;
    if (values.priority) params.priority = values.priority;
    setSearchParams(params);
  };

  const handleReset = () => {
    searchForm.resetFields();
    setSearchParams({});
  };

  const handleView = async (record: Notice) => {
    setCurrentNotice(record);
    setDetailVisible(true);
    if (!record.readStatus) {
      try {
        await markAsRead(record.id!);
        await fetchData(searchParams);
        await fetchUnreadCount();
      } catch {
        message.error('标记已读失败');
      }
    }
  };

  const handleBatchMarkAsRead = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要标记的数据');
      return;
    }
    try {
      await batchMarkAsRead(selectedRowKeys as number[]);
      message.success('批量标记成功');
      setSelectedRowKeys([]);
      await fetchData(searchParams);
      await fetchUnreadCount();
    } catch {
      message.error('批量标记失败');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead();
      message.success('全部标记成功');
      await fetchData(searchParams);
      await fetchUnreadCount();
    } catch {
      message.error('全部标记失败');
    }
  };

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
              <Form.Item name="readStatus" label="状态" style={{ marginBottom: 0 }}>
                <Select placeholder="请选择状态" allowClear style={{ width: '100%' }}>
                  <Select.Option value={0}>未读</Select.Option>
                  <Select.Option value={1}>已读</Select.Option>
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
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>
          <Space>
            <span>我的通知</span>
            <Badge count={unreadCount} overflowCount={99} />
          </Space>
        </h2>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={() => fetchData(searchParams)}>
            刷新
          </Button>
          <Popconfirm
            title="确认将选中的通知标记为已读吗？"
            onConfirm={handleBatchMarkAsRead}
            disabled={selectedRowKeys.length === 0}
            okText="确认"
            cancelText="取消"
          >
            <Button type="primary" icon={<CheckOutlined />} disabled={selectedRowKeys.length === 0}>
              批量已读
            </Button>
          </Popconfirm>
          <Button icon={<CheckOutlined />} onClick={handleMarkAllAsRead}>
            全部已读
          </Button>
        </Space>
      </div>
      <Card>
        {loading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>加载中...</div>
        ) : data.length === 0 ? (
          <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>暂无数据</div>
        ) : (
          <div>
            {data.map((item) => (
              <div
                key={item.id}
                style={{
                  padding: '16px 24px',
                  background:
                    themeMode === 'dark'
                      ? item.readStatus
                        ? '#262626'
                        : '#1f1f1f'
                      : item.readStatus
                        ? '#fafafa'
                        : '#fff',
                  border: item.readStatus
                    ? 'none'
                    : themeMode === 'dark'
                      ? '1px solid #424242'
                      : '1px solid #d9d9d9',
                  marginBottom: 8,
                  borderRadius: 4,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                }}
              >
                <div style={{ display: 'flex', alignItems: 'flex-start', flex: 1 }}>
                  <div style={{ marginRight: 16 }}>
                    <Badge dot={!item.readStatus}>
                      <div
                        style={{
                          width: 40,
                          height: 40,
                          borderRadius: '50%',
                          background: item.type === 1 ? '#1890ff' : '#fa8c16',
                          display: 'flex',
                          alignItems: 'center',
                          justifyContent: 'center',
                          color: '#fff',
                        }}
                      >
                        {item.type === 1 ? '系' : '活'}
                      </div>
                    </Badge>
                  </div>
                  <div style={{ flex: 1 }}>
                    <Space style={{ marginBottom: 4 }}>
                      <span style={{ fontWeight: !item.readStatus ? 'bold' : 'normal' }}>
                        {item.title}
                      </span>
                      {item.priority === 3 && <Tag color="error">紧急</Tag>}
                      {item.priority === 2 && <Tag color="warning">重要</Tag>}
                      {!item.readStatus && <Tag color="blue">未读</Tag>}
                    </Space>
                    <Space orientation="vertical" size={0}>
                      <span style={{ fontSize: 12, color: '#999' }}>
                        {item.publisherName} ·{' '}
                        {dayjs(item.publishTime).format('YYYY-MM-DD HH:mm:ss')}
                      </span>
                      <div
                        style={{
                          fontSize: 14,
                          color: '#666',
                          overflow: 'hidden',
                          textOverflow: 'ellipsis',
                          whiteSpace: 'nowrap',
                          maxWidth: 800,
                        }}
                      >
                        {item.content}
                      </div>
                    </Space>
                  </div>
                </div>
                <Button type="link" icon={<EyeOutlined />} onClick={() => handleView(item)}>
                  查看
                </Button>
              </div>
            ))}
          </div>
        )}
      </Card>

      <Modal
        title={currentNotice?.title}
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="close" type="primary" onClick={() => setDetailVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {currentNotice && (
          <Space orientation="vertical" size="large" style={{ width: '100%' }}>
            <div>
              <Space>
                <Tag color={currentNotice.type === 1 ? 'blue' : 'orange'}>
                  {currentNotice.typeName}
                </Tag>
                {currentNotice.priority === 3 && <Tag color="error">紧急</Tag>}
                {currentNotice.priority === 2 && <Tag color="warning">重要</Tag>}
                {currentNotice.priority === 1 && <Tag>普通</Tag>}
              </Space>
            </div>
            <div>
              <div style={{ color: '#999', marginBottom: 8 }}>
                发布者：{currentNotice.publisherName}
              </div>
              <div style={{ color: '#999' }}>
                发布时间：{dayjs(currentNotice.publishTime).format('YYYY-MM-DD HH:mm:ss')}
              </div>
            </div>
            <div
              style={{
                padding: 16,
                background: themeMode === 'dark' ? '#262626' : '#f5f5f5',
                borderRadius: 4,
                lineHeight: 1.8,
              }}
            >
              {currentNotice.content}
            </div>
          </Space>
        )}
      </Modal>
    </div>
  );
};

export default MyNotice;
