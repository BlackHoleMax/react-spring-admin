import React, { useCallback, useEffect, useState } from 'react';
import {
  Alert,
  Button,
  Card,
  Col,
  Empty,
  Popconfirm,
  Row,
  Space,
  Table,
  Tag,
  Typography,
  Form,
  Input,
  App,
} from 'antd';
import { DeleteOutlined, ReloadOutlined, ClearOutlined } from '@ant-design/icons';
import { cacheApi } from '@/services/cache';
import { useAppSelector } from '@/store/hooks';

const { Title, Text } = Typography;
const { TextArea } = Input;

interface CacheGroup {
  prefix: string;
  count: number;
  keys: string[];
}

interface KeyRecord {
  key: string;
  index: number;
}

interface CacheDetail {
  key: string;
  value: string;
  type: string;
  ttl: number;
}

const CacheList: React.FC = () => {
  const [cacheGroups, setCacheGroups] = useState<CacheGroup[]>([]);
  const [selectedGroup, setSelectedGroup] = useState<string>('');
  const [groupKeys, setGroupKeys] = useState<string[]>([]);
  const [selectedKey, setSelectedKey] = useState<string>('');
  const [cacheDetail, setCacheDetail] = useState<CacheDetail | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form] = Form.useForm();
  const themeMode = useAppSelector((state) => state.theme.mode);
  const isDarkMode = themeMode === 'dark';
  const { message } = App.useApp();

  const fetchCacheGroups = useCallback(async () => {
    setLoading(true);
    try {
      const allKeys = (await cacheApi.getKeys('*')) || [];
      const groups = new Map<string, string[]>();

      allKeys.forEach((key) => {
        const parts = key.split(':');
        const prefix = parts.length > 1 ? parts[0] : 'default';
        if (prefix && !groups.has(prefix)) {
          groups.set(prefix, []);
        }
        if (prefix) {
          groups.get(prefix)?.push(key);
        }
      });

      const groupList: CacheGroup[] = Array.from(groups.entries())
        .map(([prefix, keys]) => ({
          prefix,
          count: keys.length,
          keys,
        }))
        .sort((a, b) => b.count - a.count);

      setCacheGroups(groupList);

      // 自动选中第一个分组
      if (groupList.length > 0 && !selectedGroup) {
        setSelectedGroup(groupList[0]!.prefix);
        setGroupKeys(groupList[0]!.keys);
      }
    } catch (error) {
      console.error('获取缓存分组失败:', error);
      message.error('获取缓存分组失败');
    } finally {
      setLoading(false);
    }
  }, [message, selectedGroup]);

  const handleGroupSelect = (prefix: string) => {
    setSelectedGroup(prefix);
    const group = cacheGroups.find((g) => g.prefix === prefix);
    if (group) {
      setGroupKeys(group.keys);
      setSelectedKey('');
      setCacheDetail(null);
    }
  };

  const handleKeySelect = async (key: string) => {
    setSelectedKey(key);
    try {
      const detail = await cacheApi.getKeyDetail(key);

      setCacheDetail({
        key,
        value: String(detail.value || ''),
        type: detail.type,
        ttl: detail.ttl,
      });

      form.setFieldsValue({
        cacheName: selectedGroup,
        cacheKey: key,
        cacheContent: String(detail.value || ''),
      });
    } catch (error) {
      console.error('获取缓存内容失败:', error);
      message.error('获取缓存内容失败');
    }
  };

  const handleDeleteGroup = async (prefix: string) => {
    try {
      const group = cacheGroups.find((g) => g.prefix === prefix);
      if (group) {
        for (const key of group.keys) {
          await cacheApi.deleteKey(key);
        }
        message.success(`删除分组 "${prefix}" 成功`);
        fetchCacheGroups();
      }
    } catch (error) {
      console.error('删除分组失败:', error);
      message.error('删除分组失败');
    }
  };

  const handleDeleteKey = async (key: string) => {
    try {
      await cacheApi.deleteKey(key);
      message.success('删除成功');
      setGroupKeys((prev) => prev.filter((k) => k !== key));
      setCacheDetail(null);
      fetchCacheGroups();
    } catch (error) {
      console.error('删除键失败:', error);
      message.error('删除键失败');
    }
  };

  const handleClearAll = async () => {
    try {
      await cacheApi.clearDb();
      message.success('清空数据库成功');
      setCacheGroups([]);
      setGroupKeys([]);
      setSelectedGroup('');
      setSelectedKey('');
      setCacheDetail(null);
    } catch (error) {
      console.error('清空数据库失败:', error);
      message.error('清空数据库失败');
    }
  };

  const handleSaveValue = async () => {
    if (!selectedKey) {
      message.warning('请先选择一个缓存键');
      return;
    }

    setSaving(true);
    try {
      // 这里需要后端支持设置键值的API
      message.success('保存成功（功能待实现）');
    } catch (error) {
      console.error('保存失败:', error);
      message.error('保存失败');
    } finally {
      setSaving(false);
    }
  };

  useEffect(() => {
    fetchCacheGroups();
  }, [fetchCacheGroups]);

  const groupColumns = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      width: 60,
      render: (_: unknown, __: CacheGroup, index: number) => index + 1,
    },
    {
      title: '缓存名称',
      dataIndex: 'prefix',
      key: 'prefix',
      render: (prefix: string, record: CacheGroup) => (
        <div>
          <Text strong style={{ fontSize: '14px' }}>
            {prefix}
          </Text>
          <br />
          <Text type="secondary" style={{ fontSize: '12px' }}>
            {record.count} 个键
          </Text>
        </div>
      ),
    },
    {
      title: '备注',
      dataIndex: 'prefix',
      key: 'remark',
      render: (prefix: string) => {
        const remarks: Record<string, string> = {
          user: '用户信息缓存',
          dict: '字典数据缓存',
          token: '登录令牌缓存',
          session: '会话信息缓存',
          default: '默认缓存',
        };
        return remarks[prefix] || '-';
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_: unknown, record: CacheGroup) => (
        <Popconfirm
          title="确认删除"
          description={`确定要删除分组 "${record.prefix}" 及其所有键吗？`}
          onConfirm={() => handleDeleteGroup(record.prefix)}
          okText="确定"
          cancelText="取消"
        >
          <Button type="link" size="small" danger icon={<DeleteOutlined />}>
            删除
          </Button>
        </Popconfirm>
      ),
    },
  ];

  const keyColumns = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      width: 60,
      render: (_: unknown, _record: KeyRecord, index: number) => index + 1,
    },
    {
      title: '缓存键名',
      dataIndex: 'key',
      key: 'key',
      render: (key: string) => (
        <Text copyable style={{ fontSize: '12px' }}>
          {key}
        </Text>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 80,
      render: (_: unknown, record: KeyRecord) => (
        <Popconfirm
          title="确认删除"
          description={`确定要删除键 "${record.key}" 吗？`}
          onConfirm={() => handleDeleteKey(record.key)}
          okText="确定"
          cancelText="取消"
        >
          <Button type="link" size="small" danger icon={<DeleteOutlined />}>
            删除
          </Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <div style={{ padding: '24px' }}>
      <Row gutter={16} style={{ marginBottom: '16px' }}>
        <Col span={24}>
          <Alert
            title="缓存列表"
            description="管理和查看Redis缓存中的所有键，支持按分组查看、删除和编辑"
            type="info"
            showIcon
          />
        </Col>
      </Row>

      <Row gutter={16} style={{ height: 'calc(100vh - 200px)' }}>
        {/* 第一列：缓存列表 */}
        <Col span={8}>
          <Card
            title={
              <div
                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
              >
                <Title level={5} style={{ margin: 0 }}>
                  缓存列表
                </Title>
                <Button
                  size="small"
                  icon={<ReloadOutlined />}
                  onClick={fetchCacheGroups}
                  loading={loading}
                >
                  刷新
                </Button>
              </div>
            }
            styles={{ body: { padding: '12px', height: 'calc(100% - 60px)', overflow: 'auto' } }}
          >
            <Table
              columns={groupColumns}
              dataSource={cacheGroups}
              rowKey="prefix"
              size="small"
              pagination={false}
              onRow={(record) => ({
                onClick: () => handleGroupSelect(record.prefix),
                style: {
                  cursor: 'pointer',
                  backgroundColor:
                    selectedGroup === record.prefix
                      ? isDarkMode
                        ? '#262626'
                        : '#e6f7ff'
                      : undefined,
                },
              })}
            />
          </Card>
        </Col>

        {/* 第二列：键名列表 */}
        <Col span={8}>
          <Card
            title={
              <Title level={5} style={{ margin: 0 }}>
                键名列表
                {selectedGroup && (
                  <Tag color="blue" style={{ marginLeft: '8px' }}>
                    {selectedGroup}
                  </Tag>
                )}
              </Title>
            }
            styles={{ body: { padding: '12px', height: 'calc(100% - 60px)', overflow: 'auto' } }}
          >
            {groupKeys.length > 0 ? (
              <Table<KeyRecord>
                columns={keyColumns}
                dataSource={groupKeys.map((key, index) => ({ key, index })) as KeyRecord[]}
                rowKey="key"
                size="small"
                pagination={false}
                onRow={(record) => ({
                  onClick: () => handleKeySelect(record.key),
                  style: {
                    cursor: 'pointer',
                    backgroundColor:
                      selectedKey === record.key
                        ? isDarkMode
                          ? 'rgba(255, 255, 255, 0.1)'
                          : '#e6f7ff'
                        : undefined,
                  },
                })}
              />
            ) : (
              <Empty
                description={selectedGroup ? '该分组暂无键' : '请选择一个分组'}
                style={{ marginTop: '40px' }}
              />
            )}
          </Card>
        </Col>

        {/* 第三列：缓存内容 */}
        <Col span={8}>
          <Card
            title={
              <div
                style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
              >
                <Title level={5} style={{ margin: 0 }}>
                  缓存内容
                </Title>
                <Popconfirm
                  title="确认清空"
                  description="确定要清空当前数据库的所有键吗？此操作不可恢复！"
                  onConfirm={handleClearAll}
                  okText="确定"
                  cancelText="取消"
                >
                  <Button size="small" danger icon={<ClearOutlined />}>
                    清理全部
                  </Button>
                </Popconfirm>
              </div>
            }
            styles={{ body: { padding: '12px', height: 'calc(100% - 60px)', overflow: 'auto' } }}
          >
            {cacheDetail ? (
              <Form form={form} layout="vertical" size="small">
                <Form.Item label="缓存名称">
                  <Input value={selectedGroup} disabled />
                </Form.Item>
                <Form.Item label="缓存键名">
                  <Input value={selectedKey} disabled />
                </Form.Item>
                <Form.Item label="缓存内容">
                  <TextArea
                    value={cacheDetail.value}
                    autoSize={{ minRows: 10, maxRows: 20 }}
                    placeholder="缓存内容将显示在这里"
                  />
                </Form.Item>
                <Form.Item style={{ marginBottom: 0 }}>
                  <Space>
                    <Button type="primary" onClick={handleSaveValue} loading={saving}>
                      保存
                    </Button>
                    <Button onClick={() => form.resetFields()}>重置</Button>
                  </Space>
                </Form.Item>
              </Form>
            ) : (
              <Empty description="请选择一个键查看内容" style={{ marginTop: '40px' }} />
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default CacheList;
