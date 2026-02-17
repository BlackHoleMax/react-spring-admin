import React, { useEffect, useState } from 'react';

const ApiDoc: React.FC = () => {
  const [hasError, setHasError] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [canShowIframe, setCanShowIframe] = useState<boolean>(false);

  const token = localStorage.getItem('token');
  const docUrl = token
    ? `http://localhost:8080/doc.html?token=${encodeURIComponent(token)}`
    : 'http://localhost:8080/doc.html';

  useEffect(() => {
    const checkAccess = async (url: string) => {
      try {
        const response = await fetch(url, {
          method: 'GET',
          mode: 'cors',
          credentials: 'include',
        });

        if (response.ok) {
          const responseText = await response.text();
          try {
            const responseData = JSON.parse(responseText);
            if (responseData.code === 401) {
              setHasError(true);
              setIsLoading(false);
            } else {
              setCanShowIframe(true);
              setIsLoading(false);
            }
          } catch {
            setCanShowIframe(true);
            setIsLoading(false);
          }
        } else {
          setHasError(true);
          setIsLoading(false);
        }
      } catch {
        setTimeout(() => {
          if (isLoading) {
            setCanShowIframe(true);
            setIsLoading(false);
          }
        }, 3000);
      }
    };

    checkAccess(docUrl);
  }, [docUrl, isLoading]);

  const handleIframeLoad = () => {
    setIsLoading(false);
    setHasError(false);
  };

  useEffect(() => {
    if (canShowIframe) {
      const adjustIframeHeight = () => {
        const iframe = document.getElementById('apiDocFrame') as HTMLIFrameElement;
        const mainLayout = document.querySelector('.main-layout');
        if (iframe && mainLayout) {
          const layoutHeight = mainLayout.clientHeight;
          const headerHeight = 64;
          iframe.style.height = `${layoutHeight - headerHeight}px`;
        }
      };

      adjustIframeHeight();
      window.addEventListener('resize', adjustIframeHeight);

      return () => {
        window.removeEventListener('resize', adjustIframeHeight);
      };
    }
    return undefined;
  }, [canShowIframe]);

  if (hasError) {
    return (
      <div className="h-full flex items-center justify-center">
        <div className="text-center">
          <div className="text-6xl mb-4">🔒</div>
          <div className="text-xl text-gray-600 mb-2">无法访问API文档</div>
          <div className="text-gray-500 mb-4">您可能没有访问API文档的权限</div>
          <div className="text-sm text-gray-400">请联系管理员获取相应权限，或检查网络连接</div>
        </div>
      </div>
    );
  }

  return (
    <div className="h-full">
      <div className="bg-white rounded-lg shadow-md h-full relative">
        {isLoading && (
          <div className="absolute inset-0 flex items-center justify-center bg-white z-10">
            <div className="text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
              <div className="text-gray-600">正在加载API文档...</div>
            </div>
          </div>
        )}
        {canShowIframe && (
          <iframe
            id="apiDocFrame"
            src={docUrl}
            className="w-full border-0 rounded-lg"
            title="API Documentation"
            style={{ minHeight: '800px', width: '100%' }}
            onLoad={handleIframeLoad}
          />
        )}
      </div>
    </div>
  );
};

export default ApiDoc;
