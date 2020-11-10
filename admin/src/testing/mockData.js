export const createMockTask = ({ id, name, status }) => ({
  id,
  name,
  status,
});

export const mockTasks = [
  createMockTask({
    id: 1,
    name: "Task 1",
    status: "Pending",
  }),
  createMockTask({
    id: 2,
    name: "Task 2",
    status: "Marking",
  }),
];

export const mockConnectors = [
  {
    name: "Markus",
    url: "http://markus-connector",
    port: 8001,
  },
  {
    name: "Example",
    url: "http://example",
    port: 3000,
  },
];

export const mockMarkus = {
  info:
    "Groups names MUST NOT have white space. Empty submissions will be ignored",
  extra_fields: {
    markus_URL: {
      type: "string",
      required: true,
      info:
        "Information specific to this field e.g. Example: http://www.test-markus.com, NOT www.test-markus.com or http://www.test-markus.com/en/main",
      placeholder: "http://www.test-markus.com",
    },
    assignment_id: {
      type: "number",
      required: true,
      info:
        "Found in the URL when editing the assignment. E.g. http://www.test-markus.com/en/assignments/1/edit would have ID 1.",
      placeholder: "1",
    },
    api_key: {
      type: "string",
      required: true,
      info: "Found on the homepage of your Markus instance.",
      placeholder: "hasf08etJSkf=",
    },
  },
};
