import React from "react";
import { shallow } from "enzyme";
import { login } from "../../requests/login/";

jest.mock("../../requests/login");
describe("Login test", () => {
  const wrapper = shallow(<Login />);

  it("should have a login button", () => {
    expect(wrapper.find("Button")).toHaveLength(1);

    //Button should be of type button
    expect(wrapper.find("Button").type().defaultProps.type).toEqual("button");

    expect(wrapper.find("Button").text()).toEqual("Login");
  });

  it("should have input for email and password", () => {
    //Email and password input field should be present
    expect(wrapper.find("input#email")).toHaveLength(1);
    expect(wrapper.find("input#password")).toHaveLength(1);
  });

  it("should test email and password presence", () => {
    //correct username and password should return true
    expect(login("test@utoronto.ca", "password")).toEqual(true);

    //empty email and password should return false
    expect(validateEmailAndPasswordPresence("", "")).toEqual(false);
  });
});
