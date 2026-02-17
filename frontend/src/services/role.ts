import request from '../utils/request';
import type { PageResult, Role, RoleQuery } from '@/types';

export const getRoleList = (params: RoleQuery): Promise<PageResult<Role>> => {
  return request.get('/system/role/list', { params });
};

export const getRoleById = (id: number): Promise<Role> => {
  return request.get(`/system/role/${id}`);
};

export const createRole = (data: Partial<Role>): Promise<void> => {
  return request.post('/system/role', data);
};

export const updateRole = (data: Partial<Role>): Promise<void> => {
  return request.put('/system/role', data);
};

export const deleteRole = (id: number): Promise<void> => {
  return request.delete(`/system/role/${id}`);
};

export const getRolePerms = (roleId: number): Promise<number[]> => {
  return request.get(`/system/role/${roleId}/perms`);
};

export const saveRolePerms = (roleId: number, permIds: number[]): Promise<void> => {
  return request.post('/system/role/perms', { roleId, permIds });
};

export const getRoleMenus = (roleId: number): Promise<number[]> => {
  return request.get(`/system/role/${roleId}/menus`);
};

export const saveRoleMenus = (roleId: number, menuIds: number[]): Promise<void> => {
  return request.post('/system/role/menus', { roleId, menuIds });
};

export const batchDeleteRole = (ids: number[]): Promise<void> => {
  return request.delete('/system/role/batch', { data: ids });
};

export const importRole = (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/system/role/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const downloadRoleTemplate = (): Promise<Blob> => {
  return request.get('/system/role/template', {
    responseType: 'blob',
  });
};
