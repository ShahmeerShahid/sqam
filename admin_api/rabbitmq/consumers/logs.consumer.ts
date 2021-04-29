import { Channel } from "amqplib";
import { LogMessage } from "../../constants";
import { logsService } from "../../services/index";

export default function initLogsConsumer(channel: Channel) {
  channel.assertQueue("logs");
  channel.consume("logs", async (message) => {
    if (message == null) throw Error("Null message received in log queue");
    const log: LogMessage = JSON.parse(message.content.toString());

    console.log("Received logMessage");
    console.log({ logMessage: log });

    if (!("tid" in log && log.source && log.message)) {
      console.log(`Improper format, ignoring`);
      return;
    }

    try {
      await logsService.addLog(log);
    } catch (e) {
      console.error(e);
    } finally {
      channel.ack(message);
    }
  });
}
