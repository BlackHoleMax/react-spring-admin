import React, { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { App, Button, Card, Checkbox, Modal, Space, Table, Tabs } from 'antd';
import {
  CodeOutlined,
  DownloadOutlined,
  EditOutlined,
  EyeOutlined,
  ImportOutlined,
  ReloadOutlined,
} from '@ant-design/icons';
import {
  getDbTableList,
  getGenTableList,
  importTable,
  previewCode,
  downloadCode,
  batchDownloadCode,
  type GenTable,
} from '@/services/gen';
import CodePreview from '@/components/CodePreview';
import { getLanguageByFilename } from '@/utils/language';

const CodeGen: React.FC = () => {
  const { message } = App.useApp();
  const navigate = useNavigate();
  const [dbTables, setDbTables] = useState<GenTable[]>([]);
  const [genTables, setGenTables] = useState<GenTable[]>([]);
  const [loading, setLoading] = useState(false);
  const [importModalVisible, setImportModalVisible] = useState(false);
  const [previewModalVisible, setPreviewModalVisible] = useState(false);
  const [selectedDbTableNames, setSelectedDbTableNames] = useState<string[]>([]);
  const [previewData, setPreviewData] = useState<Record<string, string>>({});
  const [activeTab, setActiveTab] = useState('Mapper.java');

  const fetchDbTables = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getDbTableList();
      setDbTables(result);
    } catch {
      message.error('获取数据库表列表失败');
    } finally {
      setLoading(false);
    }
  }, [message]);

  const fetchGenTables = useCallback(async () => {
    setLoading(true);
    try {
      const result = await getGenTableList();
      setGenTables(result);
    } catch {
      message.error('获取代码生成配置列表失败');
    } finally {
      setLoading(false);
    }
  }, [message]);

  useEffect(() => {
    fetchDbTables();
    fetchGenTables();
  }, [fetchDbTables, fetchGenTables]);

  const handleImport = async () => {
    if (selectedDbTableNames.length === 0) {
      message.warning('请选择要导入的表');
      return;
    }
    try {
      await importTable(selectedDbTableNames.join(','));
      message.success('导入成功');
      setImportModalVisible(false);
      setSelectedDbTableNames([]);
      await fetchGenTables();
      await fetchDbTables();
    } catch {
      message.error('导入失败');
    }
  };

  const handlePreview = async (tableId: number) => {
    try {
      const result = await previewCode(tableId);
      setPreviewData(result);
      setPreviewModalVisible(true);
    } catch {
      message.error('预览失败');
    }
  };

  const handleDownload = async (tableName: string) => {
    try {
      await downloadCode(tableName);
      message.success('下载成功');
    } catch {
      message.error('下载失败');
    }
  };

  const handleBatchDownload = async () => {
    if (!genTables || genTables.length === 0) {
      message.warning('没有可下载的表');
      return;
    }
    try {
      await batchDownloadCode(genTables.map((t) => t.tableName).join(','));
      message.success('批量下载成功');
    } catch {
      message.error('批量下载失败');
    }
  };

  const handleEdit = (tableId: number) => {
    navigate(`/system-tools/code-gen/edit/${tableId}`);
  };

  const dbTableColumns = [
    {
      title: '选择',
      dataIndex: 'tableName',
      key: 'select',
      width: 60,
      render: (_: unknown, record: GenTable) => (
        <Checkbox
          checked={selectedDbTableNames.includes(record.tableName)}
          onChange={(e) => {
            if (e.target.checked) {
              setSelectedDbTableNames([...selectedDbTableNames, record.tableName]);
            } else {
              setSelectedDbTableNames(
                selectedDbTableNames.filter((name) => name !== record.tableName)
              );
            }
          }}
        />
      ),
    },
    { title: '表名称', dataIndex: 'tableName', key: 'tableName' },
    { title: '表描述', dataIndex: 'tableComment', key: 'tableComment' },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (text: string) => (text ? new Date(text).toLocaleString() : '-'),
    },
  ];

  const genTableColumns = [
    { title: '表名称', dataIndex: 'tableName', key: 'tableName' },
    { title: '表描述', dataIndex: 'tableComment', key: 'tableComment' },
    { title: '实体类名', dataIndex: 'className', key: 'className' },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      render: (text: string) => (text ? new Date(text).toLocaleString() : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 400,
      render: (_: unknown, record: GenTable) => (
        <Space>
          <Button type="link" icon={<EyeOutlined />} onClick={() => handlePreview(record.tableId!)}>
            预览
          </Button>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record.tableId!)}>
            编辑
          </Button>
          <Button
            type="link"
            icon={<DownloadOutlined />}
            onClick={() => handleDownload(record.tableName)}
          >
            生成代码
          </Button>
        </Space>
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
          <h2 style={{ fontSize: '20px', fontWeight: 'bold', margin: 0 }}>代码生成</h2>
          <Space>
            <Button
              icon={<ReloadOutlined />}
              onClick={() => {
                fetchDbTables();
                fetchGenTables();
              }}
            >
              刷新
            </Button>
            <Button
              icon={<ImportOutlined />}
              onClick={async () => {
                await fetchDbTables();
                setImportModalVisible(true);
              }}
            >
              导入表
            </Button>
            <Button type="primary" icon={<CodeOutlined />} onClick={handleBatchDownload}>
              批量生成
            </Button>
          </Space>
        </div>
      </Card>

      <Card title="已导入的表">
        <Table
          columns={genTableColumns}
          dataSource={genTables}
          rowKey="tableId"
          loading={loading}
          pagination={false}
        />
      </Card>

      <Modal
        title="导入表"
        open={importModalVisible}
        onOk={handleImport}
        onCancel={() => {
          setImportModalVisible(false);
          setSelectedDbTableNames([]);
        }}
        width={800}
      >
        <Table
          columns={dbTableColumns}
          dataSource={dbTables}
          rowKey="tableName"
          loading={loading}
          pagination={false}
          scroll={{ y: 400 }}
        />
      </Modal>

      <Modal
        title={
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <EyeOutlined />
            <span>代码预览 - {activeTab}</span>
          </div>
        }
        open={previewModalVisible}
        onCancel={() => setPreviewModalVisible(false)}
        width={1200}
        styles={{ body: { maxHeight: '70vh', overflow: 'auto' } }}
        footer={[
          <Button
            key="copy"
            onClick={() => navigator.clipboard.writeText(previewData[activeTab] || '')}
          >
            复制代码
          </Button>,
          <Button key="close" type="primary" onClick={() => setPreviewModalVisible(false)}>
            关闭
          </Button>,
        ]}
      >
        <Tabs activeKey={activeTab} onChange={setActiveTab}>
          {Object.entries(previewData).map(([key, value]) => (
            <Tabs.TabPane tab={key} key={key}>
              <CodePreview code={value} language={getLanguageByFilename(key)} />
            </Tabs.TabPane>
          ))}
        </Tabs>
      </Modal>
    </div>
  );
};

export default CodeGen;
