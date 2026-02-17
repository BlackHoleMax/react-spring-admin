import request from '../utils/request';
import type { Job, PageResult } from '@/types';

export const getJobList = (params: any): Promise<PageResult<Job>> => {
  return request.get('/system/job/list', { params });
};

export const getJobById = (jobId: number): Promise<Job> => {
  return request.get(`/system/job/${jobId}`);
};

export const createJob = (data: Partial<Job>): Promise<void> => {
  return request.post('/system/job', data);
};

export const updateJob = (data: Partial<Job>): Promise<void> => {
  return request.put('/system/job', data);
};

export const deleteJob = (jobId: number): Promise<void> => {
  return request.delete(`/system/job/${jobId}`);
};

export const batchDeleteJob = (jobIds: number[]): Promise<void> => {
  return request.delete(`/system/job/batch/${jobIds.join(',')}`);
};

export const changeJobStatus = (data: Partial<Job>): Promise<void> => {
  return request.put('/system/job/changeStatus', data);
};

export const runJob = (data: Partial<Job>): Promise<void> => {
  return request.put('/system/job/run', data);
};

export const checkCronExpression = (cronExpression: string): Promise<boolean> => {
  return request.get('/system/job/checkCronExpression', { params: { cronExpression } });
};

export const exportJob = (params: any): Promise<Blob> => {
  return request.post('/system/job/export', params, {
    responseType: 'blob',
  });
};

export const importJob = (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/system/job/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
