import React from "react";
import { ThemeProvider, ColorModeProvider, CSSReset } from "@chakra-ui/core";
import { SnackbarProvider } from "notistack";
import {
  Switch,
  Redirect,
  Route,
  withRouter,
  BrowserRouter as Router,
} from "react-router-dom";
import Homepage from "./pages/Home";
import Tasks from "./pages/Tasks";
import TaskView from "./pages/TaskView";
import AddTask from "./pages/AddTask";

import "./App.css";

function UnconnectedApp() {
  return (
    <div className="App">
      <Switch>
        <Route exact path="/" component={withRouter(Homepage)} />
        <Route exact path="/tasks" component={withRouter(Tasks)} />
        <Route exact path="/tasks/add" component={withRouter(AddTask)} />
        <Route path="/tasks/:tid" children={<TaskView />} />
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
    <ThemeProvider>
      <SnackbarProvider maxSnack={3}>
        <ColorModeProvider>
          <CSSReset />
          <Router>
            <UnconnectedApp />
          </Router>
        </ColorModeProvider>
      </SnackbarProvider>
    </ThemeProvider>
  );
}

export default ConnectedApp;
