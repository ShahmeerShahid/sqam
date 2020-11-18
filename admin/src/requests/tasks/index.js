import { getRequest, postRequest } from "../../network";

export async function fetchTasks() {
  const response = await getRequest("/api/tasks/");
  return response.data;
}

export async function createTask({ name, connector, status, extra_fields }) {
  const body = {
    name: name,
    connector: connector,
    status: status ? status : "Pending",
    extra_fields: extra_fields,
  };
  try {
    const response = await postRequest("/api/tasks/", body);
    return response.data;
  } catch (e) {
    return {
      error: true,
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}
