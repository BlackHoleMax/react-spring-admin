import React, { useEffect, useState } from 'react';
import { Alert, Card, Col, Descriptions, Progress, Row, Statistic, Table, Tag } from 'antd';
import {
  DatabaseOutlined,
  LineChartOutlined,
  ReloadOutlined,
  ThunderboltOutlined,
} from '@ant-design/icons';
import { cacheApi } from '@/services/cache';
import type { CacheInfo, CommandStat } from '@/types';

const CacheMonitor: React.FC = () => {
  const [cacheInfo, setCacheInfo] = useState<CacheInfo | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchCacheInfo = async () => {
    setLoading(true);
    try {
      const data = await cacheApi.getInfo();
      setCacheInfo(data);
    } catch {
      console.error('操作失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCacheInfo();
    const interval = setInterval(fetchCacheInfo, 30000); // 每30秒刷新一次
    return () => clearInterval(interval);
  }, []);

  const formatBytes = (bytes: number) => {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  };

  const commandColumns = [
    {
      title: '命令名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '调用次数',
      dataIndex: 'calls',
      key: 'calls',
      render: (calls: number) => calls.toLocaleString(),
    },
    {
      title: '总耗时(微秒)',
      dataIndex: 'usec',
      key: 'usec',
      render: (usec: number) => usec.toLocaleString(),
    },
    {
      title: '平均耗时(微秒)',
      dataIndex: 'usecPerCall',
      key: 'usecPerCall',
      render: (_: unknown, record: CommandStat) => {
        if (record.calls && record.calls > 0) {
          return (record.usec / record.calls).toFixed(2);
        }
        return '-';
      },
    },
  ];

  const commandData = cacheInfo?.commandStats || [];

  // 计算内存使用率
  const memoryUsage = cacheInfo?.info
    ? (parseFloat(cacheInfo.info.used_memory || '0') /
        (parseFloat(cacheInfo.info.maxmemory || '1') || 1)) *
      100
    : 0;

  return (
    <div style={{ padding: '24px' }}>
      <Row gutter={16} style={{ marginBottom: '16px' }}>
        <Col span={24}>
          <Alert
            title="缓存监控"
            description="实时监控Redis服务器状态、内存使用情况和命令执行统计，数据每30秒自动刷新"
            type="info"
            showIcon
            action={
              <ReloadOutlined
                onClick={fetchCacheInfo}
                style={{ cursor: 'pointer', fontSize: '16px' }}
              />
            }
          />
        </Col>
      </Row>

      <Row gutter={16} style={{ marginBottom: '16px' }}>
        <Col span={6}>
          <Card loading={loading}>
            <Statistic
              title="Redis版本"
              value={cacheInfo?.info?.version || '-'}
              prefix={<DatabaseOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={loading}>
            <Statistic
              title="运行时间(天)"
              value={cacheInfo?.info?.uptime_in_days ? parseInt(cacheInfo.info.uptime_in_days) : 0}
              prefix={<ThunderboltOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={loading}>
            <Statistic
              title="客户端连接数"
              value={
                cacheInfo?.info?.connected_clients ? parseInt(cacheInfo.info.connected_clients) : 0
              }
              prefix={<LineChartOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card loading={loading}>
            <Statistic
              title="Key总数"
              value={cacheInfo?.dbSize || 0}
              prefix={<DatabaseOutlined />}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginBottom: '16px' }}>
        <Col span={12}>
          <Card title="内存使用情况" loading={loading}>
            <Progress
              type="dashboard"
              percent={Number(memoryUsage.toFixed(2))}
              format={(percent) => `${percent}%`}
              strokeColor={{
                '0%': '#108ee9',
                '100%': '#87d068',
              }}
            />
            <div style={{ marginTop: '16px', textAlign: 'center' }}>
              <div>
                已用内存:{' '}
                {cacheInfo?.info ? formatBytes(parseFloat(cacheInfo.info.used_memory || '0')) : '-'}
              </div>
              <div>
                最大内存:{' '}
                {cacheInfo?.info?.maxmemory
                  ? formatBytes(parseFloat(cacheInfo.info.maxmemory))
                  : '未设置'}
              </div>
            </div>
          </Card>
        </Col>

        <Col span={12}>
          <Card title="CPU使用情况" loading={loading}>
            <Descriptions column={1} bordered size="small">
              <Descriptions.Item label="系统CPU">
                {cacheInfo?.info?.used_cpu_sys
                  ? parseFloat(cacheInfo.info.used_cpu_sys).toFixed(2)
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="用户CPU">
                {cacheInfo?.info?.used_cpu_user
                  ? parseFloat(cacheInfo.info.used_cpu_user).toFixed(2)
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="子进程系统CPU">
                {cacheInfo?.info?.used_cpu_sys_children
                  ? parseFloat(cacheInfo.info.used_cpu_sys_children).toFixed(2)
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="子进程用户CPU">
                {cacheInfo?.info?.used_cpu_user_children
                  ? parseFloat(cacheInfo.info.used_cpu_user_children).toFixed(2)
                  : '-'}
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
      </Row>

      <Row gutter={16} style={{ marginBottom: '16px' }}>
        <Col span={24}>
          <Card title="内存详细信息" loading={loading}>
            <Descriptions column={4} bordered size="small">
              <Descriptions.Item label="RSS内存">
                {cacheInfo?.info
                  ? formatBytes(parseFloat(cacheInfo.info.used_memory_rss || '0'))
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="RSS内存(人类可读)">
                {cacheInfo?.info?.used_memory_rss_human || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="峰值内存">
                {cacheInfo?.info
                  ? formatBytes(parseFloat(cacheInfo.info.used_memory_peak || '0'))
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="峰值内存(人类可读)">
                {cacheInfo?.info?.used_memory_peak_human || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Lua引擎内存">
                {cacheInfo?.info
                  ? formatBytes(parseFloat(cacheInfo.info.used_memory_lua || '0'))
                  : '-'}
              </Descriptions.Item>
              <Descriptions.Item label="Lua引擎内存(人类可读)">
                {cacheInfo?.info?.used_memory_lua_human || '-'}
              </Descriptions.Item>
              <Descriptions.Item label="内存碎片率">
                <Tag
                  color={
                    cacheInfo?.info?.mem_fragmentation_ratio &&
                    parseFloat(cacheInfo.info.mem_fragmentation_ratio) > 1.5
                      ? 'orange'
                      : 'green'
                  }
                >
                  {cacheInfo?.info?.mem_fragmentation_ratio || '-'}
                </Tag>
              </Descriptions.Item>
              <Descriptions.Item label="内存分配器">
                {cacheInfo?.info?.mem_allocator || '-'}
              </Descriptions.Item>
            </Descriptions>
          </Card>
        </Col>
      </Row>

      <Card title="Redis详细信息" loading={loading}>
        <Descriptions column={3} bordered size="small">
          <Descriptions.Item label="Redis版本">{cacheInfo?.info?.version || '-'}</Descriptions.Item>
          <Descriptions.Item label="Redis模式">
            <Tag color={cacheInfo?.info?.mode === 'standalone' ? 'blue' : 'green'}>
              {cacheInfo?.info?.mode || '-'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="操作系统">{cacheInfo?.info?.os || '-'}</Descriptions.Item>
          <Descriptions.Item label="架构">{cacheInfo?.info?.arch_bits || '-'}</Descriptions.Item>
          <Descriptions.Item label="GCC版本">
            {cacheInfo?.info?.gcc_version === 'N/A' ? '-' : cacheInfo?.info?.gcc_version || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="运行时间(秒)">
            {cacheInfo?.info?.uptime_in_seconds
              ? parseInt(cacheInfo.info.uptime_in_seconds).toLocaleString()
              : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="TCP端口">{cacheInfo?.info?.tcp_port || '-'}</Descriptions.Item>
          <Descriptions.Item label="监听地址">
            {cacheInfo?.info?.bind === 'N/A' ? '-' : cacheInfo?.info?.bind || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="内存分配器">
            {cacheInfo?.info?.mem_allocator || '-'}
          </Descriptions.Item>
          <Descriptions.Item label="AOF是否开启">
            <Tag color={cacheInfo?.info?.aof_enabled === '1' ? 'green' : 'default'}>
              {cacheInfo?.info?.aof_enabled === '1' ? '是' : '否'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="AOF重写中">
            <Tag color={cacheInfo?.info?.aof_rewrite_in_progress === '1' ? 'orange' : 'default'}>
              {cacheInfo?.info?.aof_rewrite_in_progress === '1' ? '是' : '否'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="RDB最后保存状态">
            <Tag color={cacheInfo?.info?.rdb_last_bgsave_status === 'ok' ? 'green' : 'red'}>
              {cacheInfo?.info?.rdb_last_bgsave_status || '-'}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="网络入口(kbps)">
            {cacheInfo?.info?.instantaneous_input_kbps
              ? parseFloat(cacheInfo.info.instantaneous_input_kbps).toFixed(2)
              : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="网络出口(kbps)">
            {cacheInfo?.info?.instantaneous_output_kbps
              ? parseFloat(cacheInfo.info.instantaneous_output_kbps).toFixed(2)
              : '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Card title="命令执行统计" style={{ marginTop: '16px' }} loading={loading}>
        <Table
          columns={commandColumns}
          dataSource={commandData}
          rowKey="name"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条`,
          }}
        />
      </Card>
    </div>
  );
};

export default CacheMonitor;
