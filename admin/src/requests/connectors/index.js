import { getRequest, getRequestSpecifyPort } from "../../network";

// Gets list of connectors (name and url) that can be used to download submissions
export async function fetchConnectors() {
  const response = await getRequest("/api/connectors/");
  return response.data;
}

// 
export async function fetchExtraFields(connector_name) {
  try {
    const response = await getRequest(
      `/api/connectors/${connector_name}/extra_fields/`
    );
    return response.data;
  } catch (e) {
    return {
      error: true,
      status: e.response && e.response.status,
      message: e.response && e.response.data,
    };
  }
}
