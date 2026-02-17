import React, { useEffect, useRef } from 'react';
import type { CaptchaConfig, Result } from '@/types';

interface TianaiCaptchaProps {
  onVerify: (response: Result) => void;
  captchaId?: string;
}

const TianaiCaptcha: React.FC<TianaiCaptchaProps> = ({ onVerify, captchaId = 'captcha-div' }) => {
  const captchaRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const initCaptcha = () => {
      if (!window.initTAC) {
        console.error('Tianai Captcha script not loaded');
        return;
      }

      const captchaConfig: CaptchaConfig = {
        requestCaptchaDataUrl: '/api/captcha/generate',
        validCaptchaUrl: '/api/captcha/check',
        bindEl: `#${captchaId}`,
        validSuccess: (res: Result, _c: unknown, t: { destroyWindow: () => void }) => {
          console.log('验证码验证成功回调...', res);
          t.destroyWindow();

          if (res?.code === 200) {
            console.log('验证码验证成功，通知父组件');
            onVerify(res);
          } else {
            console.error('验证码验证失败，响应格式:', res);
            onVerify({
              code: 500,
              msg: res?.msg || '验证码验证失败',
              data: null,
              timestamp: Date.now(),
              path: '',
            });
          }
        },
      };

      window
        .initTAC('/tac', captchaConfig)
        .then((tac: { init: () => void }) => {
          tac.init();
        })
        .catch((error: Error) => {
          console.error('验证码初始化失败:', error);
        });
    };

    const timer = setTimeout(initCaptcha, 100);

    return () => {
      clearTimeout(timer);
    };
  }, [captchaId, onVerify]);

  return <div id={captchaId} ref={captchaRef} />;
};

export default TianaiCaptcha;
