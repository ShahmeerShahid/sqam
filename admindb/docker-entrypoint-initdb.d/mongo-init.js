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