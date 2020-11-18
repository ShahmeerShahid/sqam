db.getUser("root");

admindb = db.getSiblingDB("sqamadmin");
admindb.createCollection("users");
admindb.createCollection("tasks");

admindb.createUser({
  user: "api",
  pwd: "Y^I8s67tF2ur",
  roles: [
    {
      role: "readWrite",
      db: "sqamadmin",
    },
  ],
  mechanisms: ["SCRAM-SHA-1"],
});

admindb.tasks.insert({
  connector: "markus-connector",
  name: "CSC343 Fall Test 1",
  status: "Pending",
  extra_fields: { markus_URL: "http://markus.com" },
});

admindb.tasks.insert({
  connector: "markus-connector",
  name: "CSC343 Fall A1",
  status: "Error",
  extra_fields: { markus_URL: "http://markus.com", assignment_id: 1 },
});

admindb.tasks.insert({
  connector: "markus-connector",
  name: "CSC343 Fall A2",
  status: "Complete",
  extra_fields: {
    markus_URL: "http://markus.com",
    assignment_id: 2,
    api_key: "UFDsfhHffd=",
  },
});

admindb.tasks.insert({
  connector: "markus-connector",
  name: "CSC343 Fall A3",
  status: "Marking",
  extra_fields: { markus_URL: "http://markus.com", assignment_id: 3 },
  submissions: [{ name: "testgroup1" }],
});
