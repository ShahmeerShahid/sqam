import React from "react";
import "mutationobserver-shim";
import { render, fireEvent, waitFor, wait } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import AddTaskForm from ".";
import { formatFieldToTitle } from "../../helpers";
import {
  wrapInTheme,
  wrapComponentInSnackBarProvider,
  wrapComponentInRouter,
} from "../../testing/helpers";
import { mockMarkus } from "../../testing/mockData";
import { act } from "react-dom/test-utils";

global.MutationObserver = window.MutationObserver;

describe("AddTaskForm", () => {
  const renderForm = () => {
    return render(
      wrapInTheme(
        wrapComponentInSnackBarProvider(
          wrapComponentInRouter(
            <AddTaskForm
              info={mockMarkus.info}
              extraFields={mockMarkus.extra_fields}
              setShowAddTaskForm={() => {}}
              enqueueSnackbar={() => {}}
            />
          )
        )
      )
    );
  };

  it("Renders extra fields ", () => {
    const { getByText } = renderForm();
    Object.entries(mockMarkus.extra_fields).forEach((extraField) => {
      expect(getByText(formatFieldToTitle(extraField[0]))).toBeInTheDocument();
    });
  });

  it("Renders errors if user attempts incomplete form submission", async () => {
    const { getByText, getByRole } = renderForm();

    act(() => {
      fireEvent.click(getByRole("button", { name: /submit/i }));
    });

    await wait(() => {
      expect(getByText(/task name is required/i)).toBeInTheDocument();
      Object.entries(mockMarkus.extra_fields).forEach((extraField) => {
        expect(
          getByText(`${formatFieldToTitle(extraField[0])} is required`)
        ).toBeInTheDocument();
      });
    });
  });
});
