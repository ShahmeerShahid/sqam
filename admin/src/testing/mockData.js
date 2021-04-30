// This file contains mock (fake) data which will be used to substitute api calls.

export const createMockTask = ({ tid, name, status }) => ({
  tid,
  name,
  status,
});

export const mockTasks = [
  createMockTask({
    tid: 1,
    name: "Task 1",
    status: "Pending",
  }),
  createMockTask({
    tid: 2,
    name: "Task 2",
    status: "Marking",
  }),
];

export const createMockTaskDetail = ({
  status,
  connector,
  num_submissions,
  max_marks,
  max_marks_per_question,
  question_names,
  submission_file_name,
  create_tables,
  create_trigger,
  create_function,
  load_data,
  solutions,
  submissions_path,
  timeout,
  db_type,
  name,
  extra_fields,
  submissions,
  logs,
  tid,
}) => ({
  status,
  connector,
  num_submissions,
  max_marks,
  max_marks_per_question,
  question_names,
  submission_file_name,
  create_tables,
  create_trigger,
  create_function,
  load_data,
  solutions,
  submissions_path,
  timeout,
  db_type,
  name,
  extra_fields,
  submissions,
  logs,
  tid,
});

export const mockTasksDetail = [
  createMockTaskDetail({
    status: "Error",
    connector: "markus-connector",
    num_submissions: 0,
    max_marks: 70,
    max_marks_per_question: [
      3,
      4,
      3,
      3,
      4,
      4,
      2,
      2,
      4,
      5,
      3,
      4,
      4,
      4,
      3,
      5,
      6,
      7,
    ],
    marking_type: "",
    question_names: [
      "Q1",
      "Q2",
      "Q3.A",
      "Q3.B",
      "Q3.C",
      "Q4.A",
      "Q4.B",
      "Q4.C",
      "Q5.A",
      "Q5.B",
      "Q6.A",
      "Q6.B",
      "Q6.C",
      "Q7.A",
      "Q7.B",
      "Q8",
      "Q9",
      "Q10",
    ],
    submission_file_name: "queries.sql",
    create_tables: "/var/downloads/0/create_tables.sql",
    create_trigger: "/var/downloads/0/create_trigger.sql",
    create_function: "/var/downloads/0/create_function.sql",
    load_data: "/var/downloads/0/load_data.sql",
    solutions: "/var/downloads/0/solutions.sql",
    submissions_path: "",
    timeout: 100,
    db_type: "mysql",
    name: "A2",
    extra_fields: {
      markus_URL: "http://testurl.com/",
      assignment_id: 12,
      api_key: "DFDFDsfdsfdfdA=",
    },
    submissions: [],
    logs: [],
    tid: 0,
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

export const mockLogs = [
  {
    source: "automarker",
    timestamp: "05:12:01:40",
    text: "Automarker running successfully",
  },
  {
    source: "connector",
    timestamp: "01:12:01:40",
    text: "Connector stopped working",
  },
];
