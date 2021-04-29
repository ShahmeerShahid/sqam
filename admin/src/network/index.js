import axios from "axios";

export const localUrl = "http://localhost";
export const serverUrl = "http://localhost:9000";

const enforceTrailingSlash = (url) => {
  return url.endsWith("/") ? url : url + "/";
};

export function getRequest(uri, searchParams) {
  const params = searchParams ? searchParams : "";
  return axios.get(enforceTrailingSlash(`${serverUrl}${uri}`) + params);
}

export function getRequestSpecifyPort(uri, searchParams) {
  const params = searchParams ? searchParams : "";
  return axios.get(enforceTrailingSlash(`${localUrl}${uri}`) + params);
}

export function postRequest(uri, data) {
  return axios.post(enforceTrailingSlash(`${serverUrl}${uri}`), data);
}

export function getZip(uri) {
  return axios.get(enforceTrailingSlash(`${serverUrl}${uri}`), {
    responseType: "arraybuffer",
    headers: {
      "Content-Type": "application/zip",
    },
  });
}
