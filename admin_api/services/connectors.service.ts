import { Channel } from "amqplib";
import { TaskDoc } from "../models/task.model";

async function publishTaskToDownload(task: TaskDoc, channel: Channel) {
	const message = {
		tid: task.tid,
		download_directory: `/var/downloads/${task.tid}/`,
		extra_fields: task.extra_fields,
	};
	channel.sendToQueue(
		`task_to_download_${task.connector}`,
		Buffer.from(JSON.stringify(message))
	);
}

export default {
	publishTaskToDownload,
};
