require("dotenv").config();
const express = require("express");
const app = express();
const cors = require("cors");
const mongoose = require("mongoose");

app.use(cors());
app.use(express.json());

const serverUrl = process.env.PROD_DB_URL || "localhost";

mongoose.connect(`mongodb://${serverUrl}:27017/sqamadmin`, {
  useNewUrlParser: true,
  user: process.env.API_USERNAME,
  pass: process.env.API_PWD,
});

const connection = mongoose.connection;
connection.once("open", function () {
  console.log("MongoDB database connection established successfully");
});

const tasksRouter = require("./routes/tasks.router");
app.use("/api/tasks", tasksRouter);

const port = process.env.PORT || 9000; // Port 80 if started by docker-compose

app.listen(port, function () {
  console.log("Server is running on port: " + port);
});
