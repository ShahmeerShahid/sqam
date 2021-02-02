import React from "react";
import { act, fireEvent, render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import TaskLog from ".";
import { wrapInAll } from "../../testing/helpers";
import { mockLogs } from "../../testing/mockData";

describe("TaskLog", () => {
  const renderTaskLog = (props) => {
    const defaultProps = {
      enqueueSnackbar: () => {},
      taskLogs: mockLogs,
      tid: 1,
    };
    return render(wrapInAll(<TaskLog {...defaultProps} {...props} />));
  };

  it("Renders logs", () => {
    const { getByText } = renderTaskLog();
    expect(getByText("Logs for 1")).toBeInTheDocument();
    expect(
      getByText(
        /> automarker \-\- \[05:12:01:40\] automarker running successfully/i
      )
    ).toBeInTheDocument();
    expect(
      getByText(/> connector \-\- \[01:12:01:40\] connector stopped working/i)
    ).toBeInTheDocument();
  });

  it("Clicking on select renders all options", () => {
    const { getAllByRole, getByRole } = renderTaskLog();
    act(() => {
      fireEvent.click(getByRole("combobox"));
    });
    const options = getAllByRole("option");
    expect(options.length).toBe(5);
  });
});
