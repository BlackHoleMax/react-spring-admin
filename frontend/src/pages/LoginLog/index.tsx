import React, { useCallback, useEffect, useState } from 'react';
import { App, Button, Card, Col, DatePicker, Form, Popconfirm, Row, Space, Table, Tag } from 'antd';
import Authorized from '@/components/Authorized';
import {
  ClearOutlined,
  DeleteOutlined,
  ExportOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  batchDeleteLoginLog,
  cleanLoginLog,
  deleteLoginLog,
  exportLoginLog,
  getLoginLogList,
} from '@/services/loginLog.ts';
import type { LoginLog, LoginLogQuery } from '@/types';
import { exportExcelFile } from '@/utils/excel';
import dayjs from 'dayjs';

const { RangePicker } = DatePicker;

const LoginLogPage: React.FC = () => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<LoginLog[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [searchParams, setSearchParams] = useState<LoginLogQuery>({});
  const [searchForm] = Form.useForm();

  const fetchData = useCallback(
    async (page = current, size = pageSize, params = searchParams) => {
      setLoading(true);
      try {
        const result = await getLoginLogList({ current: page, size, ...params });
        setDataSource(result.records);
        setTotal(result.total);
      } catch (_error) {
        console.error('获取登录日志列表失败:', _error);
      } finally {
        setLoading(false);
      }
    },
    [current, pageSize, searchParams]
  );

  useEffect(() => {
    fetchData(current, pageSize, searchParams);
  }, [fetchData, current, pageSize, searchParams]);

  const handleSearch = () => {
    const values = searchForm.getFieldsValue();
    const params: LoginLogQuery & { startTime?: string; endTime?: string } = {};

    if (values.dateRange?.length === 2) {
      params.startTime = values.dateRange[0].format('YYYY-MM-DD HH:mm:ss');
      params.endTime = values.dateRange[1].format('YYYY-MM-DD HH:mm:ss');
    }

    setSearchParams(params);
    setCurrent(1);
  };

  const handleReset = () => {
    searchForm.resetFields();
    setSearchParams({});
    setCurrent(1);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteLoginLog(id);
      message.success('删除成功');
      await fetchData(current, pageSize, searchParams);
    } catch {
      console.error('操作失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的数据');
      return;
    }
    try {
      await batchDeleteLoginLog(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData(current, pageSize, searchParams);
    } catch {
      console.error('操作失败');
    }
  };

  const handleClean = async () => {
    try {
      await cleanLoginLog();
      message.success('清空成功');
      await fetchData(current, pageSize, searchParams);
    } catch {
      console.error('操作失败');
    }
  };

  const handleExport = async () => {
    try {
      await exportExcelFile(
        exportLoginLog,
        searchParams,
        `登录日志_${dayjs().format('YYYYMMDD_HHmmss')}`
      );
      message.success('导出成功');
    } catch {
      message.error('导出失败');
    }
  };

  const columns = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '用户ID',
      dataIndex: 'userId',
      key: 'userId',
      width: 100,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
    },
    {
      title: '登录IP',
      dataIndex: 'ip',
      key: 'ip',
    },
    {
      title: '浏览器信息',
      dataIndex: 'userAgent',
      key: 'userAgent',
      ellipsis: true,
    },
    {
      title: '登录状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'}>{status === 1 ? '成功' : '失败'}</Tag>
      ),
    },
    {
      title: '登录信息',
      dataIndex: 'msg',
      key: 'msg',
    },
    {
      title: '登录时间',
      dataIndex: 'loginTime',
      key: 'loginTime',
      render: (text: string | number | Date | dayjs.Dayjs | null | undefined) => {
        return dayjs(text).format('YYYY-MM-DD HH:mm:ss');
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 120,
      render: (_: unknown, record: LoginLog) => (
        <Authorized permission="log:delete">
          <Popconfirm title="确定要删除吗？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Authorized>
      ),
    },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <Form form={searchForm} layout="inline">
          <Row gutter={[16, 16]} style={{ width: '100%' }}>
            <Col span={12}>
              <Form.Item name="dateRange" label="登录时间" style={{ marginBottom: 0 }}>
                <RangePicker
                  showTime
                  format="YYYY-MM-DD HH:mm:ss"
                  style={{ width: '100%' }}
                  placeholder={['开始时间', '结束时间']}
                />
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
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>登录日志</h2>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={() => fetchData()}>
            刷新
          </Button>
          <Authorized permission="log:export">
            <Button icon={<ExportOutlined />} onClick={handleExport}>
              导出
            </Button>
          </Authorized>
          {selectedRowKeys.length > 0 && (
            <Authorized permission="log:delete">
              <Popconfirm title="确定要批量删除吗？" onConfirm={handleBatchDelete}>
                <Button danger icon={<DeleteOutlined />}>
                  批量删除 ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            </Authorized>
          )}
          <Authorized permission="log:delete">
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
    </div>
  );
};

export default LoginLogPage;
