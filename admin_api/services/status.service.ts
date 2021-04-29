import { StatusMessage, TaskMessage, LogMessage } from "../constants";
import { logsService, tasksService } from "./index";
import { Task } from "../models/task.model";
import { Channel } from "amqplib";
import { NotFoundError } from "../errors";

async function handleStatusMessage(
	channel: Channel,
	statusMessage: StatusMessage
) {
	let tid = statusMessage.tid;
	let status = statusMessage.status;

	let task = await Task.findOne({ tid: tid });

	if (task == null) throw new NotFoundError(`Task with tid ${tid} not found`);

	await Task.findOneAndUpdate({ tid: tid }, { $set: { status: status } });
	if (status === "Downloaded") {
		let log: LogMessage = {message: "Finished downloading submissions, initiating marking", source: "api", tid}
		logsService.addLog(log)
		
		// Start marking of assignment
		if (!task.initFile || !task.solutions) {
			throw new Error("Files were not downloaded");
		}
		const taskToMarkMessage: TaskMessage = {
			tid: tid,
			assignment_name: task.name,
			max_marks: task.max_marks,
			max_marks_per_question: task.max_marks_per_question,
			marking_type: task.marking_type,
			question_names: task.question_names,
			submission_file_name: task.submission_file_name,
			init: task.initFile,
			solutions: task.solutions,
			submissions: `/var/downloads/${tid}/`,
			db_type: task.db_type,
		};
		tasksService.publishTaskToMark(channel, taskToMarkMessage);
	}
}

export default { handleStatusMessage };
