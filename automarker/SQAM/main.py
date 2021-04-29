import asyncio
import aio_pika
import time
import json
from SQAM.publish import log_message, publish_status
from SQAM.task import Task
import SQAM.settings

def create_process_task_fn(channel: aio_pika.RobustChannel):

    # Return a function that takes in a message with channel curried into it
    async def process_task(message: aio_pika.IncomingMessage):
        async with message.process(ignore_processed=True):
            config = json.loads(message.body.decode())
            message.ack()
            await grade_task(channel, config)
    return process_task


async def grade_task(channel: aio_pika.RobustChannel, config):
    try:
        task = Task(channel, config)
        await task.run()
        await publish_status(config['tid'], "Complete", channel)
    except Exception as e:
        print(e, flush=True)
        await log_message(config["tid"], f"Error when marking: {str(e)}", channel)
        await publish_status(config['tid'], "Error", channel)

async def main(loop, username, password, host):
    connection = await aio_pika.connect_robust(
        host=host, login=username, password=password, loop=loop
    )
    # Creating channel
    channel = await connection.channel()

    # Maximum message count which will be processing at the same time.
    await channel.set_qos(prefetch_count=5)

    # Declaring queues
    tasks_queue = await channel.declare_queue("task_to_mark", durable=True)
    await tasks_queue.consume(create_process_task_fn(channel), no_ack=False)

    return connection

def start_loop():
    loop = asyncio.get_event_loop()
    connected = False
    while not connected:
        print("Attempting to connect to rabbitmq instance")
        try:
            connection = loop.run_until_complete(
                main(
                    loop,
                    host=SQAM.settings.RABBITMQ_HOST,
                    username=SQAM.settings.RABBITMQ_USERNAME,
                    password=SQAM.settings.RABBIT_MQ_PASSWORD,
                )
            )
        except ConnectionError:
            print("Connection failed, retrying in 5 seconds")
            time.sleep(5)
            continue

        print("Successfully connected to rabbitmq instance 🐇")
        connected = True

    try:
        loop.run_forever()
    finally:
        loop.run_until_complete(connection.close())

if __name__ == "__main__":
    start_loop()