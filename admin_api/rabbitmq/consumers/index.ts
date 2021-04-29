import { Channel } from "amqplib";

import initGradesConsumer from "./grades.consumer";
import initLogsConsumer from "./logs.consumer";
import initStatusConsumer from "./status.consumer";

export default async function setupRabbitMQConsumers(channel: Channel) {
  initGradesConsumer(channel);
  initLogsConsumer(channel);
  initStatusConsumer(channel);
}
