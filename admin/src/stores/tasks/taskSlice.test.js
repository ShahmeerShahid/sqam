import configureStore from "redux-mock-store";
import thunk from "redux-thunk";
import reducer, {
  taskReducerName,
  initialState,
  createTask,
  fetchTasks,
  taskStoreSelector,
  isLoadingTasksSelector,
  errorSelector,
} from "./taskSlice";
import { mockTasks } from "../../testing/mockData";
import { getRequest, postRequest } from "../../network";

jest.mock("../../network");

const mockStore = configureStore([thunk]);

const mockFullState = {
  [taskReducerName]: {
    ...initialState,
  },
};

const mockState = mockFullState[taskReducerName];

describe("createTask thunk & reducer", () => {
  const body = {
    name: mockTasks[0].name,
    status: mockTasks[0].status,
  };
  let store;

  beforeEach(() => {
    store = mockStore(mockState);
  });

  afterEach(() => {
    getRequest.mockReset();
  });

  describe("Reducers", () => {
    test("Pending", () => {
      expect(reducer(mockState, createTask.pending()).newTask).toEqual(
        expect.objectContaining({
          error: null,
          isCreating: true,
        })
      );
    });

    test("Fulfilled", () => {
      expect(
        reducer(mockState, createTask.fulfilled(mockTasks[0])).newTask
      ).toEqual(
        expect.objectContaining({
          error: null,
          isCreating: false,
        })
      );
    });

    test("Rejected", () => {
      const failureResponse = {
        status: 500,
        message: "Something went wrong",
      };

      const action = createTask.rejected(
        "Rejected",
        "some-id",
        body,
        failureResponse
      );

      expect(reducer(mockState, action).newTask).toEqual(
        expect.objectContaining({
          error: failureResponse,
          isCreating: false,
        })
      );
    });
  });

  test("Successful report creation", async () => {
    const response = { data: mockTasks[0] };
    postRequest.mockImplementation(() => Promise.resolve(response));

    await store.dispatch(createTask(body));

    expect(postRequest).toHaveBeenCalledWith("/api/tasks/", body);

    const actions = store.getActions();

    expect(actions).toContainEqual(
      expect.objectContaining({
        type: createTask.fulfilled.type,
        payload: response.data,
      })
    );
  });

  test("Failed task creation returns the appropriate error", async () => {
    const error = {
      response: {
        data: {
          non_field_errors: ["Something went wrong"],
        },
        status: 400,
      },
    };
    postRequest.mockImplementation(() => Promise.reject(error));

    await store.dispatch(createTask(body));

    const actions = store.getActions();

    expect(actions).toContainEqual(
      expect.objectContaining({
        type: createTask.rejected.type,
        payload: {
          message: error.response.data,
          status: error.response.status,
        },
      })
    );
  });
});

describe("fetchTasks thunk & reducer", () => {
  let store;
  beforeEach(() => {
    store = mockStore(mockState);
  });

  afterEach(() => {
    getRequest.mockReset();
  });

  describe("Reducers", () => {
    test("Sets pending state", () => {
      expect(reducer(mockState, fetchTasks.pending(null)).isLoading).toBe(true);
    });

    test("Sets fulfilled state", () => {
      expect(
        reducer(
          reducer(mockState, fetchTasks.pending(null)),
          fetchTasks.fulfilled(mockTasks, null)
        )
      ).toEqual(
        expect.objectContaining({
          ids: mockTasks.map((e) => e.id),
          entities: Object.fromEntries(mockTasks.map((e) => [e.id, e])),
          isLoading: false,
          error: null,
        })
      );
    });

    test("Sets rejected state", () => {
      const error = { status: 999, message: "Something went wrong" };
      expect(
        reducer(
          reducer(mockState, fetchTasks.pending(null)),
          fetchTasks.rejected("Rejected", "some-id", null, error)
        )
      ).toEqual(
        expect.objectContaining({
          isLoading: false,
          error: error,
        })
      );
    });
  });

  describe("Thunk", () => {
    test("thunk successfully fetches", async (done) => {
      const response = {
        data: mockTasks,
      };
      getRequest.mockImplementationOnce(() => Promise.resolve(response));

      await store.dispatch(fetchTasks({}));
      const actions = store.getActions();

      expect(getRequest).toHaveBeenCalledWith("/api/tasks/");
      expect(actions).toContainEqual(
        expect.objectContaining({
          type: fetchTasks.fulfilled.type,
          payload: response.data,
        })
      );
      done();
    });

    test("Thunk handles failure", async (done) => {
      const error = {
        response: {
          data: {
            non_field_errors: ["Something went wrong"],
          },
          status: 400,
        },
      };
      getRequest.mockImplementationOnce(() => Promise.reject(error));

      await store.dispatch(fetchTasks({}));
      const actions = store.getActions();
      expect(actions).toContainEqual(
        expect.objectContaining({
          type: fetchTasks.rejected.type,
          payload: {
            message: error.response.data,
            status: error.response.status,
          },
        })
      );
      done();
    });
  });
});

describe("Selectors", () => {
  test("taskStoreSelector", () => {
    expect(taskStoreSelector(mockFullState)).toEqual(mockState);
  });

  test("isLoadingTasksSelector", () => {
    expect(isLoadingTasksSelector(mockFullState)).toEqual(mockState.isLoading);
  });

  test("errorSelector", () => {
    expect(errorSelector(mockFullState)).toEqual(mockState.error);
  });
});
