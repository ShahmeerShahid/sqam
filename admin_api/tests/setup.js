const mongoose = require("mongoose");
const mongooseConnect = require("../helpers");

let setup = () => {
  before((done) => {
    mongooseConnect
      .dbConnect()
      .once("open", () => done())
      .on("error", (error) => done(error));
  });

  beforeEach((done) => {
    mongoose.connection.db
      .listCollections({ name: "tasks" })
      .next((error, collection) => {
        if (collection) {
          mongoose.connection.db
            .dropCollection("tasks")
            .then(() => done())
            .catch((err) => done(err));
        } else {
          done(error);
        }
      });
  });

  after((done) => {
    mongooseConnect
      .dbClose()
      .then(() => done())
      .catch((err) => done(err));
  });
};

module.exports = setup;
