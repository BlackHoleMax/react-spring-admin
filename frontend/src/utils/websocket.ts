import type { Notice } from '@/types/notice';

type ConnectionState = 'connecting' | 'connected' | 'disconnected' | 'error';

interface WebSocketManagerOptions {
  onMessage?: (notice: Notice) => void;
  onStateChange?: (state: ConnectionState) => void;
  onError?: (error: Error) => void;
}

class WebSocketManager {
  private ws: WebSocket | null = null;
  private reconnectTimer: NodeJS.Timeout | null = null;
  private connectTimer: NodeJS.Timeout | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 3000;
  private connectTimeout = 10000;
  private userId: number | null = null;
  private messageHandler: ((notice: Notice) => void) | null = null;
  private stateChangeHandler: ((state: ConnectionState) => void) | null = null;
  private errorHandler: ((error: Error) => void) | null = null;
  private currentState: ConnectionState = 'disconnected';
  private isManualDisconnect = false;

  connect(userId: number, options: WebSocketManagerOptions = {}) {
    if (this.ws?.readyState === WebSocket.OPEN) {
      return;
    }

    this.userId = userId;
    this.messageHandler = options.onMessage || null;
    this.stateChangeHandler = options.onStateChange || null;
    this.errorHandler = options.onError || null;
    this.isManualDisconnect = false;
    this.reconnectAttempts = 0;

    this.updateState('connecting');

    const token = localStorage.getItem('token');
    // 开发环境直接连接到后端，生产环境使用当前域名
    const isDev = import.meta.env.DEV;
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const host = isDev ? 'localhost:8080' : window.location.host;
    const wsUrl = `${protocol}//${host}/ws/notice?token=${encodeURIComponent(token || '')}`;

    try {
      this.ws = new WebSocket(wsUrl);

      this.connectTimer = setTimeout(() => {
        if (this.ws?.readyState !== WebSocket.OPEN) {
          this.handleConnectError(new Error('连接超时'));
        }
      }, this.connectTimeout);

      this.ws.onopen = () => {
        this.reconnectAttempts = 0;
        this.updateState('connected');
        if (this.connectTimer) {
          clearTimeout(this.connectTimer);
          this.connectTimer = null;
        }
      };

      this.ws.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);

          // 处理连接成功消息
          if (data.type === 'connected') {
            return;
          }

          // 处理心跳响应
          if (data.type === 'pong') {
            return;
          }

          // 处理通知消息
          if (this.messageHandler) {
            this.messageHandler(data as Notice);
          }
        } catch (error) {
          console.error('[WebSocket] 解析消息失败:', error);
        }
      };

      this.ws.onerror = () => {
        this.handleConnectError(new Error('WebSocket连接错误'));
      };

      this.ws.onclose = () => {
        if (!this.isManualDisconnect) {
          this.handleReconnect();
        } else {
          this.updateState('disconnected');
        }
      };
    } catch (error) {
      console.error('[WebSocket] 创建连接失败', error);
      this.handleConnectError(error instanceof Error ? error : new Error('未知错误'));
    }
  }

  disconnect() {
    this.isManualDisconnect = true;
    this.clearTimers();
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
    this.reconnectAttempts = 0;
    this.updateState('disconnected');
  }

  reconnect() {
    if (this.userId && this.messageHandler) {
      this.reconnectAttempts = 0;
      this.clearTimers();
      const options: WebSocketManagerOptions = {
        onMessage: this.messageHandler,
      };
      if (this.stateChangeHandler) {
        options.onStateChange = this.stateChangeHandler;
      }
      if (this.errorHandler) {
        options.onError = this.errorHandler;
      }
      this.connect(this.userId, options);
    }
  }

  getState(): ConnectionState {
    return this.currentState;
  }

  private clearTimers() {
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    if (this.connectTimer) {
      clearTimeout(this.connectTimer);
      this.connectTimer = null;
    }
  }

  private updateState(state: ConnectionState) {
    this.currentState = state;
    if (this.stateChangeHandler) {
      this.stateChangeHandler(state);
    }
  }

  private handleConnectError(error: Error) {
    this.updateState('error');
    if (this.errorHandler) {
      this.errorHandler(error);
    }
    this.clearTimers();
    if (!this.isManualDisconnect) {
      this.handleReconnect();
    }
  }

  private handleReconnect() {
    if (
      this.reconnectAttempts < this.maxReconnectAttempts &&
      this.userId &&
      !this.isManualDisconnect
    ) {
      this.reconnectAttempts++;
      this.updateState('connecting');
      this.reconnectTimer = setTimeout(() => {
        const options: WebSocketManagerOptions = {};
        if (this.messageHandler) {
          options.onMessage = this.messageHandler;
        }
        if (this.stateChangeHandler) {
          options.onStateChange = this.stateChangeHandler;
        }
        if (this.errorHandler) {
          options.onError = this.errorHandler;
        }
        this.connect(this.userId!, options);
      }, this.reconnectDelay);
    } else {
      this.updateState('disconnected');
    }
  }
}

export const websocketManager = new WebSocketManager();
