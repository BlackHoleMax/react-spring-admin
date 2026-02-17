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
  Tooltip,
} from 'antd';
import Authorized from '@/components/Authorized';
import {
  ClearOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EyeOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  batchDeleteOperLog,
  cleanOperLog,
  deleteOperLog,
  exportOperLog,
  getOperLogList,
} from '@/services/operLog.ts';
import type { OperLog, OperLogQuery } from '@/types';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;
const { Option } = Select;
const { TextArea } = Input;

const OperLogPage: React.FC = () => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<OperLog[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [searchParams, setSearchParams] = useState<OperLogQuery>({});
  const [searchForm] = Form.useForm();
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailRecord, setDetailRecord] = useState<OperLog | null>(null);

  const businessTypeMap: Record<number, { text: string; color: string }> = {
    0: { text: '其它', color: 'default' },
    1: { text: '新增', color: 'green' },
    2: { text: '修改', color: 'blue' },
    3: { text: '删除', color: 'red' },
    4: { text: '授权', color: 'orange' },
    5: { text: '导出', color: 'cyan' },
    6: { text: '导入', color: 'purple' },
    7: { text: '清空', color: 'magenta' },
  };

  const fetchData = useCallback(
    async (page = current, size = pageSize, params = searchParams) => {
      setLoading(true);
      try {
        const result = await getOperLogList({ ...params, current: page, size });
        setDataSource(result.records);
        setTotal(result.total);
      } catch (_error) {
        console.error('获取操作日志列表失败:', _error);
      } finally {
        setLoading(false);
      }
    },
    [current, pageSize, searchParams]
  );

  useEffect(() => {
    fetchData(current, pageSize, searchParams);
  }, [fetchData, current, pageSize, searchParams]);

  const handleSearch = useCallback(() => {
    const values = searchForm.getFieldsValue();
    const params: OperLogQuery & { startTime?: string; endTime?: string } = {};

    if (values.title) {
      params.title = values.title;
    }
    if (values.businessType !== undefined) {
      params.businessType = values.businessType;
    }
    if (values.status !== undefined) {
      params.status = values.status;
    }
    if (values.dateRange?.length === 2) {
      params.startTime = values.dateRange[0].format('YYYY-MM-DD HH:mm:ss');
      params.endTime = values.dateRange[1].format('YYYY-MM-DD HH:mm:ss');
    }

    setSearchParams(params);
    setCurrent(1);
  }, [searchForm]);

  const handleReset = useCallback(() => {
    searchForm.resetFields();
    setSearchParams({});
    setCurrent(1);
  }, [searchForm]);

  const handleDelete = useCallback(
    async (id: number) => {
      try {
        await deleteOperLog(id);
        message.success('删除成功');
        await fetchData(current, pageSize, searchParams);
      } catch {
        console.error('操作失败');
      }
    },
    [message, fetchData, current, pageSize, searchParams]
  );

  const handleBatchDelete = useCallback(async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    try {
      await batchDeleteOperLog(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData(current, pageSize, searchParams);
    } catch {
      console.error('操作失败');
    }
  }, [selectedRowKeys, message, fetchData, current, pageSize, searchParams]);

  const handleClean = useCallback(async () => {
    try {
      await cleanOperLog();
      message.success('清空成功');
      await fetchData(current, pageSize, searchParams);
    } catch {
      console.error('操作失败');
    }
  }, [message, fetchData, current, pageSize, searchParams]);

  const handleExport = useCallback(async () => {
    try {
      const blob = await exportOperLog(searchParams);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `操作日志_${dayjs().format('YYYYMMDDHHmmss')}.xlsx`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      message.success('导出成功');
    } catch {
      console.error('操作失败');
    }
  }, [searchParams, message]);

  const handleViewDetail = useCallback((record: OperLog) => {
    setDetailRecord(record);
    setDetailVisible(true);
  }, []);

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '模块标题',
      dataIndex: 'title',
      key: 'title',
      width: 120,
    },
    {
      title: '业务类型',
      dataIndex: 'businessType',
      key: 'businessType',
      width: 100,
      render: (type: number) => {
        const config = businessTypeMap[type] || businessTypeMap[0];
        if (!config) return <Tag>未知</Tag>;
        return <Tag color={config.color}>{config.text}</Tag>;
      },
    },
    {
      title: '请求方式',
      dataIndex: 'requestMethod',
      key: 'requestMethod',
      width: 100,
      render: (method: string) => {
        const colorMap: Record<string, string> = {
          GET: 'blue',
          POST: 'green',
          PUT: 'orange',
          DELETE: 'red',
        };
        return <Tag color={colorMap[method] || 'default'}>{method}</Tag>;
      },
    },
    {
      title: '操作人员',
      dataIndex: 'operName',
      key: 'operName',
      width: 120,
    },
    {
      title: '请求URL',
      dataIndex: 'operUrl',
      key: 'operUrl',
      ellipsis: true,
      width: 200,
      render: (text: string) => (
        <Tooltip title={text}>
          <span>{text}</span>
        </Tooltip>
      ),
    },
    {
      title: '操作IP',
      dataIndex: 'operIp',
      key: 'operIp',
      width: 140,
    },
    {
      title: '操作状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: number) => (
        <Tag color={status === 0 ? 'green' : 'red'}>{status === 0 ? '正常' : '异常'}</Tag>
      ),
    },
    {
      title: '耗时(ms)',
      dataIndex: 'costTime',
      key: 'costTime',
      width: 100,
      render: (time: number) => {
        const color = time > 1000 ? 'red' : time > 500 ? 'orange' : 'green';
        return <Tag color={color}>{time}</Tag>;
      },
    },
    {
      title: '操作时间',
      dataIndex: 'operTime',
      key: 'operTime',
      width: 180,
      render: (text: string | number | Date | dayjs.Dayjs | null | undefined) => {
        return dayjs(text).format('YYYY-MM-DD HH:mm:ss');
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right' as const,
      render: (_: unknown, record: OperLog) => (
        <Space>
          <Button type="link" icon={<EyeOutlined />} onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          <Authorized permission="operlog:delete">
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
        <Form form={searchForm}>
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12} md={8} lg={8}>
              <Form.Item name="title" style={{ marginBottom: 0 }}>
                <Input placeholder="模块标题" allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={8}>
              <Form.Item name="businessType" style={{ marginBottom: 0 }}>
                <Select placeholder="业务类型" allowClear>
                  <Option value={0}>其它</Option>
                  <Option value={1}>新增</Option>
                  <Option value={2}>修改</Option>
                  <Option value={3}>删除</Option>
                  <Option value={4}>授权</Option>
                  <Option value={5}>导出</Option>
                  <Option value={6}>导入</Option>
                  <Option value={7}>清空</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col xs={24} sm={12} md={8} lg={8}>
              <Form.Item name="status" style={{ marginBottom: 0 }}>
                <Select placeholder="操作状态" allowClear>
                  <Option value={0}>正常</Option>
                  <Option value={1}>异常</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col xs={24} sm={24} md={16} lg={16}>
              <Form.Item name="dateRange" style={{ marginBottom: 0 }}>
                <RangePicker
                  showTime
                  format="YYYY-MM-DD HH:mm:ss"
                  style={{ width: '100%' }}
                  placeholder={['开始时间', '结束时间']}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={24} md={8} lg={8}>
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
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>操作日志</h2>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={() => fetchData()}>
            刷新
          </Button>
          <Authorized permission="operlog:export">
            <Button icon={<DownloadOutlined />} onClick={handleExport}>
              导出
            </Button>
          </Authorized>
          {selectedRowKeys.length > 0 && (
            <Authorized permission="operlog:delete">
              <Popconfirm title="确定要批量删除吗？" onConfirm={handleBatchDelete}>
                <Button danger icon={<DeleteOutlined />}>
                  批量删除 ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            </Authorized>
          )}
          <Authorized permission="operlog:delete">
            <Popconfirm title="确定要清空所有日志吗？" onConfirm={handleClean}>
              <Button danger icon={<ClearOutlined />}>
                清空日志
              </Button>
            </Popconfirm>
          </Authorized>
        </Space>
      </div>

      <Table
        loading={loading}
        rowKey="id"
        columns={columns}
        dataSource={dataSource}
        scroll={{ x: 1500 }}
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
        title="操作日志详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {detailRecord && (
          <div>
            <Row gutter={[16, 16]}>
              <Col span={12}>
                <strong>模块标题：</strong>
                {detailRecord.title}
              </Col>
              <Col span={12}>
                <strong>业务类型：</strong>
                <Tag color={businessTypeMap[detailRecord.businessType]?.color || 'default'}>
                  {businessTypeMap[detailRecord.businessType]?.text || '未知'}
                </Tag>
              </Col>
              <Col span={12}>
                <strong>请求方式：</strong>
                {detailRecord.requestMethod}
              </Col>
              <Col span={12}>
                <strong>操作人员：</strong>
                {detailRecord.operName}
              </Col>
              <Col span={12}>
                <strong>操作IP：</strong>
                {detailRecord.operIp}
              </Col>
              <Col span={12}>
                <strong>操作状态：</strong>
                <Tag color={detailRecord.status === 0 ? 'green' : 'red'}>
                  {detailRecord.status === 0 ? '正常' : '异常'}
                </Tag>
              </Col>
              <Col span={12}>
                <strong>耗时：</strong>
                {detailRecord.costTime}ms
              </Col>
              <Col span={12}>
                <strong>操作时间：</strong>
                {dayjs(detailRecord.operTime).format('YYYY-MM-DD HH:mm:ss')}
              </Col>
              <Col span={24}>
                <strong>请求URL：</strong>
                <div
                  style={{
                    marginTop: 8,
                    padding: 8,
                    background: '#f5f5f5',
                    borderRadius: 4,
                    color: '#000',
                  }}
                >
                  {detailRecord.operUrl}
                </div>
              </Col>
              <Col span={24}>
                <strong>方法名称：</strong>
                <div
                  style={{
                    marginTop: 8,
                    padding: 8,
                    background: '#f5f5f5',
                    borderRadius: 4,
                    color: '#000',
                  }}
                >
                  {detailRecord.method}
                </div>
              </Col>
              <Col span={24}>
                <strong>请求参数：</strong>
                <TextArea
                  value={detailRecord.operParam}
                  readOnly
                  autoSize={{ minRows: 3, maxRows: 6 }}
                  style={{ marginTop: 8 }}
                />
              </Col>
              <Col span={24}>
                <strong>返回结果：</strong>
                <TextArea
                  value={detailRecord.jsonResult}
                  readOnly
                  autoSize={{ minRows: 3, maxRows: 6 }}
                  style={{ marginTop: 8 }}
                />
              </Col>
              {detailRecord.errorMsg && (
                <Col span={24}>
                  <strong>错误消息：</strong>
                  <TextArea
                    value={detailRecord.errorMsg}
                    readOnly
                    autoSize={{ minRows: 2, maxRows: 4 }}
                    style={{ marginTop: 8 }}
                  />
                </Col>
              )}
            </Row>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default OperLogPage;
