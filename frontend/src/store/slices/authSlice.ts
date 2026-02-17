import { createAsyncThunk, createSlice, type PayloadAction } from '@reduxjs/toolkit';
import { login as loginApi, logout as logoutApi } from '../../services/auth';
import type { LoginParams, User } from '@/types';

interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  token: localStorage.getItem('token'),
  user: null,
  isAuthenticated: !!localStorage.getItem('token'),
  loading: false,
  error: null,
};

export const login = createAsyncThunk(
  'auth/login',
  async (
    credentials: LoginParams & { captchaVerification?: string; rememberMe?: boolean },
    { rejectWithValue }
  ) => {
    try {
      const result = await loginApi(credentials);
      localStorage.setItem('token', result.token);

      // 如果有记住我 token，存储到 localStorage
      if (result.rememberMeToken) {
        localStorage.setItem('rememberMeToken', result.rememberMeToken);
      } else if (!credentials.rememberMe) {
        // 如果用户取消记住我，清除记住我 token
        localStorage.removeItem('rememberMeToken');
      }

      // 获取用户完整信息（包括头像）
      const { getProfile } = await import('../../services/profile');
      const profile = await getProfile();

      return {
        ...result,
        profile,
      };
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : '登录失败');
    }
  }
);

export const logout = createAsyncThunk('auth/logout', async (_, { rejectWithValue, dispatch }) => {
  try {
    await logoutApi();
    localStorage.removeItem('token');
    localStorage.removeItem('rememberMeToken');
    // 重置 WebSocket 状态
    dispatch({ type: 'websocket/resetWebSocketState' });
    return undefined;
  } catch (error) {
    localStorage.removeItem('token');
    localStorage.removeItem('rememberMeToken');
    // 即使退出失败，也要重置 WebSocket 状态
    dispatch({ type: 'websocket/resetWebSocketState' });
    return rejectWithValue(error instanceof Error ? error.message : '登出失败');
  }
});

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setUser: (state, action: PayloadAction<User>) => {
      state.user = action.payload;
    },
    clearAuth: (state) => {
      state.token = null;
      state.user = null;
      state.isAuthenticated = false;
      state.error = null;
      localStorage.removeItem('token');
      localStorage.removeItem('rememberMeToken');
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.loading = false;
        state.token = action.payload.token;
        state.user = {
          id: action.payload.userId,
          username: action.payload.username,
          nickname: action.payload.profile?.nickname || action.payload.nickname || '',
          avatar: action.payload.profile?.avatar || '',
          email: action.payload.profile?.email || '',
          phone: action.payload.profile?.phone || '',
          status: 1,
        };
        state.isAuthenticated = true;
        state.error = null;
      })
      .addCase(login.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
        state.isAuthenticated = false;
      })
      .addCase(logout.pending, (state) => {
        state.loading = true;
      })
      .addCase(logout.fulfilled, (state) => {
        state.loading = false;
        state.token = null;
        state.user = null;
        state.isAuthenticated = false;
        state.error = null;
      })
      .addCase(logout.rejected, (state) => {
        state.loading = false;
        state.token = null;
        state.user = null;
        state.isAuthenticated = false;
        state.error = null;
      });
  },
});

export const { setUser, clearAuth, clearError } = authSlice.actions;
export default authSlice.reducer;
