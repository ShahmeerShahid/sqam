import React from "react";
import { render, unmountComponentAtNode } from "react-dom";
import { login } from "../../requests/login/";
import ErrorMsg from "../../components/ErrorMsg";
import { Link } from "react-router-dom";

describe("LoginForm", () => {
  it("Renders a form data", async () => {
    const fakeform = {
      username: "test@utoronto.ca",
      password: "password",
      //   error: "",
      //   loading: "",
      //   loggedIn: ""
    };
    jest.spyOn(global, "LoginForm").mockImplementation(() =>
      Promise.resolve({
        json: () => Promise.resolve(fakeform),
      })
    );

    expect(getByText(fakeform.username)).toBeInTheDocument();
    expect(getByText(fakeform.password)).toBeInTheDocument();
    //   expect(getByText(fakeform.error)).toBeInTheDocument();
    //   expect(getByText(fakeform.loading)).toBeInTheDocument();
    //   expect(getByText(fakeform.loggedIn)).toBeInTheDocument();

    // remove the mock to ensure tests are completely isolated
    global.LoginForm.mockRestore();
  });
});
