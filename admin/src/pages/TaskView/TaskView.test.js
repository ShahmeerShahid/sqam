import React from "react";
import { act, render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import TaskView from ".";
import { fetchTasksInfo } from "../../requests/tasks";
import { wrapInAll } from "../../testing/helpers";
import { mockTasksDetail } from "../../testing/mockData";

jest.mock("../../requests/tasks");

describe("TaskView", () => {
  const renderTaskView = (props) => {
    const defaultProps = {
      enqueueSnackbar: () => {},
    };
    return render(wrapInAll(<TaskView {...defaultProps} {...props} />));
  };

  it("Renders appropriate message if no tasks", async () => {
    fetchTasksInfo.mockImplementation(() => Promise.resolve(null));
    let component;
    await act(async () => {
      component = renderTaskView();
    });
    expect(component.getByText("No tasks to display")).toBeInTheDocument();
  });

  it("Renders task", async () => {
    const response = mockTasksDetail[0];
    fetchTasksInfo.mockImplementation(() => Promise.resolve(response));
    let component;
    await act(async () => {
      component = renderTaskView();
    });
    expect(component.getByText(mockTasksDetail[0].name)).toBeInTheDocument();
  });
});
