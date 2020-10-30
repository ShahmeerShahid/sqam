import { getRequest, postRequest } from "../../network";

export async function fetchTasks() {
  try {
    const response = await getRequest("/api/tasks/");
    return response.data;
  } catch (e) {
    return "error";
  }
}

export async function createTask({ name, status }) {
  try {
    const body = {
      name: name,
      status: status,
    };
    const response = await postRequest("/api/tasks/", body);
    return response.data;
  } catch (e) {
    return {
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}
