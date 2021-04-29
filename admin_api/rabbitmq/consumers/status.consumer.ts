import { Channel } from "amqplib";
import { StatusMessage } from "../../constants";
import { statusService } from "../../services/index";

export default function initLogsConsumer(channel: Channel) {
	channel.assertQueue("status");
	channel.consume("status", async (message) => {
		if (message == null) throw Error("Null message received in log queue");
		const statusMessage: StatusMessage = JSON.parse(
			message.content.toString()
		);

		console.log(`Received status message`);
		console.log({ statusMessage });

		if (!("tid" in statusMessage && statusMessage.status)) {
			console.log(`Improper format, ignoring`);
			return;
		}

		try {
			await statusService.handleStatusMessage(channel, statusMessage);
		} catch (e) {
			console.error(e);
		} finally {
			channel.ack(message);
		}
	});
}
