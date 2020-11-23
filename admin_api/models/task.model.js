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
      default: "markus-connector",
    },
    submissions: [Submission],
    num_submissions: {
      type: Number,
      default: 0,
    },
    max_marks: {
      type: Number,
      default: 0,
    },
    max_marks_per_question: [Number],
    marking_type: {
      type: String,
      default: "",
    },
    question_names: [String],
    submission_file_name: {
      type: String,
      default: "",
    },
    create_tables: {
      type: String,
      default: "",
    },
    create_trigger: {
      type: String,
      default: "",
    },
    create_function: {
      type: String,
      default: "",
    },
    load_data: {
      type: String,
      default: "",
    },
    solutions: {
      type: String,
      default: "",
    },
    submissions_path: {
      type: String,
      default: "",
    },
    timeout: {
      type: Number,
      default: 0,
      max: 300,
    },
    db_type: {
      type: String,
      default: "mysql",
    },
    extra_fields: {},
  },
  {
    timestamps: true,
  }
);

Task.plugin(server.autoIncrement.plugin, { model: "Task", field: "tid" });
module.exports = { Task: mongoose.model("Task", Task), TaskSchema: Task };
