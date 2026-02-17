import request from '../utils/request';
import type { JobLog, PageResult } from '@/types';

export const getJobLogList = (params: any): Promise<PageResult<JobLog>> => {
  return request.get('/system/job-log/list', { params });
};

export const getJobLogById = (jobLogId: number): Promise<JobLog> => {
  return request.get(`/system/job-log/${jobLogId}`);
};

export const deleteJobLog = (jobLogId: number): Promise<void> => {
  return request.delete(`/system/job-log/${jobLogId}`);
};

export const batchDeleteJobLog = (jobLogIds: number[]): Promise<void> => {
  return request.delete(`/system/job-log/batch/${jobLogIds.join(',')}`);
};

export const cleanJobLog = (): Promise<void> => {
  return request.delete('/system/job-log/clean');
};

export const exportJobLog = (params: any): Promise<Blob> => {
  return request.post('/system/job-log/export', params, {
    responseType: 'blob',
  });
};
