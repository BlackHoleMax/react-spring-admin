import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Button, DatePicker, Image, Input, message, Popconfirm, Select, Space, Table } from 'antd';
import { DeleteOutlined, ReloadOutlined, SearchOutlined, SettingOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { type FileQuery, fileService } from '../../services/file';

const { RangePicker } = DatePicker;
const { Option } = Select;

/**
 * 文件管理页面
 */
const FileManagement: React.FC = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [dataSource, setDataSource] = useState<any[]>([]);
  const [pagination, setPagination] = useState({ current: 1, pageSize: 10, total: 0 });
  const paginationRef = useRef(pagination);
  const [query, setQuery] = useState<FileQuery>({});
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewImage, setPreviewImage] = useState('');

  /**
   * 加载文件列表
   */
  const loadFileList = useCallback(
    async (current: number, pageSize: number) => {
      setLoading(true);
      try {
        const response = await fileService.getFilePage(current, pageSize, query);
        if (response) {
          const pageData = response as any;
          setDataSource(pageData.records || []);
          const newPagination = {
            current,
            pageSize,
            total: pageData.total || 0,
          };
          setPagination(newPagination);
          paginationRef.current = newPagination;
        }
      } catch {
        message.error('加载文件列表失败');
      } finally {
        setLoading(false);
      }
    },
    [query]
  );

  // 初始加载和查询条件变化时加载
  useEffect(() => {
    loadFileList(paginationRef.current.current, paginationRef.current.pageSize);
  }, [loadFileList]);

  /**
   * 搜索
   */
  const handleSearch = () => {
    const newPagination = { ...pagination, current: 1 };
    setPagination(newPagination);
    paginationRef.current = newPagination;
  };

  /**
   * 重置
   */
  const handleReset = () => {
    setQuery({});
    const newPagination = { ...pagination, current: 1 };
    setPagination(newPagination);
    paginationRef.current = newPagination;
  };

  /**
   * 删除文件
   */
  const handleDelete = async (id: number) => {
    try {
      await fileService.deleteFile(id);
      message.success('删除成功');
      loadFileList(pagination.current, pagination.pageSize);
    } catch {
      message.error('删除失败');
    }
  };

  /**
   * 批量删除
   */
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的文件');
      return;
    }
    try {
      await fileService.deleteFiles(selectedRowKeys as number[]);
      message.success('批量删除成功');
      setSelectedRowKeys([]);
      loadFileList(pagination.current, pagination.pageSize);
    } catch {
      message.error('批量删除失败');
    }
  };

  /**
   * 预览文件
   */
  const handlePreview = (record: any) => {
    if (record.fileCategory === 'image') {
      setPreviewImage(record.fileUrl);
      setPreviewVisible(true);
    } else if (record.fileCategory === 'video') {
      window.open(record.fileUrl, '_blank');
    } else {
      message.info('该文件类型不支持预览');
    }
  };

  /**
   * 格式化文件大小
   */
  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  };

  /**
   * 获取文件展示内容
   */
  const getFileDisplay = (record: any) => {
    if (record.fileCategory === 'image') {
      return (
        <Image
          src={record.fileUrl}
          alt={record.originalName}
          width={60}
          height={60}
          style={{ objectFit: 'cover', cursor: 'zoom-in' }}
          preview={false}
          onClick={() => handlePreview(record)}
        />
      );
    } else if (record.fileCategory === 'video') {
      return (
        <Button
          type="link"
          icon={<span style={{ fontSize: '24px' }}>🎬</span>}
          onClick={() => handlePreview(record)}
        >
          预览
        </Button>
      );
    } else {
      return <span style={{ fontSize: '24px' }}>📄</span>;
    }
  };

  const columns = [
    {
      title: '文件名',
      dataIndex: 'fileName',
      key: 'fileName',
      width: 200,
      ellipsis: true,
    },
    {
      title: '原名',
      dataIndex: 'originalName',
      key: 'originalName',
      width: 200,
      ellipsis: true,
    },
    {
      title: '文件后缀',
      dataIndex: 'fileSuffix',
      key: 'fileSuffix',
      width: 100,
    },
    {
      title: '文件展示',
      dataIndex: 'fileCategory',
      key: 'fileDisplay',
      width: 100,
      render: (_text: string, record: any) => getFileDisplay(record),
    },
    {
      title: '文件大小',
      dataIndex: 'fileSize',
      key: 'fileSize',
      width: 120,
      render: (bytes: number) => formatFileSize(bytes),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      sorter: true,
    },
    {
      title: '上传人',
      dataIndex: 'uploadUserName',
      key: 'uploadUserName',
      width: 120,
    },
    {
      title: '服务商',
      dataIndex: 'storageProvider',
      key: 'storageProvider',
      width: 100,
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right' as const,
      render: (_: any, record: any) => (
        <Space size="small">
          <Popconfirm
            title="确定要删除这个文件吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys,
    onChange: (newSelectedRowKeys: React.Key[]) => {
      setSelectedRowKeys(newSelectedRowKeys);
    },
  };

  return (
    <div style={{ padding: '24px' }}>
      <div style={{ marginBottom: 16 }}>
        <Space wrap>
          <Input
            placeholder="文件名"
            value={query.fileName}
            onChange={(e) => setQuery({ ...query, fileName: e.target.value })}
            style={{ width: 200 }}
          />
          <Input
            placeholder="原名"
            value={query.originalName}
            onChange={(e) => setQuery({ ...query, originalName: e.target.value })}
            style={{ width: 200 }}
          />
          <Input
            placeholder="文件后缀"
            value={query.fileSuffix}
            onChange={(e) => setQuery({ ...query, fileSuffix: e.target.value })}
            style={{ width: 150 }}
          />
          <RangePicker
            showTime
            format="YYYY-MM-DD HH:mm:ss"
            placeholder={['开始时间', '结束时间']}
            onChange={(dates: any) => {
              setQuery({
                ...query,
                createTimeStart: dates?.[0] ? dates[0].format('YYYY-MM-DD HH:mm:ss') : undefined,
                createTimeEnd: dates?.[1] ? dates[1].format('YYYY-MM-DD HH:mm:ss') : undefined,
              });
            }}
          />
          <Select
            placeholder="服务商"
            value={query.storageProvider ?? null}
            onChange={(value) => setQuery({ ...query, storageProvider: value ?? undefined })}
            style={{ width: 150 }}
            allowClear
          >
            <Option value="minio">MinIO</Option>
            <Option value="oss">OSS</Option>
            <Option value="cos">COS</Option>
          </Select>
          <Button type="primary" icon={<SearchOutlined />} onClick={handleSearch}>
            搜索
          </Button>
          <Button icon={<ReloadOutlined />} onClick={handleReset}>
            重置
          </Button>
          <Button
            type="primary"
            danger
            icon={<DeleteOutlined />}
            onClick={handleBatchDelete}
            disabled={selectedRowKeys.length === 0}
          >
            批量删除
          </Button>
          <Button icon={<SettingOutlined />} onClick={() => navigate('/system/file/config')}>
            配置管理
          </Button>
        </Space>
      </div>

      <Table
        rowSelection={rowSelection}
        columns={columns}
        dataSource={dataSource}
        loading={loading}
        pagination={{
          ...pagination,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total) => `共 ${total} 条`,
        }}
        onChange={(newPagination) => {
          const current = newPagination.current || 1;
          const pageSize = newPagination.pageSize || 10;
          const newPaginationState = { ...pagination, current, pageSize };
          setPagination(newPaginationState);
          paginationRef.current = newPaginationState;
          loadFileList(current, pageSize);
        }}
        scroll={{ x: 1200 }}
        rowKey="id"
      />

      {/* 图片预览遮罩层 */}
      {previewVisible && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.9)',
            zIndex: 1000,
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            cursor: 'pointer',
          }}
          onClick={() => setPreviewVisible(false)}
        >
          <img
            src={previewImage}
            alt="图片预览"
            style={{
              maxWidth: '90vw',
              maxHeight: '90vh',
              objectFit: 'contain',
              cursor: 'default',
            }}
            onClick={(e) => e.stopPropagation()}
          />
        </div>
      )}
    </div>
  );
};

export default FileManagement;
