import React from "react";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import TaskDetail from ".";
import { wrapInAll } from "../../testing/helpers";
import { mockTasksDetail } from "../../testing/mockData";

describe("TaskDetail", () => {
  const renderTaskDetail = (props) => {
    const defaultProps = {
      enqueueSnackbar: () => {},
      taskData: mockTasksDetail[0],
      tid: mockTasksDetail[0].tid,
    };
    return render(wrapInAll(<TaskDetail {...defaultProps} {...props} />));
  };

  it("Renders task info", async () => {
    const { getByText } = renderTaskDetail();
    const task = mockTasksDetail[0];
    expect(getByText(task.status)).toBeInTheDocument();
    expect(getByText(task.connector)).toBeInTheDocument();
    expect(getByText(task.submission_file_name)).toBeInTheDocument();
    expect(getByText(task.create_tables)).toBeInTheDocument();
    expect(getByText(task.create_trigger)).toBeInTheDocument();
    expect(getByText(task.create_function)).toBeInTheDocument();
    expect(getByText(task.load_data)).toBeInTheDocument();
    expect(getByText(task.solutions)).toBeInTheDocument();
    expect(getByText(task.db_type)).toBeInTheDocument();
    expect(getByText(task.name)).toBeInTheDocument();
  });
});
