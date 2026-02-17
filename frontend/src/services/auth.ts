import request from '../utils/request';
import type { LoginParams, LoginResult } from '@/types';

export interface CaptchaLoginParams extends LoginParams {
  captchaVerification?: string;
}

export const login = (data: CaptchaLoginParams): Promise<LoginResult> => {
  return request.post('/login', data);
};

export const logout = (): Promise<void> => {
  return request.delete('/logout');
};
