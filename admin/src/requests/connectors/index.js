import { getRequest, getRequestSpecifyPort } from "../../network";

export async function fetchConnectors() {
  const response = await getRequest("/api/connectors/");
  return response.data;
}

export async function fetchExtraFields(port) {
  try {
    const response = await getRequestSpecifyPort(`:${port}/extra_fields/`);
    return response.data;
  } catch (e) {
    return {
      error: true,
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}
