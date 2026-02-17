import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  App,
  Button,
  Card,
  Checkbox,
  Col,
  Form,
  Input,
  Radio,
  Row,
  Select,
  Space,
  Table,
  Tabs,
  TreeSelect,
} from 'antd';
import { ArrowLeftOutlined, CheckOutlined } from '@ant-design/icons';
import {
  getGenTableById,
  updateGenTable,
  type GenTable,
  type GenTableColumn,
} from '@/services/gen';
import { getMenuTree } from '@/services/menu';
import { getAllEnabledDicts } from '@/services/dict';
import type { Menu, Dict } from '@/types';

const CodeGenEdit: React.FC = () => {
  const { tableId } = useParams<{ tableId: string }>();
  const { message } = App.useApp();
  const navigate = useNavigate();
  const [form] = Form.useForm<GenTable>();
  const [loading, setLoading] = useState(false);
  const [activeTab, setActiveTab] = useState('basic');
  const [columns, setColumns] = useState<GenTableColumn[]>([]);
  const [menuTree, setMenuTree] = useState<Menu[]>([]);
  const [dictList, setDictList] = useState<Dict[]>([]);

  const fetchTableInfo = useCallback(async () => {
    if (!tableId) return;
    const id = parseInt(tableId, 10);
    setLoading(true);
    try {
      const result = await getGenTableById(id);
      setColumns(result.columns || []);
      form.setFieldsValue(result);
    } catch {
      message.error('获取表信息失败');
      navigate('/system-tools/code-gen');
    } finally {
      setLoading(false);
    }
  }, [tableId, message, navigate, form]);

  const fetchMenuTree = useCallback(async () => {
    try {
      const menus = await getMenuTree();
      setMenuTree(menus);
    } catch {
      message.error('获取菜单列表失败');
    }
  }, [message]);

  const fetchDictList = useCallback(async () => {
    try {
      const dicts = await getAllEnabledDicts();
      setDictList(dicts);
    } catch {
      message.error('获取字典列表失败');
    }
  }, [message]);

  useEffect(() => {
    fetchTableInfo();
    fetchMenuTree();
    fetchDictList();
  }, [fetchTableInfo, fetchMenuTree, fetchDictList]);

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      await updateGenTable({ ...values, tableId: parseInt(tableId!, 10), columns });
      message.success('保存成功');
      navigate('/system-tools/code-gen');
    } catch {
      message.error('保存失败');
    } finally {
      setLoading(false);
    }
  };

  const handleColumnChange = (index: number, field: keyof GenTableColumn, value: any) => {
    const newColumns = [...columns];
    newColumns[index] = { ...newColumns[index], [field]: value } as GenTableColumn;
    setColumns(newColumns);
  };

  const columnColumns = [
    {
      title: '序号',
      dataIndex: 'sort',
      key: 'sort',
      width: 60,
      render: (_: unknown, __: GenTableColumn, index: number) => index + 1,
    },
    {
      title: '字段列名',
      dataIndex: 'columnName',
      key: 'columnName',
      width: 120,
    },
    {
      title: '字段描述',
      dataIndex: 'columnComment',
      key: 'columnComment',
      width: 150,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Input
          value={text}
          onChange={(e) => handleColumnChange(index, 'columnComment', e.target.value)}
          placeholder="请输入字段描述"
        />
      ),
    },
    {
      title: '物理类型',
      dataIndex: 'columnType',
      key: 'columnType',
      width: 120,
    },
    {
      title: 'Java类型',
      dataIndex: 'javaType',
      key: 'javaType',
      width: 100,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Select
          value={text}
          onChange={(value) => handleColumnChange(index, 'javaType', value)}
          style={{ width: '100%' }}
          options={[
            { label: 'Long', value: 'Long' },
            { label: 'String', value: 'String' },
            { label: 'Integer', value: 'Integer' },
            { label: 'Double', value: 'Double' },
            { label: 'BigDecimal', value: 'BigDecimal' },
            { label: 'Date', value: 'Date' },
            { label: 'Boolean', value: 'Boolean' },
          ]}
        />
      ),
    },
    {
      title: 'Java属性',
      dataIndex: 'javaField',
      key: 'javaField',
      width: 120,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Input
          value={text}
          onChange={(e) => handleColumnChange(index, 'javaField', e.target.value)}
          placeholder="请输入Java属性"
        />
      ),
    },
    {
      title: '插入',
      dataIndex: 'isInsert',
      key: 'isInsert',
      width: 60,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Checkbox
          checked={text === '1'}
          onChange={(e) => handleColumnChange(index, 'isInsert', e.target.checked ? '1' : '0')}
        />
      ),
    },
    {
      title: '编辑',
      dataIndex: 'isEdit',
      key: 'isEdit',
      width: 60,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Checkbox
          checked={text === '1'}
          onChange={(e) => handleColumnChange(index, 'isEdit', e.target.checked ? '1' : '0')}
        />
      ),
    },
    {
      title: '列表',
      dataIndex: 'isList',
      key: 'isList',
      width: 60,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Checkbox
          checked={text === '1'}
          onChange={(e) => handleColumnChange(index, 'isList', e.target.checked ? '1' : '0')}
        />
      ),
    },
    {
      title: '查询',
      dataIndex: 'isQuery',
      key: 'isQuery',
      width: 60,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Checkbox
          checked={text === '1'}
          onChange={(e) => handleColumnChange(index, 'isQuery', e.target.checked ? '1' : '0')}
        />
      ),
    },
    {
      title: '查询方式',
      dataIndex: 'queryType',
      key: 'queryType',
      width: 100,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Select
          value={text}
          onChange={(value) => handleColumnChange(index, 'queryType', value)}
          style={{ width: '100%' }}
          options={[
            { label: '=', value: 'EQ' },
            { label: '!=', value: 'NE' },
            { label: '>', value: 'GT' },
            { label: '>=', value: 'GTE' },
            { label: '<', value: 'LT' },
            { label: '<=', value: 'LTE' },
            { label: 'LIKE', value: 'LIKE' },
            { label: 'BETWEEN', value: 'BETWEEN' },
          ]}
        />
      ),
    },
    {
      title: '必填',
      dataIndex: 'isRequired',
      key: 'isRequired',
      width: 60,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Checkbox
          checked={text === '1'}
          onChange={(e) => handleColumnChange(index, 'isRequired', e.target.checked ? '1' : '0')}
        />
      ),
    },
    {
      title: '显示类型',
      dataIndex: 'htmlType',
      key: 'htmlType',
      width: 120,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Select
          value={text}
          onChange={(value) => handleColumnChange(index, 'htmlType', value)}
          style={{ width: '100%' }}
          options={[
            { label: '文本框', value: 'input' },
            { label: '文本域', value: 'textarea' },
            { label: '下拉框', value: 'select' },
            { label: '复选框', value: 'checkbox' },
            { label: '单选框', value: 'radio' },
            { label: '日期控件', value: 'datetime' },
            { label: '图片上传', value: 'imageUpload' },
            { label: '文件上传', value: 'fileUpload' },
            { label: '富文本控件', value: 'editor' },
          ]}
        />
      ),
    },
    {
      title: '字典类型',
      dataIndex: 'dictType',
      key: 'dictType',
      width: 150,
      render: (text: string, _record: GenTableColumn, index: number) => (
        <Select
          value={text}
          onChange={(value) => handleColumnChange(index, 'dictType', value)}
          placeholder="请选择字典类型"
          allowClear
          showSearch
          style={{ width: '100%' }}
          options={dictList.map((dict) => ({
            label: `${dict.dictName} (${dict.dictCode})`,
            value: dict.dictCode,
          }))}
        />
      ),
    },
  ];

  return (
    <div>
      <Card style={{ marginBottom: 16 }}>
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          <Space>
            <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/system-tools/code-gen')}>
              返回
            </Button>
            <span style={{ marginLeft: 8, fontSize: '18px', fontWeight: 'bold' }}>
              编辑代码生成配置
            </span>
          </Space>
          <Space>
            <Button type="primary" icon={<CheckOutlined />} onClick={handleSave} loading={loading}>
              保存
            </Button>
          </Space>
        </div>
      </Card>

      <Card>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={[
            {
              key: 'basic',
              label: '基本信息',
              children: (
                <Form form={form} layout="vertical">
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="tableName"
                        label="表名称"
                        rules={[{ required: true, message: '请输入表名称' }]}
                      >
                        <Input placeholder="请输入表名称" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="tableComment"
                        label="表描述"
                        rules={[{ required: true, message: '请输入表描述' }]}
                      >
                        <Input placeholder="请输入表描述" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="className"
                        label="实体类名称"
                        rules={[{ required: true, message: '请输入实体类名称' }]}
                      >
                        <Input placeholder="请输入实体类名称" />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="functionAuthor"
                        label="作者"
                        rules={[{ required: true, message: '请输入作者' }]}
                      >
                        <Input placeholder="请输入作者" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Row gutter={16}>
                    <Col span={24}>
                      <Form.Item name="remark" label="备注">
                        <Input.TextArea rows={4} placeholder="请输入备注" />
                      </Form.Item>
                    </Col>
                  </Row>
                </Form>
              ),
            },
            {
              key: 'column',
              label: '字段信息',
              children: (
                <Table
                  columns={columnColumns}
                  dataSource={columns}
                  rowKey="columnId"
                  pagination={false}
                  scroll={{ x: 2000 }}
                  size="small"
                />
              ),
            },
            {
              key: 'gen',
              label: '生成信息',
              children: (
                <Form form={form} layout="vertical">
                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="tplCategory"
                        label="生成模板"
                        rules={[{ required: true, message: '请选择生成模板' }]}
                      >
                        <Select
                          placeholder="请选择生成模板"
                          options={[
                            { label: '单表（增删改查）', value: 'crud' },
                            { label: '树表（增删改查）', value: 'tree' },
                          ]}
                        />
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item
                        name="packageName"
                        label="生成包路径"
                        rules={[{ required: true, message: '请输入生成包路径' }]}
                      >
                        <Input placeholder="请输入生成包路径" />
                      </Form.Item>
                    </Col>
                  </Row>

                  <Row gutter={16}>
                    <Col span={12}>
                      <Form.Item
                        name="genType"
                        label="生成代码方式"
                        rules={[{ required: true, message: '请选择生成代码方式' }]}
                      >
                        <Radio.Group>
                          <Radio value="0">zip压缩包</Radio>
                          <Radio value="1">自定义路径</Radio>
                        </Radio.Group>
                      </Form.Item>
                    </Col>
                    <Col span={12}>
                      <Form.Item name="parentMenuId" label="上级菜单">
                        <TreeSelect
                          placeholder="请选择上级菜单（默认为根菜单）"
                          treeDefaultExpandAll
                          allowClear
                          treeData={[
                            {
                              title: '根菜单',
                              value: 0,
                              key: 0,
                              children: menuTree.map((menu) => ({
                                title: menu.name,
                                value: menu.id,
                                key: menu.id,
                                children:
                                  menu.children?.map((child) => ({
                                    title: child.name,
                                    value: child.id,
                                    key: child.id,
                                  })) || [],
                              })),
                            },
                          ]}
                        />
                      </Form.Item>
                    </Col>
                  </Row>
                </Form>
              ),
            },
          ]}
        />
      </Card>
    </div>
  );
};

export default CodeGenEdit;
