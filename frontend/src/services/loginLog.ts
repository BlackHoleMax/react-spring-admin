import request from '../utils/request';
import type { LoginLog, LoginLogQuery, PageResult } from '@/types';

export const getLoginLogList = (params: LoginLogQuery): Promise<PageResult<LoginLog>> => {
  return request.get('/system/login-log/list', { params });
};

export const getLoginLogById = (id: number): Promise<LoginLog> => {
  return request.get(`/system/login-log/${id}`);
};

export const deleteLoginLog = (id: number): Promise<void> => {
  return request.delete(`/system/login-log/${id}`);
};

export const batchDeleteLoginLog = (ids: number[]): Promise<void> => {
  return request.delete('/system/login-log/batch', { data: ids });
};

export const cleanLoginLog = (): Promise<void> => {
  return request.delete('/system/login-log/clean');
};

export const exportLoginLog = (params: LoginLogQuery): Promise<Blob> => {
  return request.post('/system/login-log/export', params, {
    responseType: 'blob',
  });
};
