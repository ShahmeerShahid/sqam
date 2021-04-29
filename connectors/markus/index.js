import {createClient} from "./rabbitmq/setupClient.js";

const rabbitmq_host = process.env.RABBITMQ_HOST
const rabbitmq_username = process.env.RABBITMQ_USERNAME
const rabbitmq_password = process.env.RABBITMQ_PASSWORD

const rabbitMQClient = createClient(rabbitmq_host, rabbitmq_username, rabbitmq_password)

