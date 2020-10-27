import {
  createAsyncThunk,
  createEntityAdapter,
  createSlice,
} from "@reduxjs/toolkit";
import { createSelector } from "reselect";
import { getRequest, postRequest } from "../../network";

export const tasksAdapter = createEntityAdapter({});
export const taskReducerName = "tasks";

export const initialState = tasksAdapter.getInitialState({
  error: null,
  isLoading: false,
  newTask: {
    error: null,
    isCreating: false,
  },
});

// Thunks

export const createTask = createAsyncThunk(
  `${taskReducerName}/createTask`,
  async ({ name, status }, { rejectWithValue }) => {
    const requestBody = {
      name: name,
      status: status,
    };
    try {
      const response = await postRequest("/api/tasks/", requestBody);
      return response.data;
    } catch (e) {
      return rejectWithValue({
        status: e.response && e.response.status,
        message: e.response && e.response.data,
      });
    }
  }
);

export const fetchTasks = createAsyncThunk(
  `${taskReducerName}/fetchTasks`,
  async (arg, { rejectWithValue }) => {
    try {
      const response = await getRequest("/api/tasks/");
      return response.data;
    } catch (e) {
      return rejectWithValue({
        status: e.response && e.response.status,
        message: e.response && e.response.data,
      });
    }
  }
);

// Slice

const taskSlice = createSlice({
  name: taskReducerName,
  initialState,
  reducers: {},
  extraReducers: {
    [createTask.pending]: (state) => {
      state.newTask.error = null;
      state.newTask.isCreating = true;
    },
    [createTask.fulfilled]: (state) => {
      state.newTask.error = null;
      state.newTask.isCreating = false;
    },
    [createTask.rejected]: (state, { payload }) => {
      state.newTask.error = payload;
      state.newTask.isCreating = false;
    },
    [fetchTasks.pending]: (state) => {
      state.error = null;
      state.isLoading = true;
    },
    [fetchTasks.fulfilled]: (state, { payload }) => {
      tasksAdapter.upsertMany(state, payload);
      state.error = null;
      state.isLoading = false;
    },
    [fetchTasks.rejected]: (state, { payload }) => {
      state.error = payload;
      state.isLoading = false;
    },
  },
});

const { reducer } = taskSlice;

export default reducer;

// Selectors

export const taskStoreSelector = (state) => state[taskReducerName];

export const tasksSelectors = tasksAdapter.getSelectors(taskStoreSelector);

export const isLoadingTasksSelector = createSelector(
  taskStoreSelector,
  (taskStore) => taskStore.isLoading
);

export const errorSelector = createSelector(
  taskStoreSelector,
  (taskStore) => taskStore.error
);
