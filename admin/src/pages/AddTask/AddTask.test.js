import React from "react";
import { act, render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import { fetchConnectors } from "../../requests/connectors";
import AddTask from ".";
import { wrapInAll } from "../../testing/helpers";
import { mockConnectors } from "../../testing/mockData";

jest.mock("../../requests/connectors");

describe("AddTask", () => {
  const renderPage = (props) => {
    return render(wrapInAll(<AddTask {...props} />));
  };

  beforeEach(() => {
    fetchConnectors.mockImplementationOnce(() =>
      Promise.resolve(mockConnectors)
    );
  });

  it("Renders", async () => {
    let component;
    await act(async () => {
      component = renderPage();
    });
    expect(component.getByText("Add Task")).toBeInTheDocument();
    expect(fetchConnectors).toHaveBeenCalledTimes(1);
  });
});
