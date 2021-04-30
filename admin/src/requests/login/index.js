// Unused File

const bcrypt = require("bcryptjs");
const saltRounds = 10;
const myPassword = "password";

export async function login({ username, password }) {
  const hash = await bcrypt.hash(password, saltRounds);
  try {
    const match = await bcrypt.compare(myPassword, hash);
    return match && username === "test@utoronto.ca";
  } catch (e) {
    return {
      error: true,
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}
