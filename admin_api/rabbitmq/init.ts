import amqplib, { Channel } from "amqplib";
import setupRabbitMQConsumers from "./consumers/index";

export async function createClient(
  host: string,
  username: string,
  password: string
) {
  let connected = false;
  let connection;
  while (!connected) {
    try {
      console.log("Trying to connect to rabbitmq server");
      connection = await amqplib.connect(
        `amqp://${username}:${password}@${host}`
      );
    } catch (err) {
      console.log(
        "Connection to rabbitmq server failed, trying again in 5 seconds"
      );
      await sleep(5000);
      continue;
    }
    console.log("Successfully connected to rabbitmq instance 🐇");
    connected = true;
  }
  if (connection == undefined) throw Error("Connection still undefined");
  const channel = await connection.createChannel();

  setupRabbitMQConsumers(channel);

  return channel;
}

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
