import json
import pika
from config import RABBITMQ_HOST, RABBITMQ_PORT, OUTBOUND_QUEUE

def publish_message(message):
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST, port=RABBITMQ_PORT))
    channel = connection.channel()
    channel.queue_declare(queue=OUTBOUND_QUEUE, durable=True)
    channel.basic_publish(
        exchange='',
        routing_key=OUTBOUND_QUEUE,
        body=json.dumps(message),
        properties=pika.BasicProperties(content_type="application/json")
    )
    print(f"Published message to {OUTBOUND_QUEUE}: {message}")
    connection.close()
