import request from '../utils/request';

interface UserVO {
  id: number;
  username: string;
  nickname: string;
  avatar: string;
  email: string;
  phone: string;
  status: number;
  createTime: string;
  updateTime: string;
}

interface RoleVO {
  id: number;
  name: string;
  code: string;
  sort: number;
  status: number;
  delFlag: number;
  createBy: string;
  createTime: string;
  updateBy: string;
  updateTime: string;
}

interface PermissionVO {
  id: number;
  perm: string;
  name: string;
  menuId: number;
}

interface LoginLogVO {
  id: number;
  userId: number;
  username: string;
  ip: string;
  userAgent: string;
  status: number;
  msg: string;
  loginTime: string;
}

interface IPage<T> {
  size: number;
  current: number;
  total: number;
  records: T[];
  pages: number;
}

export const getUserCount = async (): Promise<number> => {
  try {
    const response: IPage<UserVO> = await request.get('/user/list?current=1&size=1');
    return response.total || 0;
  } catch (error) {
    console.error('Failed to get user count:', error);
    return 0;
  }
};

export const getRoleCount = async (): Promise<number> => {
  try {
    const response: IPage<RoleVO> = await request.get('/system/role/list?current=1&size=1');
    return response.total || 0;
  } catch (error) {
    console.error('Failed to get role count:', error);
    return 0;
  }
};

export const getPermissionCount = async (): Promise<number> => {
  try {
    const response: PermissionVO[] = await request.get('/system/permission/list');
    return Array.isArray(response) ? response.length : 0;
  } catch (error) {
    console.error('Failed to get permission count:', error);
    return 0;
  }
};

export const getLoginLogCount = async (): Promise<number> => {
  try {
    const response: IPage<LoginLogVO> = await request.get(
      '/system/login-log/list?current=1&size=1'
    );
    return response.total || 0;
  } catch (error) {
    console.error('Failed to get login log count:', error);
    return 0;
  }
};

export interface SystemStatus {
  osName: string;
  osVersion: string;
  availableProcessors: number;
  systemLoadAverage: number;
  uptime: number;
  vmVendor: string;
  vmVersion: string;
}

export const getSystemStatus = (): Promise<SystemStatus> => {
  return request.get('/status');
};

export const getTodayLoginCount = async (): Promise<number> => {
  try {
    const response: IPage<LoginLogVO> = await request.get(
      '/system/login-log/list?current=1&size=1'
    );
    return response.total || 0;
  } catch (error) {
    console.error('Failed to get today login count:', error);
    return 0;
  }
};
