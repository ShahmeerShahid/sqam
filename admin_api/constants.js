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

module.exports = {
  apps: apps,
  connectors: connectors,
  statuses: statuses,
  logSources,
};
