import { createSlice } from '@reduxjs/toolkit';

interface ThemeState {
  mode: 'light' | 'dark';
  primaryColor: string;
  compactMode: boolean;
}

const initialState: ThemeState = {
  mode: (localStorage.getItem('theme') as 'light' | 'dark') || 'light',
  primaryColor: localStorage.getItem('primaryColor') || '#1890ff',
  compactMode: localStorage.getItem('compactMode') === 'true',
};

const themeSlice = createSlice({
  name: 'theme',
  initialState,
  reducers: {
    setTheme: (state, action) => {
      state.mode = action.payload;
      localStorage.setItem('theme', action.payload);
    },
    toggleTheme: (state) => {
      state.mode = state.mode === 'light' ? 'dark' : 'light';
      localStorage.setItem('theme', state.mode);
    },
    setPrimaryColor: (state, action) => {
      state.primaryColor = action.payload;
      localStorage.setItem('primaryColor', action.payload);
    },
    toggleCompactMode: (state) => {
      state.compactMode = !state.compactMode;
      localStorage.setItem('compactMode', state.compactMode.toString());
    },
    setCompactMode: (state, action) => {
      state.compactMode = action.payload;
      localStorage.setItem('compactMode', action.payload.toString());
    },
  },
});

export const { setTheme, toggleTheme, setPrimaryColor, toggleCompactMode, setCompactMode } =
  themeSlice.actions;
export default themeSlice.reducer;
