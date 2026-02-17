import { createSlice } from '@reduxjs/toolkit';

export type ConnectionState = 'connecting' | 'connected' | 'disconnected' | 'error';

interface WebSocketState {
  connectionState: ConnectionState;
  lastConnectedTime: number | null;
  reconnectAttempts: number;
}

const initialState: WebSocketState = {
  connectionState: 'disconnected',
  lastConnectedTime: null,
  reconnectAttempts: 0,
};

const websocketSlice = createSlice({
  name: 'websocket',
  initialState,
  reducers: {
    setConnectionState: (state, action) => {
      state.connectionState = action.payload;
      if (action.payload === 'connected') {
        state.lastConnectedTime = Date.now();
        state.reconnectAttempts = 0;
      }
    },
    setReconnectAttempts: (state, action) => {
      state.reconnectAttempts = action.payload;
    },
    resetWebSocketState: (state) => {
      state.connectionState = 'disconnected';
      state.lastConnectedTime = null;
      state.reconnectAttempts = 0;
    },
  },
});

export const { setConnectionState, setReconnectAttempts, resetWebSocketState } =
  websocketSlice.actions;

export default websocketSlice.reducer;
