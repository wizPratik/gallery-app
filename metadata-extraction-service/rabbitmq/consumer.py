import pika
from config import RABBITMQ_HOST, RABBITMQ_PORT, INBOUND_QUEUE
from rabbitmq.callback import callback

def start_consumer():
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST, port=RABBITMQ_PORT))
    channel = connection.channel()
    channel.queue_declare(queue=INBOUND_QUEUE, durable=True)
    channel.basic_consume(queue=INBOUND_QUEUE, on_message_callback=callback, auto_ack=True)
    print(f"Listening for messages on {INBOUND_QUEUE}...")
    channel.start_consuming()
