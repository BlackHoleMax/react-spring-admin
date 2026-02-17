import React, { useCallback, useEffect, useState } from 'react';
import { App, Button, Card, Form, Input, Modal, Popconfirm, Select, Space, Table, Tag } from 'antd';
import Authorized from '@/components/Authorized';
import { DeleteOutlined, ExportOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import {
  batchDeleteJobLog,
  cleanJobLog,
  deleteJobLog,
  exportJobLog,
  getJobLogList,
} from '@/services/jobLog';
import type { JobLog } from '@/types';
import { exportExcelFile } from '@/utils/excel';
import dayjs from 'dayjs';

const JobLogPage: React.FC = () => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<JobLog[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentLog, setCurrentLog] = useState<JobLog | null>(null);
  const [searchForm] = Form.useForm();

  const fetchData = useCallback(
    async (page = current, size = pageSize) => {
      setLoading(true);
      try {
        const values = searchForm.getFieldsValue();
        const result = await getJobLogList({ current: page, size, ...values });
        setDataSource(result.records);
        setTotal(result.total);
      } catch (_error) {
        console.error('获取定时任务日志列表失败:', _error);
      } finally {
        setLoading(false);
      }
    },
    [current, pageSize, searchForm]
  );

  useEffect(() => {
    fetchData(current, pageSize);
  }, [fetchData, current, pageSize]);

  const handleSearch = () => {
    setCurrent(1);
    fetchData(1, pageSize);
  };

  const handleReset = () => {
    searchForm.resetFields();
    setCurrent(1);
    fetchData(1, pageSize);
  };

  const handleDelete = async (jobLogId: number) => {
    try {
      await deleteJobLog(jobLogId);
      message.success('删除成功');
      await fetchData();
    } catch {
      console.error('操作失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的日志');
      return;
    }
    try {
      await batchDeleteJobLog(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData();
    } catch {
      console.error('操作失败');
    }
  };

  const handleClean = async () => {
    Modal.confirm({
      title: '清空日志',
      content: '确定要清空所有定时任务日志吗？此操作不可恢复！',
      okText: '确定',
      cancelText: '取消',
      okType: 'danger',
      onOk: async () => {
        try {
          await cleanJobLog();
          message.success('清空成功');
          await fetchData();
        } catch {
          console.error('操作失败');
        }
      },
    });
  };

  const handleExport = async () => {
    try {
      const values = searchForm.getFieldsValue();
      await exportExcelFile(exportJobLog, values, `任务日志_${dayjs().format('YYYYMMDD_HHmmss')}`);
      message.success('导出成功');
    } catch {
      message.error('导出失败');
    }
  };

  const handleViewDetail = (record: JobLog) => {
    setCurrentLog(record);
    setDetailVisible(true);
  };

  const columns = [
    {
      title: '日志ID',
      dataIndex: 'jobLogId',
      key: 'jobLogId',
      width: 80,
    },
    {
      title: '任务名称',
      dataIndex: 'jobName',
      key: 'jobName',
      width: 150,
    },
    {
      title: '任务组名',
      dataIndex: 'jobGroup',
      key: 'jobGroup',
      width: 100,
    },
    {
      title: '调用目标',
      dataIndex: 'invokeTarget',
      key: 'invokeTarget',
      width: 200,
      ellipsis: true,
    },
    {
      title: '执行状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: string) => (
        <Tag color={status === '0' ? 'green' : 'red'}>{status === '0' ? '正常' : '失败'}</Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 150,
      render: (time: string) => (time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '结束时间',
      dataIndex: 'stopTime',
      key: 'stopTime',
      width: 150,
      render: (time: string) => (time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'action',
      fixed: 'right' as const,
      width: 150,
      render: (_: any, record: JobLog) => (
        <Space size="small">
          <Button type="link" size="small" onClick={() => handleViewDetail(record)}>
            详情
          </Button>
          <Authorized permission="joblog:delete">
            <Popconfirm
              title="确定删除该日志吗？"
              onConfirm={() => handleDelete(record.jobLogId)}
              okText="确定"
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
    <div style={{ padding: '24px' }}>
      <Card>
        <Form form={searchForm} layout="inline" style={{ marginBottom: 16 }}>
          <Form.Item name="jobName" label="任务名称">
            <Input placeholder="请输入任务名称" allowClear />
          </Form.Item>
          <Form.Item name="jobGroup" label="任务组名">
            <Input placeholder="请输入任务组名" allowClear />
          </Form.Item>
          <Form.Item name="status" label="执行状态">
            <Select placeholder="请选择执行状态" allowClear style={{ width: 120 }}>
              <Select.Option value="0">正常</Select.Option>
              <Select.Option value="1">失败</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item>
            <Space>
              <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
                查询
              </Button>
              <Button icon={<ReloadOutlined />} onClick={handleReset}>
                重置
              </Button>
            </Space>
          </Form.Item>
        </Form>

        <div style={{ marginBottom: 16 }}>
          <Space>
            <Authorized permission="joblog:export">
              <Button icon={<ExportOutlined />} onClick={handleExport}>
                导出
              </Button>
            </Authorized>
            <Authorized permission="joblog:clean">
              <Button danger icon={<DeleteOutlined />} onClick={handleClean}>
                清空日志
              </Button>
            </Authorized>
            {selectedRowKeys.length > 0 && (
              <Authorized permission="joblog:delete">
                <Popconfirm
                  title={`确定删除选中的 ${selectedRowKeys.length} 条日志吗？`}
                  onConfirm={handleBatchDelete}
                  okText="确定"
                  cancelText="取消"
                >
                  <Button danger icon={<DeleteOutlined />}>
                    批量删除
                  </Button>
                </Popconfirm>
              </Authorized>
            )}
          </Space>
        </div>

        <Table
          rowSelection={{
            selectedRowKeys,
            onChange: setSelectedRowKeys,
          }}
          columns={columns}
          dataSource={dataSource}
          loading={loading}
          rowKey="jobLogId"
          scroll={{ x: 1200 }}
          pagination={{
            current,
            pageSize,
            total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条`,
            onChange: (page, size) => {
              setCurrent(page);
              setPageSize(size);
            },
          }}
        />
      </Card>

      <Modal
        title="日志详情"
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={[
          <Button key="close" onClick={() => setDetailVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        {currentLog && (
          <div>
            <p>
              <strong>任务名称：</strong>
              {currentLog.jobName}
            </p>
            <p>
              <strong>任务组名：</strong>
              {currentLog.jobGroup}
            </p>
            <p>
              <strong>调用目标：</strong>
              {currentLog.invokeTarget}
            </p>
            <p>
              <strong>执行状态：</strong>
              <Tag color={currentLog.status === '0' ? 'green' : 'red'}>
                {currentLog.status === '0' ? '正常' : '失败'}
              </Tag>
            </p>
            <p>
              <strong>创建时间：</strong>
              {dayjs(currentLog.createTime).format('YYYY-MM-DD HH:mm:ss')}
            </p>
            <p>
              <strong>结束时间：</strong>
              {dayjs(currentLog.stopTime).format('YYYY-MM-DD HH:mm:ss')}
            </p>
            {currentLog.jobMessage && (
              <p>
                <strong>日志信息：</strong>
                {currentLog.jobMessage}
              </p>
            )}
            {currentLog.exceptionInfo && (
              <div>
                <p>
                  <strong>异常信息：</strong>
                </p>
                <pre
                  style={{
                    background: '#f5f5f5',
                    padding: '10px',
                    borderRadius: '4px',
                    maxHeight: '200px',
                    overflow: 'auto',
                  }}
                >
                  {currentLog.exceptionInfo}
                </pre>
              </div>
            )}
          </div>
        )}
      </Modal>
    </div>
  );
};

export default JobLogPage;
