require("dotenv").config();
const express = require("express");
const fileUpload = require("express-fileupload");
const app = express();
const cors = require("cors");
var autoIncrement = require("mongoose-auto-increment");
const morgan = require("morgan");

app.use(cors());
app.use(morgan("common")); // For logging
app.use(express.json());
app.use(fileUpload());

const mongooseConnect = require("./helpers");
const connection = mongooseConnect.dbConnect();
autoIncrement.initialize(connection);

exports.autoIncrement = autoIncrement;

const tasksRouter = require("./routes/tasks.router");
app.use("/api/tasks", tasksRouter);

const submissionsRouter = require("./routes/submissions.router");
app.use("/api/submissions", submissionsRouter);

const connectorsRouter = require("./routes/connectors.router");
app.use("/api/connectors", connectorsRouter);

const port = process.env.PORT || 9000; // Port 80 if started by docker-compose

app.listen(port, function () {
  console.log("Server is running on port: " + port);
});

module.exports = app;
