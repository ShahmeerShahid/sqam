require("dotenv").config();
import mongoose from "mongoose";

const dbAddress = process.env.PROD_DB_URL || "localhost";

function dbConnect() {
  mongoose.connect(`mongodb://${dbAddress}:27017/sqamadmin`, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
    useFindAndModify: false,
    user: process.env.DB_USERNAME,
    pass: process.env.DB_PASSWD,
  });

  const connection = mongoose.connection;
  connection.once("open", function () {
    console.log("MongoDB database connection established successfully");
  });
  return connection;
}

function dbClose() {
  return mongoose.disconnect();
}

export default { dbConnect, dbClose };
