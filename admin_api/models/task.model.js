const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const taskSchema = new Schema(
  {
    id: {
      type: Number,
      default: 0,
    },
    name: {
      type: String,
    },
    status: {
      type: String,
      enum: ["Pending", "Error", "Marking", "Complete"],
      default: "Pending",
    },
  },
  {
    timestamps: true,
  }
);

const Task = mongoose.model("Task", taskSchema);
module.exports = Task;
