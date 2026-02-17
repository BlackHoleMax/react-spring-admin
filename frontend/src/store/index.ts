import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import menuReducer from './slices/menuSlice';
import themeReducer from './slices/themeSlice';
import sessionReducer from './slices/sessionSlice';
import websocketReducer from './slices/websocketSlice';
import permissionReducer from './slices/permissionSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    menu: menuReducer,
    theme: themeReducer,
    session: sessionReducer,
    websocket: websocketReducer,
    permission: permissionReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
