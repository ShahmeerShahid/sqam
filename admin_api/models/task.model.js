const mongoose = require("mongoose");
const server = require("../server");
const Schema = mongoose.Schema;
const constants = require("../constants");

const Submission = new Schema({
  name: {
    type: String,
    required: true,
  },
  status: {
    type: String,
    enum: constants.statuses,
    required: true,
    default: "Pending",
  },
});

const Task = new Schema(
  {
    tid: {
      type: Number,
      unique: true,
      default: 1,
    },
    name: {
      type: String,
      required: true,
    },
    status: {
      type: String,
      enum: constants.statuses,
      required: true,
      default: "Pending",
    },
    connector: {
      type: String,
      enum: constants.connectors,
      required: true,
      default: "markus-connector",
    },
    submissions: [Submission],
    num_submissions: {
      type: Number,
      required: true,
      default: 0,
    },
    extra_fields: {},
  },
  {
    timestamps: true,
  }
);

Task.plugin(server.autoIncrement.plugin, { model: "Task", field: "tid" });
module.exports = { Task: mongoose.model("Task", Task), TaskSchema: Task };
