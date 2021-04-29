import { Channel } from "amqplib";
import { TaskMessage } from "../constants";


function publishTaskToMark(channel: Channel, taskMessage: TaskMessage) {
	channel.sendToQueue(
		"task_to_mark",
		Buffer.from(JSON.stringify(taskMessage))
	);
}

export default { publishTaskToMark };
