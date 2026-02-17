import React, { useEffect, useState } from 'react';
import { Alert, Card, Col, Progress, Row, Space, Statistic, Table, Tag } from 'antd';
import {
  CheckCircleOutlined,
  CloseCircleOutlined,
  CloudServerOutlined,
  DashboardOutlined,
  ThunderboltOutlined,
  WarningOutlined,
} from '@ant-design/icons';
import { getHealth, getJvmInfo, getMetrics, getSystemInfo } from '@/services/monitor.ts';
import type { JvmInfo, MonitorHealth, MonitorMetrics, SystemInfo } from '@/types';

const Monitor: React.FC = () => {
  const [health, setHealth] = useState<MonitorHealth | null>(null);
  const [metrics, setMetrics] = useState<MonitorMetrics | null>(null);
  const [jvmInfo, setJvmInfo] = useState<JvmInfo | null>(null);
  const [systemInfo, setSystemInfo] = useState<SystemInfo | null>(null);
  const [loading, setLoading] = useState(true);

  const fetchData = async (isInitial = false) => {
    if (isInitial) {
      setLoading(true);
    }
    try {
      const [healthRes, metricsRes, jvmRes, systemRes] = await Promise.all([
        getHealth(),
        getMetrics(),
        getJvmInfo(),
        getSystemInfo(),
      ]);

      setHealth(healthRes);
      setMetrics(metricsRes);
      setJvmInfo(jvmRes);
      setSystemInfo(systemRes);
    } catch {
      console.error('操作失败');
    } finally {
      if (isInitial) {
        setLoading(false);
      }
    }
  };

  useEffect(() => {
    fetchData(true);
    const interval = setInterval(() => fetchData(false), 5000);
    return () => clearInterval(interval);
  }, []);

  const formatBytes = (bytes: number) => {
    if (!bytes) return '0 B';
    const k = 1024;
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + ' ' + sizes[i];
  };

  const formatUptime = (ms: number) => {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `${days}天 ${hours % 24}小时`;
    if (hours > 0) return `${hours}小时 ${minutes % 60}分钟`;
    if (minutes > 0) return `${minutes}分钟 ${seconds % 60}秒`;
    return `${seconds}秒`;
  };

  const getHealthStatus = (status: string) => {
    switch (status?.toUpperCase()) {
      case 'UP':
        return (
          <Tag icon={<CheckCircleOutlined />} color="success">
            正常
          </Tag>
        );
      case 'DOWN':
        return (
          <Tag icon={<CloseCircleOutlined />} color="error">
            异常
          </Tag>
        );
      case 'OUT_OF_SERVICE':
        return (
          <Tag icon={<WarningOutlined />} color="warning">
            停止服务
          </Tag>
        );
      default:
        return <Tag color="default">未知</Tag>;
    }
  };

  const healthColumns = [
    {
      title: '组件',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => getHealthStatus(status),
    },
    {
      title: '详情',
      dataIndex: 'details',
      key: 'details',
      render: (details: unknown) => (details ? JSON.stringify(details) : '-'),
    },
  ];

  const healthData = health?.components
    ? Object.entries(health.components).map(([name, component]) => ({
        key: name,
        name,
        status: component.status,
        details: component.details,
      }))
    : [];

  const cpuUsage =
    metrics && typeof metrics['process.cpu.usage'] === 'number'
      ? metrics['process.cpu.usage'] * 100
      : 0;
  const systemCpuUsage =
    metrics && typeof metrics['system.cpu.usage'] === 'number'
      ? metrics['system.cpu.usage'] * 100
      : 0;
  const memoryUsage = jvmInfo?.heapMemory
    ? (jvmInfo.heapMemory.used / jvmInfo.heapMemory.max) * 100
    : 0;

  return (
    <div style={{ padding: '24px' }}>
      <Space orientation="vertical" size="large" style={{ width: '100%' }}>
        <Alert
          title="系统监控面板"
          description="实时监控系统运行状态、JVM性能指标和资源使用情况，数据每5秒自动刷新"
          type="info"
          showIcon
        />

        <Card title="健康状态" loading={loading}>
          <Row gutter={16}>
            <Col span={6}>
              <Statistic
                title="系统状态"
                value={health?.status || '-'}
                prefix={health?.status === 'UP' ? <CheckCircleOutlined /> : <CloseCircleOutlined />}
                styles={{ content: { color: health?.status === 'UP' ? '#3f8600' : '#cf1322' } }}
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="运行时间"
                value={metrics?.uptime ? formatUptime(metrics.uptime) : '-'}
                prefix={<ThunderboltOutlined />}
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="活动线程"
                value={metrics?.['jvm.threads.live'] || 0}
                suffix={`/ ${metrics?.['jvm.threads.peak'] || 0}`}
                prefix={<DashboardOutlined />}
              />
            </Col>
            <Col span={6}>
              <Statistic
                title="可用处理器"
                value={systemInfo?.availableProcessors || 0}
                prefix={<CloudServerOutlined />}
              />
            </Col>
          </Row>

          <div style={{ marginTop: 24 }}>
            <Table
              columns={healthColumns}
              dataSource={healthData}
              pagination={false}
              size="small"
            />
          </div>
        </Card>

        <Row gutter={16}>
          <Col span={8}>
            <Card title="CPU使用率" loading={loading}>
              <Progress
                type="dashboard"
                percent={Number(cpuUsage.toFixed(2))}
                format={(percent) => `${percent}%`}
                strokeColor={{
                  '0%': '#108ee9',
                  '100%': '#87d068',
                }}
              />
              <div style={{ marginTop: 16, textAlign: 'center' }}>
                <div>进程CPU: {cpuUsage.toFixed(2)}%</div>
                <div>系统CPU: {systemCpuUsage.toFixed(2)}%</div>
              </div>
            </Card>
          </Col>

          <Col span={8}>
            <Card title="内存使用率" loading={loading}>
              <Progress
                type="dashboard"
                percent={Number(memoryUsage.toFixed(2))}
                format={(percent) => `${percent}%`}
                strokeColor={{
                  '0%': '#108ee9',
                  '100%': '#87d068',
                }}
              />
              <div style={{ marginTop: 16, textAlign: 'center' }}>
                <div>已用: {jvmInfo ? formatBytes(jvmInfo.heapMemory.used) : '-'}</div>
                <div>最大: {jvmInfo ? formatBytes(jvmInfo.heapMemory.max) : '-'}</div>
              </div>
            </Card>
          </Col>

          <Col span={8}>
            <Card title="系统负载" loading={loading}>
              <Progress
                type="dashboard"
                percent={
                  systemInfo && systemInfo.systemLoadAverage >= 0
                    ? Math.min(
                        (systemInfo.systemLoadAverage / systemInfo.availableProcessors) * 100,
                        100
                      )
                    : 0
                }
                format={(percent) =>
                  systemInfo && systemInfo.systemLoadAverage >= 0
                    ? `${percent?.toFixed(2)}%`
                    : '不可用'
                }
                strokeColor={{
                  '0%': '#108ee9',
                  '100%': '#87d068',
                }}
              />
              <div style={{ marginTop: 16, textAlign: 'center' }}>
                <div>
                  负载:{' '}
                  {systemInfo && systemInfo.systemLoadAverage >= 0
                    ? systemInfo.systemLoadAverage.toFixed(2)
                    : '不可用'}
                </div>
                <div>处理器: {systemInfo?.availableProcessors || '-'}</div>
              </div>
            </Card>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col span={12}>
            <Card title="JVM信息" loading={loading}>
              <Space orientation="vertical" style={{ width: '100%' }}>
                <Row>
                  <Col span={8}>JVM名称:</Col>
                  <Col span={16}>{jvmInfo?.name || '-'}</Col>
                </Row>
                <Row>
                  <Col span={8}>JVM版本:</Col>
                  <Col span={16}>{jvmInfo?.version || '-'}</Col>
                </Row>
                <Row>
                  <Col span={8}>JVM厂商:</Col>
                  <Col span={16}>{jvmInfo?.vendor || '-'}</Col>
                </Row>
                <Row>
                  <Col span={8}>堆内存:</Col>
                  <Col span={16}>
                    {jvmInfo
                      ? `${formatBytes(jvmInfo.heapMemory.used)} / ${formatBytes(
                          jvmInfo.heapMemory.max
                        )}`
                      : '-'}
                  </Col>
                </Row>
                <Row>
                  <Col span={8}>非堆内存:</Col>
                  <Col span={16}>
                    {jvmInfo
                      ? `${formatBytes(jvmInfo.nonHeapMemory.used)} / ${formatBytes(
                          jvmInfo.nonHeapMemory.max || jvmInfo.nonHeapMemory.committed
                        )}`
                      : '-'}
                  </Col>
                </Row>
              </Space>
            </Card>
          </Col>

          <Col span={12}>
            <Card title="系统信息" loading={loading}>
              <Space orientation="vertical" style={{ width: '100%' }}>
                <Row>
                  <Col span={8}>操作系统:</Col>
                  <Col span={16}>{systemInfo?.name || '-'}</Col>
                </Row>
                <Row>
                  <Col span={8}>系统版本:</Col>
                  <Col span={16}>{systemInfo?.version || '-'}</Col>
                </Row>
                <Row>
                  <Col span={8}>系统架构:</Col>
                  <Col span={16}>{systemInfo?.arch || '-'}</Col>
                </Row>
                <Row>
                  <Col span={8}>总内存:</Col>
                  <Col span={16}>{systemInfo ? formatBytes(systemInfo.totalMemory) : '-'}</Col>
                </Row>
                <Row>
                  <Col span={8}>可用内存:</Col>
                  <Col span={16}>{systemInfo ? formatBytes(systemInfo.freeMemory) : '-'}</Col>
                </Row>
              </Space>
            </Card>
          </Col>
        </Row>
      </Space>
    </div>
  );
};

export default Monitor;
