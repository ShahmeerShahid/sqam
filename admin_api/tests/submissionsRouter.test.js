const expect = require("chai").expect;
const request = require("supertest");
const setup = require("./setup");
const server = require("../server");
const Task = require("../models/task.model");

describe("GET /submissions/:tid", () => {
  const submission = { name: "testgroup", status: "Pending" };
  const data = {
    name: "CSC343 A1",
    status: "Pending",
    tid: 1,
    submissions: [submission],
  };

  setup();
  beforeEach((done) => {
    new Task(data)
      .save()
      .then(() => done())
      .catch((err) => done(err));
  });

  it("existing data", (done) => {
    request(server)
      .get("/api/submissions/1")
      .then((res) => {
        expect(res.statusCode).to.equal(200);
        expect(res.body[0].name).to.equal(submission.name);
        expect(res.body[0].status).to.equal(submission.status);
        done();
      })
      .catch((err) => done(err));
  });
});

describe("POST /submissions/:tid", () => {
  const data = {
    name: "CSC343 A1",
    status: "Pending",
  };

  setup();
  beforeEach((done) => {
    new Task(data)
      .save()
      .then(() => done())
      .catch((err) => done(err));
  });

  it("valid data", (done) => {
    const submissionData = { names: ["testgroup"] };
    request(server)
      .post("/api/submissions/1")
      .send(submissionData)
      .then((res) => {
        expect(res.statusCode).to.equal(201);
        expect(res.body.message).to.equal(
          "Submission(s) successfully added to task 1"
        );
        done();
      })
      .catch((err) => done(err));
  });

  it("empty names", (done) => {
    const submissionData = { names: [] };
    request(server)
      .post("/api/submissions/1")
      .send(submissionData)
      .then((res) => {
        expect(res.statusCode).to.equal(400);
        done();
      })
      .catch((err) => done(err));
  });

  it("invalid tid", (done) => {
    const submissionData = { names: ["hello"] };
    request(server)
      .post("/api/submissions/howdy")
      .send(submissionData)
      .then((res) => {
        expect(res.statusCode).to.equal(400);
        done();
      })
      .catch((err) => done(err));
  });
});

describe("PATCH /status/:sid", () => {
  const data = {
    name: "CSC343 A1",
    status: "Pending",
    submissions: {
      name: "testgroup",
    },
  };

  var submissionId = "";

  setup();
  beforeEach((done) => {
    new Task(data)
      .save()
      .then((task) => {
        submissionId = task.submissions[0]._id;
        done();
      })
      .catch((err) => done(err));
  });

  it("standard update", (done) => {
    request(server)
      .patch(`/api/submissions/status/${submissionId}`)
      .send({ tid: 1, status: "Error" })
      .then((res) => {
        expect(res.statusCode).to.equal(200);
        expect(res.body.message).to.equal(
          `Submission ${submissionId} successfully updated to status Error`
        );
        done();
      })
      .catch((err) => done(err));
  });

  it("sid does not exist", (done) => {
    request(server)
      .patch("/api/tasks/status/2")
      .send({ tid: 1, status: "Error" })
      .then((res) => {
        expect(res.statusCode).to.equal(404);
        done();
      })
      .catch((err) => done(err));
  });

  it("invalid tid", (done) => {
    request(server)
      .patch("/api/tasks/status/howdy")
      .send({ tid: 1, status: "Error" })
      .then((res) => {
        expect(res.statusCode).to.equal(400);
        done();
      })
      .catch((err) => done(err));
  });

  it("invalid status", (done) => {
    request(server)
      .patch(`/api/submissions/status/${submissionId}`)
      .send({ status: "pending" })
      .then((res) => {
        expect(res.statusCode).to.equal(400);
        done();
      })
      .catch((err) => done(err));
  });
});
