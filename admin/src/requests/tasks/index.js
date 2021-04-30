import { getRequest, getZip, postRequest } from "../../network";

// Get a list of all tasks
export async function fetchTasks() {
  const response = await getRequest("/api/tasks/");
  return response.data;
}

// get a task object with a specific tid
export async function fetchTasksInfo({ tid }) {
  const response = await getRequest("/api/tasks/" + tid);
  return response.data;
}

// Post request to create a task given body (name, connector, status, extra_fields, submissions)
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

// post request to upload the task files given files path in files parameter
export async function uploadTaskFiles({ files }) {
  const form = new FormData();
  files.forEach((file) => {
    form.append(file.meta.name.slice(0, -4), file.file);
  });
  const response = await postRequest("/api/tasks/upload", form);
  return response.data;
}

// get a report of a task given tid in zip format
export async function downloadReport(tid) {
  try {
    const response = await getZip(`/api/tasks/reports/${tid}`);
    return response;
  } catch (e) {
    return {
      error: true,
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}
