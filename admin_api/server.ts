require("dotenv").config();
import express from "express";
import fileUpload from "express-fileupload";
const app = express();
import cors from "cors";
import autoIncrement from "mongoose-auto-increment";
import morgan from "morgan";
import { createClient as createRabbitMQClient } from "./rabbitmq/init";
import { Channel } from "amqplib";
import { HTTPError } from "./errors";

app.use(cors());
app.use(morgan("common")); // For logging
app.use(express.json());
app.use(fileUpload());

import mongooseConnect from "./helpers";
const connection = mongooseConnect.dbConnect();
autoIncrement.initialize(connection);

// RabbitMQ initialization
const rabbitmq_host = process.env.RABBITMQ_HOST || "rabbitmq";
const rabbitmq_username = process.env.RABBITMQ_USERNAME || "guest";
const rabbitmq_password = process.env.RABBITMQ_PASSWORD || "guest";

const rabbitmqChannelPromise = createRabbitMQClient(
	rabbitmq_host,
	rabbitmq_username,
	rabbitmq_password
);

declare global {
	namespace Express {
		interface Request {
			rabbitmqChannelPromise: Promise<Channel>;
		}
	}
}

// Add rabbitmqClient to request object for use in middleware
app.use((req, res, next) => {
	req.rabbitmqChannelPromise = rabbitmqChannelPromise;
	next();
});

import tasksRouter from "./routes/tasks.router";
app.use("/api/tasks", tasksRouter);

import submissionsRouter from "./routes/submissions.router";
app.use("/api/submissions", submissionsRouter);

import connectorsRouter from "./routes/connectors.router";
app.use("/api/connectors", connectorsRouter);

app.use(
	(err: Error, req: express.Request, res: express.Response, next: any) => {
		if (err instanceof HTTPError) {
			return res.status(err.errorCode).json({ message: err.message });
		} else {
			return res
				.status(500)
				.json({ message: "Internal server error: " + err.message });
		}
	}
);

const port = process.env.PORT || 9000; // Port 80 if started by docker-compose

app.listen(port, function () {
	console.log("Server is running on port: " + port);
});

export default app;
