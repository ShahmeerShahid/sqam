const mongoose = require("mongoose");
const Schema = mongoose.Schema;
const server = require("../server");

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
    extra_fields: [
      new Schema(
        {
          markus_URL: {
            type: String,
          },
          assignment_id: {
            type: Number,
          },
          api_key: {
            type: String,
          },
        },
        { strict: false }
      ),
    ],
  },
  {
    timestamps: true,
  }
);

taskSchema.plugin(server.autoIncrement.plugin, { model: "Task", field: "id" });
const Task = mongoose.model("Task", taskSchema);
module.exports = Task;
