import os
import boto3
import pika
import requests
import logging
from PIL import Image
from io import BytesIO
from flask import Flask, send_file, jsonify

# Environment variables
INCOMING_QUEUE = os.getenv("INCOMING_QUEUE", "core.thumbnail.queue")
OUTGOING_QUEUE = os.getenv("OUTGOING_QUEUE", "thumbnail.core.queue")
S3_INPUT_BUCKET = os.getenv("S3_INPUT_BUCKET", "source-bucket")
S3_OUTPUT_BUCKET = os.getenv("S3_OUTPUT_BUCKET", "thumbnail-bucket")
AWS_REGION = os.getenv("AWS_REGION", "us-east-1")
LOCALSTACK_HOSTNAME = os.getenv("LOCALSTACK_HOSTNAME", "localhost")
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "localhost")

# Configure logging
logging.basicConfig(level=logging.INFO)

# S3 Client
s3_client = boto3.client(
    "s3",
    region_name=AWS_REGION,
    endpoint_url=f"http://{LOCALSTACK_HOSTNAME}:4566",
    aws_access_key_id="test",
    aws_secret_access_key="test",
)

# Flask App
app = Flask(__name__)


def create_thumbnail(image_url, output_bucket, image_id):
    try:
        # Download the image
        response = requests.get(image_url)
        response.raise_for_status()
        image = Image.open(BytesIO(response.content))

        # Create a thumbnail
        image.thumbnail((128, 128))

        # Save to BytesIO
        thumbnail_io = BytesIO()
        image.save(thumbnail_io, format=image.format)
        thumbnail_io.seek(0)

        # Upload thumbnail to S3
        content_type = f"image/{image.format.lower()}"
        s3_client.upload_fileobj(
            thumbnail_io,
            output_bucket,
            f"{image_id}_thumbnail",
            ExtraArgs={"ContentType": content_type},
        )
        logging.info(f"Uploaded thumbnail for {image_id} to {output_bucket}")
        return True
    except Exception as e:
        logging.error(f"Failed to process image {image_id}: {e}")
        return False


@app.route("/thumbnail/<image_id>", methods=["GET"])
def get_thumbnail(image_id):
    """Fetch and return the thumbnail for a given image_id."""

    thumbnail_key = f"{image_id}_thumbnail"

    try:
        # Fetch the thumbnail from S3
        response = s3_client.get_object(Bucket=S3_OUTPUT_BUCKET, Key=thumbnail_key)

        # Get the Content-Type
        content_type = response["ContentType"]

        # Return the image as a response
        return send_file(
            BytesIO(response["Body"].read()),
            mimetype=content_type,
            as_attachment=False,
            download_name=f"{image_id}_thumbnail",
        )
    except s3_client.exceptions.NoSuchKey:
        logging.error(f"Thumbnail not found for image_id: {image_id}")
        return jsonify({"error": "Thumbnail not found"}), 404
    except Exception as e:
        logging.error(f"Error fetching thumbnail for image_id {image_id}: {e}")
        return jsonify({"error": "Internal server error"}), 500


def process_message(ch, method, properties, body):
    import json

    logging.info(f"Received message: {body}")

    try:
        message = json.loads(body)
        if "image_id" not in message or "location" not in message:
            raise KeyError("Required keys missing in message")

        image_id = message["image_id"]
        location = message["location"]

        logging.info(f"Processing message for image_id: {image_id}")

        # Create thumbnail and upload to S3
        success = create_thumbnail(location, S3_OUTPUT_BUCKET, image_id)

        # Publish status to RabbitMQ
        status_message = {"image_id": image_id, "status": success}
        channel.basic_publish(
            exchange="",
            routing_key=OUTGOING_QUEUE,
            body=json.dumps(status_message),
        )

    except (json.JSONDecodeError, KeyError) as e:
        logging.error(f"Error processing message: {e}. Message body: {body}")

    finally:
        # Acknowledge the message to remove it from the queue
        ch.basic_ack(delivery_tag=method.delivery_tag)


# RabbitMQ Connection
connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
channel = connection.channel()

# Declare queue
channel.queue_declare(queue=INCOMING_QUEUE, durable=True)
channel.queue_declare(queue=OUTGOING_QUEUE, durable=True)

logging.info(f"Listening to queue: {INCOMING_QUEUE}")

# Start RabbitMQ Consumer in a Separate Thread
import threading


def start_rabbitmq_consumer():
    channel.basic_consume(queue=INCOMING_QUEUE, on_message_callback=process_message)
    channel.start_consuming()


consumer_thread = threading.Thread(target=start_rabbitmq_consumer, daemon=True)
consumer_thread.start()

# Start Flask Server
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
