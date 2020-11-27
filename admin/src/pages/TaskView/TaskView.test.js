import React from "react";
import { render, wait } from "@testing-library/react";
import "@testing-library/jest-dom/extend-expect";
import { Task, Tasks } from ".";
import { fetchTasks } from "../../requests/tasks";
import { wrapInTheme, wrapComponentInRouter } from "../../testing/helpers";
import { mockTasks } from "../../testing/mockData";

jest.mock("../../requests/tasks");

describe("Task", () => {
  it("Renders a task for each task provided", () => {
    const { getByText } = render(wrapInTheme(<Task task={mockTasks[0]} />));
    expect(getByText(mockTasks[0].name)).toBeInTheDocument();
    expect(getByText(mockTasks[0].status)).toBeInTheDocument();
  });
});
