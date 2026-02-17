import { createAsyncThunk, createSlice, type PayloadAction } from '@reduxjs/toolkit';
import { getUserPermissions } from '@/services/permission';

interface PermissionState {
  permissions: string[];
  loading: boolean;
  error: string | null;
}

const initialState: PermissionState = {
  permissions: [],
  loading: false,
  error: null,
};

export const fetchUserPermissions = createAsyncThunk(
  'permission/fetchUserPermissions',
  async (_, { rejectWithValue }) => {
    try {
      return await getUserPermissions();
    } catch (error) {
      return rejectWithValue(error instanceof Error ? error.message : '加载权限失败');
    }
  }
);

const permissionSlice = createSlice({
  name: 'permission',
  initialState,
  reducers: {
    setPermissions: (state, action: PayloadAction<string[]>) => {
      state.permissions = action.payload;
    },
    clearPermissions: (state) => {
      state.permissions = [];
      state.error = null;
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUserPermissions.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchUserPermissions.fulfilled, (state, action) => {
        state.loading = false;
        state.permissions = action.payload;
        state.error = null;
      })
      .addCase(fetchUserPermissions.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as string;
      });
  },
});

export const { setPermissions, clearPermissions, clearError } = permissionSlice.actions;
export default permissionSlice.reducer;
