import request from '../utils/request';
import type { JvmInfo, MonitorHealth, MonitorMetrics, SystemInfo } from '@/types';

export const getHealth = (): Promise<MonitorHealth> => {
  return request.get('/monitor/health');
};

export const getMetrics = (): Promise<MonitorMetrics> => {
  return request.get('/monitor/metrics');
};

export const getJvmInfo = (): Promise<JvmInfo> => {
  return request.get('/monitor/jvm');
};

export const getSystemInfo = (): Promise<SystemInfo> => {
  return request.get('/monitor/system');
};
