import axios from "axios";

export const serverUrl = "http://localhost:9000";

const enforceTrailingSlash = (url) => {
  return url.endsWith("/") ? url : url + "/";
};

export function getRequest(uri, searchParams) {
  const params = searchParams ? searchParams : "";
  return axios.get(enforceTrailingSlash(`${serverUrl}${uri}`) + params);
}

export function postRequest(uri, data) {
  return axios.post(enforceTrailingSlash(`${serverUrl}${uri}`), data);
}
