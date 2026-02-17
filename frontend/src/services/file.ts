import request from '../utils/request';

/**
 * 文件查询参数
 */
export interface FileQuery {
  fileName?: string;
  originalName?: string;
  fileSuffix?: string;
  createTimeStart?: string;
  createTimeEnd?: string;
  storageProvider?: string;
}

/**
 * 文件信息
 */
export interface FileInfo {
  id: number;
  fileName: string;
  originalName: string;
  fileSuffix: string;
  filePath: string;
  fileUrl: string;
  fileSize: number;
  fileType: string;
  fileCategory: string;
  storageProvider: string;
  bucketName: string;
  uploadUserId: number;
  uploadUserName: string;
  createTime: string;
  updateTime: string;
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

/**
 * 文件服务
 */
export const fileService = {
  /**
   * 分页查询文件列表
   */
  getFilePage: (current: number, size: number, query?: FileQuery) => {
    return request.get<PageResponse<FileInfo>>('/system/file/page', {
      params: { current, size, ...query },
    });
  },

  /**
   * 根据ID删除文件
   */
  deleteFile: (id: number) => {
    return request.delete(`/system/file/${id}`);
  },

  /**
   * 批量删除文件
   */
  deleteFiles: (ids: number[]) => {
    return request.delete('/system/file/batch', { data: ids });
  },
};

/**
 * 存储配置信息
 */
export interface StorageConfigInfo {
  id: number;
  configKey: string;
  endpoint: string;
  domain: string | null;
  bucketName: string;
  prefix: string | null;
  region: string | null;
  bucketAcl: string;
  accessKey: string;
  secretKey: string;
  status: number | null;
  isHttps: number;
  storageProvider: string;
  isDefault: number;
  remark: string;
  createBy: string | null;
  createTime: string;
  updateBy: string | null;
  updateTime: string;
}

/**
 * 存储配置服务
 */
export const storageConfigService = {
  /**
   * 分页查询存储配置列表
   */
  getConfigPage: (current: number, size: number) => {
    return request.get<PageResponse<StorageConfigInfo>>('/system/storage-config/page', {
      params: { current, size },
    });
  },

  /**
   * 根据ID获取存储配置详情
   */
  getConfigById: (id: number) => {
    return request.get<StorageConfigInfo>(`/system/storage-config/${id}`);
  },

  /**
   * 新增存储配置
   */
  saveConfig: (data: Partial<StorageConfigInfo>) => {
    return request.post('/system/storage-config', data);
  },

  /**
   * 更新存储配置
   */
  updateConfig: (data: Partial<StorageConfigInfo>) => {
    return request.put('/system/storage-config', data);
  },

  /**
   * 删除存储配置
   */
  deleteConfig: (id: number) => {
    return request.delete(`/system/storage-config/${id}`);
  },

  /**
   * 设置默认配置
   */
  setDefaultConfig: (id: number) => {
    return request.put(`/system/storage-config/default/${id}`);
  },

  /**
   * 获取默认配置
   */
  getDefaultConfig: () => {
    return request.get<StorageConfigInfo>('/system/storage-config/default');
  },
};
