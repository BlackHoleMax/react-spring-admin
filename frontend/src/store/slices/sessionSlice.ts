import { createSlice } from '@reduxjs/toolkit';

interface SessionState {
  sessionTimeoutEnabled: boolean;
  sessionTimeoutMinutes: number;
  lastActivityTime: number;
  warningShown: boolean;
  unreadCount: number;
}

const initialState: SessionState = {
  sessionTimeoutEnabled: localStorage.getItem('sessionTimeoutEnabled') === 'true',
  sessionTimeoutMinutes: parseInt(localStorage.getItem('sessionTimeoutMinutes') || '30'),
  lastActivityTime: Date.now(),
  warningShown: false,
  unreadCount: 0,
};

const sessionSlice = createSlice({
  name: 'session',
  initialState,
  reducers: {
    setSessionTimeoutEnabled: (state, action) => {
      state.sessionTimeoutEnabled = action.payload;
      localStorage.setItem('sessionTimeoutEnabled', action.payload.toString());
    },
    setSessionTimeoutMinutes: (state, action) => {
      state.sessionTimeoutMinutes = action.payload;
      localStorage.setItem('sessionTimeoutMinutes', action.payload.toString());
    },
    updateLastActivity: (state) => {
      state.lastActivityTime = Date.now();
    },
    setWarningShown: (state, action) => {
      state.warningShown = action.payload;
    },
    resetSession: (state) => {
      state.lastActivityTime = Date.now();
      state.warningShown = false;
    },
    incrementUnread: (state) => {
      state.unreadCount += 1;
    },
    setUnreadCount: (state, action) => {
      state.unreadCount = action.payload;
    },
  },
});

export const {
  setSessionTimeoutEnabled,
  setSessionTimeoutMinutes,
  updateLastActivity,
  setWarningShown,
  resetSession,
  incrementUnread,
  setUnreadCount,
} = sessionSlice.actions;

export default sessionSlice.reducer;
