import { Channel } from "amqplib";
import { TaskMessage } from "../constants";

// TODO: Migrate all business logic from routes/tasks.router.ts to here

function publishTaskToMark(channel: Channel, taskMessage: TaskMessage) {
	channel.sendToQueue(
		"task_to_mark",
		Buffer.from(JSON.stringify(taskMessage))
	);
}

export default { publishTaskToMark };
