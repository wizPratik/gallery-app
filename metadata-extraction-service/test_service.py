import pika
import json

# RabbitMQ connection details
RABBITMQ_HOST = 'localhost'
QUEUE_NAME = 'image_queue'

# Test message with an example image ID and path
message = {
    "image_id": "1",                # Set a unique ID for the test image
    "image_path": "test_img.jpeg"  # Replace with an actual path to an image
}

# Connect to RabbitMQ and send the message
def send_message():
    connection = pika.BlockingConnection(pika.ConnectionParameters(RABBITMQ_HOST))
    channel = connection.channel()

    # Ensure the queue exists
    channel.queue_declare(queue=QUEUE_NAME)

    # Publish the message to the queue
    channel.basic_publish(
        exchange='',
        routing_key=QUEUE_NAME,
        body=json.dumps(message)
    )
    print(f"Sent message to RabbitMQ: {message}")

    # Close the connection
    connection.close()

if __name__ == "__main__":
    send_message()