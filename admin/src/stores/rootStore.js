import { applyMiddleware, createStore, combineReducers } from "redux";
import { composeWithDevTools } from "redux-devtools-extension/developmentOnly";
import { connectRouter } from "connected-react-router";
import { createBrowserHistory } from "history";
import { routerMiddleware } from "connected-react-router";
import thunk from "redux-thunk";
import taskReducer, { taskReducerName } from "./tasks/taskSlice";

const createRootReducer = (history) =>
  combineReducers({
    [taskReducerName]: taskReducer,
    router: connectRouter(history),
  });

export const history = createBrowserHistory();
export default function configureStore(preloadedState) {
  const store = createStore(
    createRootReducer(history),
    preloadedState,
    composeWithDevTools(applyMiddleware(routerMiddleware(history), thunk))
  );
  return store;
}
