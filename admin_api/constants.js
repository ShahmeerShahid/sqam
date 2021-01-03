const statuses = [
  "Pending",
  "Downloading",
  "Downloaded",
  "Error",
  "Marking",
  "Complete",
];

const apps = ["automarker", "connectors", "admin", "admin_api"];

const connectors = ["markus-connector"];

const logSources = ["frontend", "automarker", "connector", "api"];

const requiredFiles = [
  "create_tables",
  "create_trigger",
  "create_function",
  "load_data",
  "solutions",
];

module.exports = {
  apps,
  connectors,
  logSources,
  requiredFiles,
  statuses,
};
