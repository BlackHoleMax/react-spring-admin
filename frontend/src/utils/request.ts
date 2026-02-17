import axios from 'axios';

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

let isRedirecting = false;

const redirectToLogin = (errorMsg: string) => {
  if (isRedirecting) return;
  isRedirecting = true;

  localStorage.removeItem('token');
  // 静默重定向，不显示弹窗
  console.warn('认证失效，正在重定向到登录页面:', errorMsg);

  setTimeout(() => {
    window.location.href = '/login';
  }, 1000);
};

instance.interceptors.response.use(
  (response) => {
    // 如果是 blob 类型的响应，直接返回数据
    if (response.config.responseType === 'blob') {
      return response.data;
    }

    const { code, msg, data } = response.data;
    if (code === 200) {
      return data;
    } else if (code === 401) {
      redirectToLogin(msg || '认证已失效，请重新登录');
      return Promise.reject(new Error(msg || '认证已失效'));
    } else {
      return Promise.reject(new Error(msg || '请求失败'));
    }
  },
  (error) => {
    console.error('请求错误详情:', error);

    // 如果是 blob 类型的响应错误，直接返回错误信息
    if (error.config?.responseType === 'blob') {
      return Promise.reject(error);
    }

    // JSON解析错误
    if (error.message && (error.message.includes('JSON') || error.message.includes('JSON.parse'))) {
      console.error('JSON解析错误 - 响应数据:', error.response?.data);
      console.error('JSON解析错误 - 响应文本:', error.response?.request?.response);
      return Promise.reject(new Error('服务器返回数据格式错误'));
    }

    if (error.response) {
      const { status, data } = error.response;
      if (status === 401) {
        redirectToLogin(data?.msg || '未授权，请重新登录');
        return Promise.reject(new Error(data?.msg || '未授权，请重新登录'));
      } else {
        const errorMsg = data?.msg || getStatusText(status);
        return Promise.reject(new Error(errorMsg));
      }
    } else {
      return Promise.reject(new Error('网络错误'));
    }
  }
);

const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    403: '没有权限访问',
    404: '请求的资源不存在',
    500: '服务器错误',
    502: '网关错误',
    503: '服务不可用',
    504: '网关超时',
  };
  return statusMap[status] || '请求失败';
};

export default instance;
