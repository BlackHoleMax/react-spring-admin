import request from '../utils/request';
import type { UpdatePasswordParams, UpdateProfileParams, UserProfile } from '@/types';

export const getProfile = (): Promise<UserProfile> => {
  return request.get('/profile');
};

export const updateAvatar = (avatar: string) => {
  return request.put('/profile/avatar', { avatar });
};

export const updatePassword = (data: UpdatePasswordParams) => {
  return request.put('/profile/password', data);
};

export const updateProfile = (data: UpdateProfileParams): Promise<UserProfile> => {
  return request.put('/profile', data);
};
