import React from "react";
import { render } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import TaskDetailsForm from "./index";
import { StageTwoSchema } from "../MasterForm";
import { wrapInAll } from "../../testing/helpers";
import { mockMarkus } from "../../testing/mockData";

// unfortunately exporting this function from TaskDetailsForm
// results in a "not a function" error, so it has to be duplicated
function validateStageTwo(values) {
  try {
    StageTwoSchema.validateSync({
      name: values.name,
      max_marks_per_question: values.max_marks_per_question,
      question_names: values.question_names,
      submission_file_name: values.submission_file_name,
      extra_fields: values.extra_fields,
    });
    return false;
  } catch (e) {
    return true;
  }
}

describe("TaskDetailsForm", () => {
  let values = {
    name: "",
    question_names: [],
    max_marks_per_question: [],
    submission_file_name: "",
    extra_fields: {
      api_key: "",
      assignment_id: 0,
      markus_URL: "",
    },
  };
  const renderTaskDetailsForm = (props) => {
    const defaultProps = {
      connectorInfo: mockMarkus,
      isLoadingConnectorInfo: false,
      questions: [],
      saveBtn: null,
      setFieldValue: () => {},
      setStage: () => {},
      validate: () => {},
      values: values,
    };

    return render(wrapInAll(<TaskDetailsForm {...defaultProps} {...props} />));
  };

  it("Basic render functionality", () => {
    const { getByText } = renderTaskDetailsForm();
    expect(getByText(mockMarkus.info)).toBeInTheDocument();
  });

  it("Continue button is disabled while form is incomplete", () => {
    const { getByRole } = renderTaskDetailsForm({
      validate: () => validateStageTwo(values),
    });
    expect(getByRole("button", { name: /continue/i }).disabled).toBe(true);
  });

  it("Continue button is not disabled when form is complete", () => {
    values = {
      name: "CSC343 A1",
      question_names: ["Q1"],
      max_marks_per_question: [1],
      submission_file_name: "queries.sql",
      extra_fields: {
        api_key: "Aa=",
        assignment_id: 11,
        markus_URL: "http://www.test-markus.com",
      },
    };

    const { getByRole } = renderTaskDetailsForm({
      validate: () => validateStageTwo(values),
    });
    expect(getByRole("button", { name: /continue/i }).disabled).toBe(false);
  });
});
