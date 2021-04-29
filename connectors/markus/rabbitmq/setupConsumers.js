import {
	publishLogMessage,
	publishErrorStatus,
} from "../lib/rabbitmq_helpers.js";
import { downloadSubmissions } from "../lib/downloader.js";

export function setupDownloadsConsumer(channel) {
	const connector_name = process.env.CONNECTOR_NAME || "Markus";
	const queue_name = `task_to_download_${connector_name}`;
	console.log(`Consuming on queue ${queue_name}`);
	channel.assertQueue(queue_name);
	channel.consume(queue_name, async (message) => {
		if (message == null)
			throw Error("Null message received in task_to_download queue");

		const taskMessage = JSON.parse(message.content.toString());
		console.log(taskMessage);
		if (!("tid" in taskMessage)) {
			console.log(`Missing TID in message, ignoring`);
			return;
		}
		const tid = taskMessage.tid;
		channel.ack(message);
		if (
			!(
				taskMessage.download_directory &&
				taskMessage.extra_fields &&
				taskMessage.extra_fields.markus_URL &&
				taskMessage.extra_fields.assignment_id &&
				taskMessage.extra_fields.api_key
			)
		) {
			console.log(`Message was missing fields`);
			publishLogMessage(
				channel,
				"Connector received bad message from admin, missing fields",
				tid
			);
			publishErrorStatus(channel, tid);
			return;
		}

		const { download_directory, extra_fields } = taskMessage;
		const { markus_URL, assignment_id, api_key } = extra_fields;
		await downloadSubmissions(
			tid,
			download_directory,
			markus_URL,
			assignment_id,
			api_key,
			channel
		);
	});
}
