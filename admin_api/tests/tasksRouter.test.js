const expect = require("chai").expect;
const request = require("supertest");
const setup = require("./setup");
const server = require("../server");
const Task = require("../models/task.model");

describe("GET /tasks", () => {
  const data = { name: "CSC343 A1", status: "Pending" };

  setup();
  beforeEach((done) => {
    new Task(data)
      .save()
      .then(() => done())
      .catch((err) => done(err));
  });

  it("existing data", (done) => {
    request(server)
      .get("/api/tasks")
      .then((res) => {
        expect(res.statusCode).to.equal(200);
        expect(res.body[0]).to.include(data);
        done();
      })
      .catch((err) => done(err));
  });
});

describe("POST /tasks", () => {
  setup();

  it("valid data", (done) => {
    const data = { name: "CSC343 A1", status: "Pending" };
    request(server)
      .post("/api/tasks")
      .send(data)
      .then((res) => {
        expect(res.statusCode).to.equal(201);
        expect(res.body).to.include(data);
        done();
      })
      .catch((err) => done(err));
  });

  it("empty name", (done) => {
    const data = { name: "", status: "Pending" };
    request(server)
      .post("/api/tasks")
      .send(data)
      .then((res) => {
        expect(res.statusCode).to.equal(400);
        done();
      })
      .catch((err) => done(err));
  });

  it("invalid status", (done) => {
    const data = { name: "CSC343 A2", status: "pending" };
    request(server)
      .post("/api/tasks")
      .send(data)
      .then((res) => {
        expect(res.statusCode).to.equal(400);
        done();
      })
      .catch((err) => done(err));
  });
});

describe("PATCH /status/:tid", () => {
  const data = { tid: 1, name: "CSC343 A1", status: "Pending" };

  setup();
  beforeEach((done) => {
    new Task(data)
      .save()
      .then(() => done())
      .catch((err) => done(err));
  });

  it("standard update", (done) => {
    request(server)
      .patch("/api/tasks/status/1")
      .send({ status: "Error" })
      .then((res) => {
        expect(res.statusCode).to.equal(200);
        expect(res.body.message).to.equal(
          `Task 1 successfully updated to status Error`
        );
        done();
      })
      .catch((err) => done(err));
  });

  it("tid does not exist", (done) => {
    request(server)
      .patch("/api/tasks/status/2")
      .send({ status: "Error" })
      .then((res) => {
        expect(res.statusCode).to.equal(404);
        done();
      })
      .catch((err) => done(err));
  });

  it("invalid tid", (done) => {
    request(server)
      .patch("/api/tasks/status/howdy")
      .send({ status: "Error" })
      .then((res) => {
        expect(res.statusCode).to.equal(400);
        done();
      })
      .catch((err) => done(err));
  });

  it("invalid status", (done) => {
    request(server)
      .patch("/api/tasks/status/1")
      .send({ status: "pending" })
      .then((res) => {
        expect(res.statusCode).to.equal(400);
        done();
      })
      .catch((err) => done(err));
  });
});
