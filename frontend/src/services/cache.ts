import request from '@/utils/request';
import type { CacheInfo } from '@/types';

export interface CacheInfoResponse {
  info: {
    redis_version: string;
    redis_mode: string;
    os: string;
    arch_bits: string;
    gcc_version: string;
    uptime_in_seconds: number;
    uptime_in_days: number;
    tcp_port: number;
    bind: string;
    connected_clients: number;
    used_memory: number;
    used_memory_human: string;
    used_memory_peak: number;
    used_memory_peak_human: string;
    maxmemory: number;
    maxmemory_human: string;
    used_cpu_sys: number;
    used_cpu_user: number;
    used_cpu_sys_children: number;
    used_cpu_user_children: number;
    aof_enabled: number;
    rdb_last_bgsave_time_sec?: number;
    instantaneous_input_kbps: number;
    instantaneous_output_kbps: number;
  };
  dbSize: number;
  commandStats: CommandStat[];
}

export interface CommandStat {
  name: string;
  calls: number;
  usec: number;
  usecPerCall: number;
}

export interface CacheKeyInfo {
  key: string;
  type: string;
  size: number;
  ttl: number;
}

export const cacheApi = {
  // 获取缓存监控信息
  getInfo: async (): Promise<CacheInfo> => {
    const result = await request.get('/monitor/cache/info');
    return result as unknown as CacheInfo;
  },

  // 获取缓存键列表
  getKeys: async (pattern: string = '*'): Promise<string[]> => {
    const result = await request.get('/monitor/cache/keys', { params: { pattern } });
    return result as unknown as string[];
  },

  // 获取键详细信息
  getKeyDetail: async (
    key: string
  ): Promise<{
    value: unknown;
    type: string;
    ttl: number;
  }> => {
    return request.get(`/monitor/cache/detail/${key}`);
  },

  // 获取键值
  getValue: async (key: string): Promise<unknown> => {
    return request.get(`/monitor/cache/value/${key}`);
  },

  // 删除键
  deleteKey: async (key: string): Promise<void> => {
    return request.delete(`/monitor/cache/key/${key}`);
  },

  // 设置过期时间
  setTtl: async (key: string, ttl: number): Promise<void> => {
    return request.put(`/monitor/cache/ttl/${key}`, { ttl });
  },

  // 清空数据库
  clearDb: async (): Promise<void> => {
    return request.delete('/monitor/cache/clear');
  },

  // 删除指定模式的键
  deleteKeysByPattern: async (pattern: string): Promise<number> => {
    const result = await request.delete('/monitor/cache/keys', { params: { pattern } });
    return result as unknown as number;
  },
};
