//import { postRequest } from "../../network";

const bcrypt = require("bcryptjs");
const saltRounds = 10;
const myPassword = "password";

export async function login({ username, password }) {
  const hash = await bcrypt.hash(password, saltRounds);

  // const body = {
  //   username: username,
  //   password:hash
  // };
  try {
    const match = await bcrypt.compare(myPassword, hash);
    //const response = await postRequest("/api/TODO", body);
    //return response.data;
    return match && username === "test@utoronto.ca";
  } catch (e) {
    return {
      error: true,
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}
