import React from "react";
import "mutationobserver-shim";
import { render, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import SelectConnectorForm from ".";
import { wrapInTheme, wrapComponentInRouter } from "../../testing/helpers";
import { mockConnectors } from "../../testing/mockData";

global.MutationObserver = window.MutationObserver;

describe("SelectConnectorForm", () => {
  const renderForm = () => {
    return render(
      wrapInTheme(
        wrapComponentInRouter(
          <SelectConnectorForm
            connectors={mockConnectors}
            setConnector={() => {}}
            fetchData={() => {}}
            setShowAddTaskForm={() => {}}
          />
        )
      )
    );
  };

  it("Submit is disabled unless valid option selected", async () => {
    const { getByText, getByTestId } = renderForm();
    expect(getByText(/Submit/).disabled).toBeTruthy();
    fireEvent.change(getByTestId("select"), {
      target: { value: mockConnectors[0].url },
    });
    expect(getByText(/Submit/).disabled).toBeFalsy();
  });

  it("Renders connector options", async () => {
    const { getByText, getByTestId } = renderForm();
    fireEvent.click(getByTestId("select"));
    mockConnectors.forEach((connector) => {
      expect(getByText(connector.name)).toBeInTheDocument();
    });
  });
});
