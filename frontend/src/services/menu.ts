import request from '../utils/request';
import type { Menu, MenuQuery } from '@/types';

export const getMenuTree = (params?: MenuQuery): Promise<Menu[]> => {
  return request.get('/system/menu/tree', { params });
};

export const getMenuList = (params?: MenuQuery): Promise<Menu[]> => {
  return request.get('/system/menu/list', { params });
};

export const getMenuById = (id: number): Promise<Menu> => {
  return request.get(`/system/menu/${id}`);
};

export const createMenu = (data: Partial<Menu>): Promise<void> => {
  return request.post('/system/menu', data);
};

export const updateMenu = (data: Partial<Menu>): Promise<void> => {
  return request.put('/system/menu', data);
};

export const deleteMenu = (id: number): Promise<void> => {
  return request.delete(`/system/menu/${id}`);
};

export const getUserMenuTree = (userId: number): Promise<Menu[]> => {
  return request.get(`/system/menu/user/${userId}`);
};

export const getCurrentUserMenus = (): Promise<Menu[]> => {
  return request.get('/system/menu/current');
};

export const batchDeleteMenu = (ids: number[]): Promise<void> => {
  return request.delete('/system/menu/batch', { data: ids });
};

export const importMenu = (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/system/menu/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const downloadMenuTemplate = (): Promise<Blob> => {
  return request.get('/system/menu/template', {
    responseType: 'blob',
  });
};
