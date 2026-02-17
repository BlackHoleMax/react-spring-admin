import request from '../utils/request';
import type { Dict, DictItem, DictQuery, PageResult } from '@/types';

export const getDictList = (params: DictQuery): Promise<PageResult<Dict>> => {
  return request.get('/system/dict/list', { params });
};

export const getAllEnabledDicts = (): Promise<Dict[]> => {
  return request.get('/system/dict/all');
};

export const getDictById = (id: number): Promise<Dict> => {
  return request.get(`/system/dict/${id}`);
};

export const getDictItemsByCode = (dictCode: string): Promise<DictItem[]> => {
  return request.get(`/system/dict/code/${dictCode}/items`);
};

export const createDict = (data: Partial<Dict>): Promise<void> => {
  return request.post('/system/dict', data);
};

export const updateDict = (data: Partial<Dict>): Promise<void> => {
  return request.put('/system/dict', data);
};

export const deleteDict = (id: number): Promise<void> => {
  return request.delete(`/system/dict/${id}`);
};

export const getDictItemList = (dictId: number): Promise<DictItem[]> => {
  return request.get(`/system/dict/${dictId}/items`);
};

export const createDictItem = (data: Partial<DictItem>): Promise<void> => {
  return request.post('/system/dict/item', data);
};

export const updateDictItem = (data: Partial<DictItem>): Promise<void> => {
  return request.put('/system/dict/item', data);
};

export const deleteDictItem = (id: number): Promise<void> => {
  return request.delete(`/system/dict/item/${id}`);
};

export const batchDeleteDict = (ids: number[]): Promise<void> => {
  return request.delete('/system/dict/batch', { data: ids });
};

export const importDict = (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post('/system/dict/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

export const downloadDictTemplate = (): Promise<void> => {
  return request.get('/system/dict/template', {
    responseType: 'blob',
  });
};
