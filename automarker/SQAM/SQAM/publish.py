import aio_pika
import asyncio
import json

async def publish_status(tid, status, channel): 
    status_json = json.dumps({"tid": tid, "status": status})
    await channel.default_exchange.publish(
        aio_pika.Message(status_json.encode()), routing_key="status"
    )

async def log_message(tid, message, channel):
    print(f"[Tid {tid}]: {message}")
    log_json = json.dumps({"tid": tid, "source": "automarker", "message": message})
    await channel.default_exchange.publish(
        aio_pika.Message(log_json.encode()), routing_key="logs"
    )