import request from '../utils/request';
import type { PageResult, Role, User, UserQuery } from '@/types';

export const getUserList = (params: UserQuery): Promise<PageResult<User>> => {
  return request.get('/user/list', { params });
};

export const getUserById = (id: number): Promise<User> => {
  return request.get(`/user/${id}`);
};

export const createUser = (data: Partial<User>): Promise<void> => {
  return request.post('/user', data);
};

export const updateUser = (data: Partial<User>): Promise<void> => {
  return request.put('/user', data);
};

export const deleteUser = (id: number): Promise<void> => {
  return request.delete(`/user/${id}`);
};

export const batchDeleteUser = (ids: number[]): Promise<void> => {
  return request.delete('/user/batch', { data: ids });
};

export const resetPassword = (id: number): Promise<string> => {
  return request.put(`/user/${id}/reset-password`);
};

export const changePassword = (id: number, password: string): Promise<void> => {
  return request.put(`/user/${id}/change-password`, null, { params: { password } });
};

export const assignRoles = (userId: number, roleIds: number[]): Promise<void> => {
  return request.post('/user/assign-role/assign', { userId, roleIds });
};

export const getUserRoles = (userId: number): Promise<Role[]> => {
  return request.get(`/user/assign-role/user/${userId}`);
};

export const importUser = (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/user/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const downloadUserTemplate = (): Promise<Blob> => {
  return request.get('/user/template', {
    responseType: 'blob',
  });
};
