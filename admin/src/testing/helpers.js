import React from "react";
import { BrowserRouter as Router } from "react-router-dom";
import { Provider } from "react-redux";
import configureStore from "../stores/rootStore";

export const wrapInStore = (component) => {
  const store = configureStore();
  return <Provider store={store}>{component}</Provider>;
};

export const wrapComponentInRouter = (component) => (
  <Router>{component}</Router>
);
