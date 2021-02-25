import { getRequest, postRequest } from "../../network";

export async function fetchTasks() {
  const response = await getRequest("/api/tasks/");
  return response.data;
}

export async function fetchTasksInfo({ tid }) {
  const response = await getRequest("/api/tasks/" + tid);
  return response.data;
}

export async function createTask(body) {
  //validation for prohibited keys
  const reqBody = {
    ...body,
    status: body.status ? body.status : "Pending",
  };
  try {
    const response = await postRequest("/api/tasks/", reqBody);
    return response.data;
  } catch (e) {
    console.log(e);
    return {
      error: true,
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}

export async function uploadTaskFiles({ files }) {
  const form = new FormData();
  files.forEach((file) => {
    form.append(file.meta.name.slice(0, -4), file.file);
  });
  const response = await postRequest("/api/tasks/upload", form);
  return response.data;
}

export async function downloadReport(tid) {
  try {
    const response = await getRequest(`/api/tasks/reports/${tid}`);
    console.log(response);
    return response;
  } catch (e) {
    return {
      error: true,
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}
