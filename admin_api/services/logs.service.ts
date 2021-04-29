import { LogMessage } from "../constants";
import { Task, Log } from "../models/task.model";
import { NotFoundError } from "../errors";
import { Mongoose } from "mongoose";

async function addLog(log: LogMessage) {
	let tid = log.tid;
	let logToAppend = new Log({
		timestamp: new Date(),
		text: log.message,
		source: log.source,
	});

	const filter = { tid };
	const update = { $push: { logs: logToAppend } };
	try {
		Task.findOneAndUpdate(filter, update).orFail();
	} catch (err) {
		throw new NotFoundError(`Task with tid ${tid} not found`);
	}
}

export default { addLog };
