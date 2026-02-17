import React, { useCallback, useEffect, useState } from 'react';
import {
  App,
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
  Table,
  Tag,
} from 'antd';
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
  batchDeleteDict,
  createDict,
  createDictItem,
  deleteDict,
  deleteDictItem,
  getDictItemList,
  getDictList,
  updateDict,
  updateDictItem,
} from '@/services/dict.ts';
import type { Dict, DictItem, DictQuery } from '@/types';
import dayjs from 'dayjs';

const DictPage: React.FC = () => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<Dict[]>([]);
  const [total, setTotal] = useState(0);
  const [current, setCurrent] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingDict, setEditingDict] = useState<Dict | null>(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [searchParams, setSearchParams] = useState<DictQuery>({});
  const [form] = Form.useForm();
  const [searchForm] = Form.useForm();

  const [itemModalVisible, setItemModalVisible] = useState(false);
  const [dictItems, setDictItems] = useState<DictItem[]>([]);
  const [currentDictCode, setCurrentDictCode] = useState('');
  const [itemModalLoading, setItemModalLoading] = useState(false);
  const [currentDictId, setCurrentDictId] = useState<number | null>(null);
  const [itemFormVisible, setItemFormVisible] = useState(false);
  const [editingItem, setEditingItem] = useState<DictItem | null>(null);
  const [itemForm] = Form.useForm();

  const fetchData = useCallback(
    async (page = current, size = pageSize, params = searchParams) => {
      setLoading(true);
      try {
        const result = await getDictList({ current: page, size, ...params });
        setDataSource(result.records);
        setTotal(result.total);
      } catch (_error) {
        console.error('获取字典列表失败:', _error);
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
    setSearchParams(values);
    setCurrent(1);
  };

  const handleReset = () => {
    searchForm.resetFields();
    setSearchParams({});
    setCurrent(1);
  };

  const handleAdd = () => {
    setEditingDict(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: Dict) => {
    setEditingDict(record);
    form.setFieldsValue(record);
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteDict(id);
      message.success('删除成功');
      await fetchData();
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
      await batchDeleteDict(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      await fetchData();
    } catch {
      message.error('批量删除失败');
    }
  };

  const handleExport = async () => {
    try {
      const response = await fetch('/api/system/dict/export', {
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
        a.download = `字典数据_${new Date().toLocaleDateString()}.xlsx`;
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

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      if (editingDict) {
        await updateDict({ ...values, id: editingDict.id });
        message.success('更新成功');
      } else {
        await createDict(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      await fetchData();
    } catch {
      console.error('操作失败');
    }
  };

  const handleViewItems = async (record: Dict) => {
    try {
      setItemModalLoading(true);
      setCurrentDictCode(record.dictCode);
      setCurrentDictId(record.id);
      const items = await getDictItemList(record.id);
      setDictItems(items);
      setItemModalVisible(true);
    } catch (error) {
      console.error('获取字典数据失败:', error);
      message.error('获取字典数据失败');
    } finally {
      setItemModalLoading(false);
    }
  };

  const handleAddItem = () => {
    setEditingItem(null);
    itemForm.resetFields();
    setItemFormVisible(true);
  };

  const handleEditItem = (record: DictItem) => {
    setEditingItem(record);
    itemForm.setFieldsValue(record);
    setItemFormVisible(true);
  };

  const handleDeleteItem = async (id: number) => {
    try {
      await deleteDictItem(id);
      message.success('删除成功');
      if (currentDictId) {
        const items = await getDictItemList(currentDictId);
        setDictItems(items);
      }
    } catch (error) {
      console.error('删除失败:', error);
      message.error('删除失败');
    }
  };

  const handleSaveItem = async () => {
    try {
      const values = await itemForm.validateFields();
      if (currentDictId) {
        const itemData = {
          ...values,
          dictId: currentDictId,
        };

        if (editingItem) {
          await updateDictItem({ ...itemData, id: editingItem.id });
          message.success('更新成功');
        } else {
          await createDictItem(itemData);
          message.success('创建成功');
        }

        setItemFormVisible(false);
        const items = await getDictItemList(currentDictId);
        setDictItems(items);
      }
    } catch (error) {
      console.error('操作失败:', error);
      message.error('操作失败');
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
      title: '字典名称',
      dataIndex: 'dictName',
      key: 'dictName',
    },
    {
      title: '字典编码',
      dataIndex: 'dictCode',
      key: 'dictCode',
      render: (text: string, record: Dict) => (
        <Button type="link" onClick={() => handleViewItems(record)}>
          {text}
        </Button>
      ),
    },
    {
      title: '排序',
      dataIndex: 'sort',
      key: 'sort',
      width: 100,
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'red'}>{status === 1 ? '启用' : '禁用'}</Tag>
      ),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (text: string | number | Date | dayjs.Dayjs | null | undefined) => {
        return dayjs(text).format('YYYY-MM-DD HH:mm:ss');
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 200,
      render: (_: unknown, record: Dict) => (
        <Space>
          <Authorized permission="dict:edit">
            <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
              编辑
            </Button>
          </Authorized>
          <Authorized permission="dict:delete">
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
              <Form.Item name="dictName" label="字典名称" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入字典名称" allowClear />
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item name="dictCode" label="字典编码" style={{ marginBottom: 0 }}>
                <Input placeholder="请输入字典编码" allowClear />
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
            <Col span={6}>
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
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>字典管理</h2>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={() => fetchData()}>
            刷新
          </Button>
          <Authorized permission="dict:export">
            <Button icon={<ExportOutlined />} onClick={handleExport}>
              导出
            </Button>
          </Authorized>
          {selectedRowKeys.length > 0 && (
            <Authorized permission="dict:delete">
              <Popconfirm title="确定删除选中的数据?" onConfirm={handleBatchDelete}>
                <Button danger icon={<DeleteOutlined />}>
                  批量删除 ({selectedRowKeys.length})
                </Button>
              </Popconfirm>
            </Authorized>
          )}
          <Authorized permission="dict:add">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              新增字典
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
        title={editingDict ? '编辑字典' : '新增字典'}
        open={modalVisible}
        onOk={handleOk}
        onCancel={() => setModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="字典名称"
            name="dictName"
            rules={[{ required: true, message: '请输入字典名称' }]}
          >
            <Input placeholder="请输入字典名称" />
          </Form.Item>

          <Form.Item
            label="字典编码"
            name="dictCode"
            rules={[{ required: true, message: '请输入字典编码' }]}
          >
            <Input placeholder="请输入字典编码" />
          </Form.Item>

          <Form.Item label="排序" name="sort" initialValue={0}>
            <Input type="number" placeholder="请输入排序" />
          </Form.Item>

          <Form.Item
            label="状态"
            name="status"
            initialValue={1}
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={4} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`字典数据 - ${currentDictCode}`}
        open={itemModalVisible}
        onCancel={() => setItemModalVisible(false)}
        footer={[
          <Button key="close" onClick={() => setItemModalVisible(false)}>
            关闭
          </Button>,
        ]}
        width={1000}
      >
        <div style={{ marginBottom: 16 }}>
          <Authorized permission="dict:add">
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAddItem}>
              新增
            </Button>
          </Authorized>
        </div>
        <Table
          loading={itemModalLoading}
          rowKey="id"
          dataSource={dictItems}
          pagination={false}
          size="small"
          columns={[
            {
              title: 'ID',
              dataIndex: 'id',
              key: 'id',
              width: 80,
            },
            {
              title: '字典项文本',
              dataIndex: 'itemText',
              key: 'itemText',
            },
            {
              title: '字典项值',
              dataIndex: 'itemValue',
              key: 'itemValue',
            },
            {
              title: '排序',
              dataIndex: 'sort',
              key: 'sort',
              width: 80,
            },
            {
              title: '状态',
              dataIndex: 'status',
              key: 'status',
              width: 80,
              render: (status: number) => (
                <Tag color={status === 1 ? 'green' : 'red'}>{status === 1 ? '启用' : '禁用'}</Tag>
              ),
            },
            {
              title: '创建时间',
              dataIndex: 'createTime',
              key: 'createTime',
              width: 160,
              render: (text: string | number | Date | dayjs.Dayjs | null | undefined) => {
                return dayjs(text).format('YYYY-MM-DD HH:mm:ss');
              },
            },
            {
              title: '操作',
              key: 'action',
              width: 120,
              render: (_: unknown, record: DictItem) => (
                <Space>
                  <Authorized permission="dict:edit">
                    <Button
                      type="link"
                      size="small"
                      icon={<EditOutlined />}
                      onClick={() => handleEditItem(record)}
                    >
                      编辑
                    </Button>
                  </Authorized>
                  <Authorized permission="dict:delete">
                    <Popconfirm
                      title="确定要删除吗？"
                      onConfirm={() => handleDeleteItem(record.id)}
                    >
                      <Button type="link" size="small" danger icon={<DeleteOutlined />}>
                        删除
                      </Button>
                    </Popconfirm>
                  </Authorized>
                </Space>
              ),
            },
          ]}
        />
      </Modal>

      <Modal
        title={editingItem ? '编辑字典数据' : '新增字典数据'}
        open={itemFormVisible}
        onOk={handleSaveItem}
        onCancel={() => setItemFormVisible(false)}
        width={600}
      >
        <Form form={itemForm} layout="vertical">
          <Form.Item
            label="字典项文本"
            name="itemText"
            rules={[{ required: true, message: '请输入字典项文本' }]}
          >
            <Input placeholder="请输入字典项文本" />
          </Form.Item>

          <Form.Item
            label="字典项值"
            name="itemValue"
            rules={[{ required: true, message: '请输入字典项值' }]}
          >
            <Input placeholder="请输入字典项值" />
          </Form.Item>

          <Form.Item label="排序" name="sort" initialValue={0}>
            <Input type="number" placeholder="请输入排序" />
          </Form.Item>

          <Form.Item
            label="状态"
            name="status"
            initialValue={1}
            rules={[{ required: true, message: '请选择状态' }]}
          >
            <Select>
              <Select.Option value={1}>启用</Select.Option>
              <Select.Option value={0}>禁用</Select.Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default DictPage;
