import React, { useEffect } from "react";
import { connect } from "react-redux";
import { fetchTasks, tasksSelectors } from "../../stores/tasks/taskSlice";

import logo from "../../logo.svg";
import "../../App.css";

export function UnconnectedHomePage({ fetchTasks, tasks }) {
  useEffect(() => {
    fetchTasks({});
  }, [fetchTasks]);

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.js</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
    </div>
  );
}

const mapStateToProps = (state) => {
  return {
    tasks: tasksSelectors.selectAll(state),
  };
};

const ConnectedHomePage = connect(mapStateToProps, {
  fetchTasks,
})(UnconnectedHomePage);

export default ConnectedHomePage;
