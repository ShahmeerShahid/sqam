import React from "react";
import { act, fireEvent, render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import SelectConnectorForm from ".";
import { wrapInAll } from "../../testing/helpers";
import { mockConnectors } from "../../testing/mockData";

describe("SelectConnectorForm", () => {
  const renderSelectConnectorForm = (props) => {
    const defaultProps = {
      connectors: mockConnectors,
      enqueueSnackbar: () => {},
      resetForm: () => {},
      setFieldValue: () => {},
      setStage: () => {},
      setValues: () => {},
      values: {
        connector: null,
        connectorIndex: NaN,
        connectorInfo: null,
      },
    };

    return render(
      wrapInAll(<SelectConnectorForm {...defaultProps} {...props} />)
    );
  };

  it("Renders options based on connectors", () => {
    const { getByRole, getByText } = renderSelectConnectorForm();
    const select = getByRole("combobox");
    act(() => {
      fireEvent.click(select);
    });
    mockConnectors.forEach((connector) => {
      expect(getByText(connector.name)).toBeInTheDocument();
    });
  });
});
