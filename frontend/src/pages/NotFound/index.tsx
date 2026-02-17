import React from 'react';
import { Button, Result } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAppSelector } from '../../store/hooks';
import { ArrowLeftOutlined, HomeOutlined } from '@ant-design/icons';

const NotFound: React.FC = () => {
  const navigate = useNavigate();
  const themeMode = useAppSelector((state) => state.theme.mode);

  const handleGoBack = () => {
    navigate(-1);
  };

  const handleGoHome = () => {
    navigate('/dashboard');
  };

  return (
    <div
      style={{
        height: '100vh',
        width: '100vw',
        overflow: 'hidden',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: themeMode === 'dark' ? '#1a1a1a' : '#f0f2f5',
        padding: '20px',
      }}
    >
      <div
        style={{
          background: themeMode === 'dark' ? '#1f1f1f' : '#ffffff',
          borderRadius: '16px',
          padding: '60px 40px',
          boxShadow:
            themeMode === 'dark'
              ? '0 20px 60px rgba(0, 0, 0, 0.5)'
              : '0 20px 60px rgba(0, 0, 0, 0.1)',
          maxWidth: '600px',
          width: '100%',
          textAlign: 'center',
          animation: 'fadeIn 0.5s ease-in-out',
        }}
      >
        <Result
          status="404"
          title="404"
          subTitle={
            <div
              style={{
                fontSize: '16px',
                color: themeMode === 'dark' ? '#a0a0a0' : '#666',
                marginTop: '10px',
              }}
            >
              抱歉，您访问的页面不存在
            </div>
          }
          extra={
            <div
              style={{
                marginTop: '30px',
                display: 'flex',
                gap: '16px',
                justifyContent: 'center',
                flexWrap: 'wrap',
              }}
            >
              <Button
                type="default"
                icon={<ArrowLeftOutlined />}
                onClick={handleGoBack}
                size="large"
              >
                返回上一页
              </Button>
              <Button type="primary" icon={<HomeOutlined />} onClick={handleGoHome} size="large">
                返回首页
              </Button>
            </div>
          }
        />
        <div
          style={{
            marginTop: '40px',
            padding: '20px',
            background: themeMode === 'dark' ? '#2a2a2a' : '#f5f5f5',
            borderRadius: '8px',
            fontSize: '14px',
            color: themeMode === 'dark' ? '#a0a0a0' : '#666',
            lineHeight: '1.6',
          }}
        >
          <div style={{ marginBottom: '8px', fontWeight: 500 }}>可能的原因：</div>
          <ul style={{ textAlign: 'left', paddingLeft: '20px', margin: 0 }}>
            <li>输入的网址有误</li>
            <li>页面已被删除或移动</li>
            <li>您没有访问该页面的权限</li>
            <li>链接已过期</li>
          </ul>
        </div>
      </div>
      <style>
        {`
                    @keyframes fadeIn {
                        from {
                            opacity: 0;
                            transform: translateY(20px);
                        }
                        to {
                            opacity: 1;
                            transform: translateY(0);
                        }
                    }
                `}
      </style>
    </div>
  );
};

export default NotFound;
