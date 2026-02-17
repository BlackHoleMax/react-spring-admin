import request from '@/utils/request';
import type { Permission, PermissionQuery, PageResult } from '@/types';

/**
 * 获取当前用户的权限列表
 */
export const getUserPermissions = (): Promise<string[]> => {
  return request.get('/user/permissions');
};

/**
 * 获取权限树
 */
export const getPermissionTree = (): Promise<Permission[]> => {
  return request.get('/system/permission/tree');
};

/**
 * 获取权限列表（分页）
 */
export const getPermissionList = (params?: PermissionQuery): Promise<PageResult<Permission>> => {
  return request.get('/system/permission/list', { params });
};

/**
 * 根据ID获取权限详情
 */
export const getPermissionById = (id: number): Promise<Permission> => {
  return request.get(`/system/permission/${id}`);
};

/**
 * 创建权限
 */
export const createPermission = (data: Partial<Permission>): Promise<void> => {
  return request.post('/system/permission', data);
};

/**
 * 更新权限
 */
export const updatePermission = (data: Partial<Permission>): Promise<void> => {
  return request.put('/system/permission', data);
};

/**
 * 删除权限
 */
export const deletePermission = (id: number): Promise<void> => {
  return request.delete(`/system/permission/${id}`);
};

/**
 * 批量删除权限
 */
export const batchDeletePermission = (ids: number[]): Promise<void> => {
  return request.delete('/system/permission/batch', { data: ids });
};

/**
 * 导入权限
 */
export const importPermission = (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/system/permission/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

/**
 * 下载权限导入模板
 */
export const downloadPermissionTemplate = (): Promise<Blob> => {
  return request.get('/system/permission/template', {
    responseType: 'blob',
  });
};
