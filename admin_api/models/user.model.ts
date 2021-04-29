import mongoose from "mongoose";
const Schema = mongoose.Schema;

// TODO: Convert to ts-mongoose like in task.model.ts
let User = new Schema({
  id: {
    type: Number,
    default: 0,
    required: true,
  },
  username: {
    type: String,
    required: true,
  },
  password: {
    type: String,
    required: true,
  },
  email: {
    type: String,
    required: true,
  },
});
module.exports = mongoose.model("User", User);
