import React from "react";
import { render } from "@testing-library/react";
import { UnconnectedHomePage } from "./";
import { wrapInStore, wrapComponentInRouter } from "../../testing/helpers";

describe("UnconnectedHomePage", () => {
  const renderHomepage = (additionalProps) => {
    return render(
      wrapInStore(
        wrapComponentInRouter(
          <UnconnectedHomePage
            fetchTasks={() => {}}
            tasks={[]}
            {...additionalProps}
          />
        )
      )
    );
  };

  it("Renders the Learn React test", () => {
    const { getByText } = renderHomepage();
    expect(getByText("Learn React")).toBeInTheDocument();
  });

  it("Calls the fetchTasks upon mount", () => {
    const fetchTasksSpy = jest.fn();
    renderHomepage({
      fetchTasks: fetchTasksSpy,
    });
    expect(fetchTasksSpy).toHaveBeenCalled();
  });
});
