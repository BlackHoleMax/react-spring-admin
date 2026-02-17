import { createAsyncThunk, createSlice, type PayloadAction } from '@reduxjs/toolkit';
import { getCurrentUserMenus } from '@/services/menu.ts';
import type { Menu } from '@/types';

interface MenuState {
  menus: Menu[];
  loading: boolean;
  error: string | null;
  collapsed: boolean;
}

const initialState: MenuState = {
  menus: [],
  loading: false,
  error: null,
  collapsed: false,
};

export const fetchUserMenus = createAsyncThunk(
  'menu/fetchUserMenus',
  async (_, { rejectWithValue }) => {
    try {
      return await getCurrentUserMenus();
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : '加载菜单失败');
    }
  }
);

const menuSlice = createSlice({
  name: 'menu',
  initialState,
  reducers: {
    setMenus: (state, action: PayloadAction<Menu[]>) => {
      state.menus = action.payload;
    },
    toggleCollapsed: (state) => {
      state.collapsed = !state.collapsed;
    },
    setCollapsed: (state, action: PayloadAction<boolean>) => {
      state.collapsed = action.payload;
    },
    clearMenus: (state) => {
      state.menus = [];
      state.error = null;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUserMenus.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUserMenus.fulfilled, (state, action) => {
        state.loading = false;
        state.menus = action.payload;
        state.error = null;
      })
      .addCase(fetchUserMenus.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { setMenus, toggleCollapsed, setCollapsed, clearMenus, clearError } =
  menuSlice.actions;
export default menuSlice.reducer;
