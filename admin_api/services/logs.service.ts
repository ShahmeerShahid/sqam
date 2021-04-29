import { LogMessage } from "../constants";
import { Task, Log } from "../models/task.model";
import { NotFoundError } from "../errors";

async function addLog(log: LogMessage) {
	let tid = log.tid;
	let logToAppend = new Log({
		timestamp: new Date(),
		text: log.message,
		source: log.source,
	});

	await new Promise((resolve, reject) => {
		Task.findOneAndUpdate(
			{ tid: tid },
			{ $push: { logs: logToAppend } },
			function (err, doc) {
				if (err) {
					reject(err);
				} else if (doc === null) {
					reject(new NotFoundError(`Task with tid ${tid} not found`));
				} else {
					resolve(doc);
				}
			}
		);
	});
}

export default { addLog };
