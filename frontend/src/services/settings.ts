import request from '../utils/request';

export interface CaptchaSettings {
  loginEnabled: boolean;
}

export const getCaptchaSettings = (): Promise<CaptchaSettings> => {
  return request.get('/system/config/captcha');
};

export const updateCaptchaSettings = (settings: CaptchaSettings): Promise<void> => {
  const configRequest = {
    configKey: 'captcha.login.enabled',
    configValue: String(settings.loginEnabled),
    configName: '登录验证码开关',
    remark: '控制登录时是否需要验证码验证',
  };
  return request.post('/system/config', configRequest);
};
