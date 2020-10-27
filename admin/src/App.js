import React from "react";
import { ConnectedRouter } from "connected-react-router";
import { Provider as ReduxProvider } from "react-redux";
import { Switch, Redirect, Route, withRouter } from "react-router-dom";
import configureStore, { history } from "./stores/rootStore";
import Homepage from "./pages/Home";
import "./App.css";

const store = configureStore();

function UnconnectedApp() {
  return (
    <div className="App">
      <Switch>
        <Route exact path="/" component={withRouter(Homepage)} />
        <Route
          exact
          path="/not-found"
          render={() => (
            <>
              <h1>Not Found</h1>
              <p>The requested page could not be found</p>
            </>
          )}
        />
        <Redirect to="/not-found" />
      </Switch>
    </div>
  );
}

function ConnectedApp() {
  return (
    <ReduxProvider store={store}>
      <ConnectedRouter history={history}>
        <UnconnectedApp />
      </ConnectedRouter>
    </ReduxProvider>
  );
}

export default ConnectedApp;
