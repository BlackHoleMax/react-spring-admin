import request from '@/utils/request';

export interface UserOnline {
  id: string;
  userId: number;
  username: string;
  nickname: string;
  ip: string;
  location: string;
  browser: string;
  os: string;
  status: string;
  startTime: string;
  lastTime: string;
  expireTime: string;
  onlineMinutes: number;
}

export interface OnlineUserQueryParams {
  current?: number;
  size?: number;
  username?: string;
  ip?: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
  pages: number;
}

export const onlineUserApi = {
  page: async (params: OnlineUserQueryParams): Promise<PageResult<UserOnline>> => {
    const result = await request.get('/system/online/page', { params });
    return result as unknown as PageResult<UserOnline>;
  },

  count: async (): Promise<number> => {
    const result = await request.get('/system/online/count');
    return result as unknown as number;
  },

  getBySessionId: (sessionId: string) => {
    return request.get(`/system/online/${sessionId}`);
  },

  kickout: (sessionId: string) => {
    return request.delete(`/system/online/kickout/${sessionId}`);
  },

  batchKickout: (sessionIds: string[]) => {
    return request.delete('/system/online/kickout/batch', { data: sessionIds });
  },

  kickoutByUserId: (userId: number) => {
    return request.delete(`/system/online/kickout/user/${userId}`);
  },

  cleanExpiredSessions: () => {
    return request.delete('/system/online/clean');
  },
};
