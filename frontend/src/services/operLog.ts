import request from '@/utils/request';
import type { OperLog, OperLogQuery } from '@/types';

export interface OperLogListResult {
  records: OperLog[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

export const getOperLogList = (params: OperLogQuery): Promise<OperLogListResult> => {
  return request.get('/system/oper-log/list', { params });
};

export const deleteOperLog = (id: number): Promise<void> => {
  return request.delete(`/system/oper-log/${id}`);
};

export const batchDeleteOperLog = (ids: number[]): Promise<void> => {
  return request.delete('/system/oper-log/batch', { data: ids });
};

export const cleanOperLog = (): Promise<void> => {
  return request.delete('/system/oper-log/clean');
};

export const exportOperLog = (params?: OperLogQuery): Promise<Blob> => {
  return request.post('/system/oper-log/export', params, {
    responseType: 'blob',
  });
};
