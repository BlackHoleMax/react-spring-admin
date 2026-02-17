import React, { useCallback, useEffect, useState } from 'react';
import {
  App,
  Button,
  Card,
  Form,
  Input,
  Modal,
  Popconfirm,
  Select,
  Space,
  Switch,
  Table,
  Tag,
} from 'antd';
import Authorized from '@/components/Authorized';
import {
  DeleteOutlined,
  EditOutlined,
  PlayCircleOutlined,
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import {
  batchDeleteJob,
  changeJobStatus,
  createJob,
  deleteJob,
  getJobList,
  runJob,
  updateJob,
} from '@/services/job';
import type { Job } from '@/types';
import dayjs from 'dayjs';

const JobPage: React.FC = () => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Job[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingJob, setEditingJob] = useState<Job | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  const fetchData = useCallback(
    async (page = current, size = pageSize) => {
      setLoading(true);
      try {
        const values = searchForm.getFieldsValue();
        const result = await getJobList({ current: page, size, ...values });
        setDataSource(result.records);
        setTotal(result.total);
      } catch (_error) {
        console.error('获取定时任务列表失败:', _error);
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

  const handleAdd = () => {
    setEditingJob(null);
    form.resetFields();
    form.setFieldsValue({ status: '0', misfirePolicy: '3', concurrent: '1' });
    setModalVisible(true);
  };

  const handleEdit = (record: Job) => {
    setEditingJob(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handleDelete = async (jobId: number) => {
    try {
      await deleteJob(jobId);
      message.success('删除成功');
      await fetchData();
    } catch {
      console.error('操作失败');
    }
  };

  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的任务');
      return;
    }
    try {
      await batchDeleteJob(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData();
    } catch {
      console.error('操作失败');
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editingJob) {
        await updateJob({ ...values, jobId: editingJob.jobId });
        message.success('更新成功');
      } else {
        await createJob(values);
        message.success('新增成功');
      }
      setModalVisible(false);
      await fetchData();
    } catch {
      console.error('操作失败');
    }
  };

  const handleStatusChange = async (record: Job, checked: boolean) => {
    try {
      const newStatus = checked ? '0' : '1';
      await changeJobStatus({ ...record, status: newStatus });
      message.success('状态修改成功');
      await fetchData();
    } catch {
      console.error('操作失败');
    }
  };

  const handleRun = async (record: Job) => {
    try {
      await runJob(record);
      message.success('任务执行成功');
    } catch {
      console.error('操作失败');
    }
  };

  const columns = [
    {
      title: '任务ID',
      dataIndex: 'jobId',
      key: 'jobId',
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
      title: 'cron表达式',
      dataIndex: 'cronExpression',
      key: 'cronExpression',
      width: 150,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: string, record: Job) => (
        <Switch
          checked={status === '0'}
          onChange={(checked) => handleStatusChange(record, checked)}
          checkedChildren="正常"
          unCheckedChildren="暂停"
        />
      ),
    },
    {
      title: '并发执行',
      dataIndex: 'concurrent',
      key: 'concurrent',
      width: 100,
      render: (concurrent: string) => (
        <Tag color={concurrent === '0' ? 'green' : 'red'}>
          {concurrent === '0' ? '允许' : '禁止'}
        </Tag>
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
      title: '操作',
      key: 'action',
      fixed: 'right' as const,
      width: 200,
      render: (_: any, record: Job) => (
        <Space size="small">
          <Authorized permission="job:run">
            <Button
              type="link"
              size="small"
              icon={<PlayCircleOutlined />}
              onClick={() => handleRun(record)}
            >
              执行
            </Button>
          </Authorized>
          <Authorized permission="job:edit">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            >
              编辑
            </Button>
          </Authorized>
          <Authorized permission="job:delete">
            <Popconfirm
              title="确定删除该任务吗？"
              onConfirm={() => handleDelete(record.jobId)}
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
          <Form.Item name="status" label="状态">
            <Select placeholder="请选择状态" allowClear style={{ width: 120 }}>
              <Select.Option value="0">正常</Select.Option>
              <Select.Option value="1">暂停</Select.Option>
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
            <Authorized permission="job:add">
              <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
                新增
              </Button>
            </Authorized>
            {selectedRowKeys.length > 0 && (
              <Authorized permission="job:delete">
                <Popconfirm
                  title={`确定删除选中的 ${selectedRowKeys.length} 个任务吗？`}
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
          rowKey="jobId"
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
        title={editingJob ? '编辑定时任务' : '新增定时任务'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="jobName"
            label="任务名称"
            rules={[{ required: true, message: '请输入任务名称' }]}
          >
            <Input placeholder="请输入任务名称" />
          </Form.Item>
          <Form.Item
            name="jobGroup"
            label="任务组名"
            rules={[{ required: true, message: '请输入任务组名' }]}
          >
            <Input placeholder="请输入任务组名" />
          </Form.Item>
          <Form.Item
            name="invokeTarget"
            label="调用目标"
            rules={[{ required: true, message: '请输入调用目标' }]}
            extra="示例：beanName.methodName 或 com.example.Class.methodName"
          >
            <Input placeholder="请输入调用目标" />
          </Form.Item>
          <Form.Item
            name="cronExpression"
            label="cron表达式"
            rules={[{ required: true, message: '请输入cron表达式' }]}
            extra="示例：0/10 * * * * ?"
          >
            <Input placeholder="请输入cron表达式" />
          </Form.Item>
          <Form.Item
            name="misfirePolicy"
            label="计划执行错误策略"
            rules={[{ required: true, message: '请选择计划执行错误策略' }]}
          >
            <Select>
              <Select.Option value="1">立即执行</Select.Option>
              <Select.Option value="2">执行一次</Select.Option>
              <Select.Option value="3">放弃执行</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item
            name="concurrent"
            label="是否并发执行"
            rules={[{ required: true, message: '请选择是否并发执行' }]}
          >
            <Select>
              <Select.Option value="0">允许</Select.Option>
              <Select.Option value="1">禁止</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="remark" label="备注">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default JobPage;
