import { Channel } from "amqplib";
import { GradesMessage } from "../../constants";
import { gradesService } from "../../services/index";

export default function initGradesConsumer(channel: Channel) {
	channel.assertQueue("grades");
	channel.consume("grades", (message) => {
		if (message == null)
			throw Error("Null message received in grades queue");
		const gradesMessage: GradesMessage = JSON.parse(
			message.content.toString()
		);

		console.log(`Received grades message`);
		console.log({ gradesMessage });

		if (!("tid" in gradesMessage)) {
			console.log(`Improper format, ignoring`);
			return;
		}

		try {
			gradesService.handleGrades(gradesMessage);
		} catch (e) {
			console.error(e);
		} finally {
			channel.ack(message);
		}
	});
}
