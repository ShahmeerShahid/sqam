import React from "react";
import { ThemeProvider, ColorModeProvider, CSSReset } from "@chakra-ui/core";
import {
  Switch,
  Redirect,
  Route,
  withRouter,
  BrowserRouter as Router,
} from "react-router-dom";
import Homepage from "./pages/Home";
import Classes from "./pages/Classes";
import Tasks from "./pages/Tasks";
import "./App.css";

function UnconnectedApp() {
  return (
    <div className="App">
      <Switch>
        <Route exact path="/" component={withRouter(Homepage)} />
        <Route exact path="/classes" component={withRouter(Classes)} />
        <Route exact path="/tasks" component={withRouter(Tasks)} />
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
      <ColorModeProvider>
        <CSSReset />
        <Router>
          <UnconnectedApp />
        </Router>
      </ColorModeProvider>
    </ThemeProvider>
  );
}

export default ConnectedApp;
