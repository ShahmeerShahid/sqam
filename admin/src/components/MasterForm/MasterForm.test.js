import React from "react";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import { UnconnectedMasterForm } from ".";
import { wrapInAll } from "../../testing/helpers";

describe("MasterForm", () => {
  const renderMasterForm = (props) => {
    const defaultProps = {
      connectors: [],
      enqueueSnackbar: () => {},
      handleSubmit: () => {},
      resetForm: () => {},
      setFieldValue: () => {},
      setStage: () => {},
      setValues: () => {},
      stage: () => {},
      values: {
        connector: "",
        connectorInfo: "",
      },
    };

    return render(
      wrapInAll(<UnconnectedMasterForm {...defaultProps} {...props} />)
    );
  };

  it("Renders", () => {
    const { getByText } = renderMasterForm();
    expect(getByText("Select connector")).toBeInTheDocument();
  });
});
