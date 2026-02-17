import request from '../utils/request';
import type { PageResult } from '@/types';

export interface Notice {
  readStatus: any;
  id?: number;
  title: string;
  content: string;
  type: number;
  targetType: number;
  targetRoles?: number[];
  priority: number;
  startTime?: string;
  endTime?: string;
  status?: number;
  publishTime?: string;
  publisherId?: number;
  publisherName?: string;
  readCount?: number;
  totalCount?: number;
  readRate?: string;
  createTime?: string;
  updateTime?: string;
  typeName?: string;
  targetTypeName?: string;
  priorityName?: string;
  statusName?: string;
}

export interface NoticeQuery {
  current?: number;
  size?: number;
  title?: string;
  type?: number;
  status?: number;
  priority?: number;
}

export interface MyNoticeQuery {
  current?: number;
  size?: number;
  title?: string;
  type?: number;
  readStatus?: number;
  priority?: number;
}

export const getNoticeList = (params: NoticeQuery): Promise<PageResult<Notice>> => {
  return request.get('/system/notice/list', { params });
};

export const getNoticeById = (id: number): Promise<Notice> => {
  return request.get(`/system/notice/${id}`);
};

export const createNotice = (data: Partial<Notice>): Promise<void> => {
  return request.post('/system/notice', data);
};

export const updateNotice = (data: Partial<Notice>): Promise<void> => {
  return request.put('/system/notice', data);
};

export const deleteNotice = (id: number): Promise<void> => {
  return request.delete(`/system/notice/${id}`);
};

export const batchDeleteNotice = (ids: number[]): Promise<void> => {
  return request.delete('/system/notice/batch', { data: ids });
};

export const publishNotice = (id: number): Promise<void> => {
  return request.post(`/system/notice/publish/${id}`);
};

export const revokeNotice = (id: number): Promise<void> => {
  return request.post(`/system/notice/revoke/${id}`);
};

export const getMyNoticeList = (params: MyNoticeQuery): Promise<PageResult<Notice>> => {
  return request.get('/user/notice/list', { params });
};

export const getUnreadCount = (): Promise<number> => {
  return request.get('/user/notice/unread-count');
};

export const markAsRead = (noticeId: number): Promise<void> => {
  return request.post(`/user/notice/read/${noticeId}`);
};

export const batchMarkAsRead = (ids: number[]): Promise<void> => {
  return request.post('/user/notice/read/batch', ids);
};

export const markAllAsRead = (): Promise<void> => {
  return request.post('/user/notice/read/all');
};
