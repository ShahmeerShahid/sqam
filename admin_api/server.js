require("dotenv").config();
const express = require("express");
const app = express();
const cors = require("cors");
const mongoose = require("mongoose");
const morgan = require("morgan");

app.use(cors());
app.use(morgan("common")); // For logging
app.use(express.json());

const dbAddress = process.env.PROD_DB_URL || "localhost";

mongoose.connect(`mongodb://${dbAddress}:27017/sqamadmin`, {
  useNewUrlParser: true,
  useUnifiedTopology: true,
  user: process.env.DB_USERNAME,
  pass: process.env.DB_PASSWD,
});

const connection = mongoose.connection;
connection.once("open", function () {
  console.log("MongoDB database connection established successfully");
});

const tasksRouter = require("./routes/tasks.router");
app.use("/api/tasks", tasksRouter);

const connectorsRouter = require("./routes/connectors.router");
app.use("/api/connectors", connectorsRouter);






const port = process.env.PORT || 9000; // Port 80 if started by docker-compose

app.listen(port, function () {
  console.log("Server is running on port: " + port);
});
