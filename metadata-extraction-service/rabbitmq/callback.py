import json
from services.metadata import process_message
from rabbitmq.publisher import publish_message

def callback(ch, method, properties, body):
    try:
        message = json.loads(body)
        image_id = message.get("image_id")
        location = message.get("location")

        if not image_id or not location:
            print("Invalid message format.")
            return

        process_message(image_id, location)

        success_message = {
            "image_id": image_id,
            "status": "true"
        }
        publish_message(success_message)
    except Exception as e:
        print(f"Error in callback: {e}")
