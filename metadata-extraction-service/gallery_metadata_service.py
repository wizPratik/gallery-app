from flask import Flask, request, jsonify
import mysql.connector
import json
import os
import pika  # Import pika for RabbitMQ
from PIL import Image
import time
from mysql.connector import Error

app = Flask(__name__)

# RabbitMQ configuration
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "rabbitmq")  # Use the Docker service name

# Global variable for database connection
db_connection = None

# Function to create a database connection with retries
def create_connection():
    global db_connection  # Declare db_connection as global
    for _ in range(5):  # Retry up to 5 times
        try:
            db_connection = mysql.connector.connect(
                host='mysql',  # Use the service name defined in docker-compose
                user='YOUR_USERNAME',
                password="YOUR_PASSWORD",  # Replace with your MySQL password
                database='gallery_database'  # Replace with your database name
            )
            print("Connection to MySQL DB successful")
            return
        except Error as e:
            print(f"The error '{e}' occurred. Retrying in 5 seconds...")
            time.sleep(5)
    db_connection = None  # Ensure db_connection is set to None on failure

def extract_metadata(file):
    """Extracts metadata from the image file and returns it in dictionary form."""
    try:
        image = Image.open(file)
        return {
            "width": image.width,
            "height": image.height,
            "format": image.format,
            "mode": image.mode
        }
    except Exception as e:
        print(f"Error extracting metadata: {e}")
        return {}

# Function to process image metadata and save to the database
def process_message(image_id, location):
    """Processes the message and saves image metadata into individual columns in the database."""
    global db_connection
    cursor = None

    try:
        if db_connection is None:
            create_connection()

        if db_connection is None:
            print("Database connection failed.")
            return

        # Open the image file to extract metadata
        with open(location, 'rb') as img_file:
            metadata = extract_metadata(img_file)

        # Extract individual metadata fields
        width = metadata.get('width')
        height = metadata.get('height')
        img_format = metadata.get('format')
        mode = metadata.get('mode')

        # Insert metadata into individual columns
        cursor = db_connection.cursor()
        cursor.execute("""
            INSERT INTO image_metadata (image_id, location, width, height, format, mode)
            VALUES (%s, %s, %s, %s, %s, %s)
        """, (image_id, location, width, height, img_format, mode))
        db_connection.commit()
        print(f"Metadata for {image_id} uploaded successfully.")

    except Exception as e:
        print(f"Error processing message: {e}")

    finally:
        if cursor:
            cursor.close()

# RabbitMQ setup
def callback(ch, method, properties, body):
    """Callback function for processing incoming RabbitMQ messages."""
    try:
        message = json.loads(body)  # Load the JSON message
        image_id = message.get('image_id')  # Extract image_id
        location = message.get('location')  # Extract location

        if image_id and location:
            process_message(image_id, location)  # Call the processing function
        else:
            print("Invalid message received. Missing image_id or location.")
    except json.JSONDecodeError:
        print("Error decoding JSON message.")
    except Exception as e:
        print(f"Error in callback: {e}")

def consume_messages():
    """Set up RabbitMQ consumer to listen for messages."""
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
    channel = connection.channel()
    channel.queue_declare(queue='image_metadata_queue')  # Ensure the queue exists
    channel.basic_consume(queue='image_metadata_queue', on_message_callback=callback, auto_ack=True)
    print('Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()

# Upload Image Metadata
@app.route('/api/metadata', methods=['POST'])
def upload_metadata():
    if 'file' not in request.files:
        return jsonify({"error": "No file part in the request"}), 400

    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No file selected"}), 400

    image_id = request.form.get('image_id')
    location = request.form.get('location')

    try:
        # Create the directory if it doesn't exist
        os.makedirs(os.path.dirname(location), exist_ok=True)

        # Save the file to the specified location
        file.save(location)

        # Check if the saved file is a valid image
        with open(location, 'rb') as img_file:
            metadata = extract_metadata(img_file)

        # Try to process the message to insert metadata into the database
        try:
            process_message(image_id, location)
        except Exception as process_error:
            print(f"Error processing message: {process_error}")
            return jsonify({"error": "Failed to process the metadata"}), 500

        return jsonify({"message": "Metadata uploaded successfully"}), 201

    except Exception as e:
        print(f"Error occurred: {e}")
        return jsonify({"error": "Internal Server Error"}), 500

# Get Image Metadata
@app.route('/api/metadata/<image_id>', methods=['GET'])
def get_metadata(image_id):
    print(f"Received request for image_id: {image_id}")  # Debugging

    cursor = db_connection.cursor(dictionary=True)
    try:
        cursor.execute("SELECT * FROM image_metadata WHERE image_id = %s", (image_id,))
        metadata = cursor.fetchone()
        print(f"Fetched metadata: {metadata}")  # Debugging

        if metadata:
            return jsonify(metadata), 200
        else:
            print("No metadata found.")  # Debugging
            return jsonify({"error": "Metadata not found"}), 404
    except Exception as e:
        print(f"Error occurred: {e}")  # Debugging
        return jsonify({"error": "Internal Server Error"}), 500
    finally:
        cursor.close()

# Update Image Metadata
@app.route('/api/metadata/<image_id>', methods=['PUT'])
def update_metadata(image_id):
    data = request.json
    print(f"Received data for update: {data}")  # Debugging

    location = data.get('location')
    metadata = data.get('metadata')

    cursor = db_connection.cursor()
    try:
        cursor.execute("UPDATE image_metadata SET location = %s WHERE image_id = %s",
                       (location, image_id))
        db_connection.commit()

        if cursor.rowcount > 0:
            print("Metadata updated successfully.")  # Debugging
            return jsonify({"message": "Metadata updated successfully"}), 200
        else:
            print("No metadata found for update.")  # Debugging
            return jsonify({"error": "Metadata not found"}), 404
    except Exception as e:
        print(f"Error occurred: {e}")  # Debugging
        return jsonify({"error": "Internal Server Error"}), 500
    finally:
        cursor.close()

# Delete Image Metadata
@app.route('/api/metadata/<image_id>', methods=['DELETE'])
def delete_metadata(image_id):
    print(f"Received request to delete image_id: {image_id}")  # Debugging
    global db_connection  # Ensure to access the global connection variable

    cursor = None  # Initialize cursor to None
    try:
        # Ensure the database connection is established
        if db_connection is None:
            create_connection()

        if db_connection is None:
            print("Database connection failed.")
            return jsonify({"error": "Database connection failed"}), 500

        cursor = db_connection.cursor()
        cursor.execute("DELETE FROM image_metadata WHERE image_id = %s", (image_id,))
        db_connection.commit()

        if cursor.rowcount > 0:
            print("Metadata deleted successfully.")  # Debugging
            return jsonify({"message": "Metadata deleted successfully"}), 200
        else:
            print("No metadata found for deletion.")  # Debugging
            return jsonify({"error": "Metadata not found"}), 404
    except Exception as e:
        print(f"Error occurred: {e}")  # Debugging
        return jsonify({"error": "Internal Server Error"}), 500
    finally:
        if cursor:
            cursor.close()

if __name__ == '__main__':
    # Start RabbitMQ consumer in a separate thread
    import threading
    threading.Thread(target=consume_messages, daemon=True).start()

    # Start Flask app
    app.run(host='0.0.0.0', port=5001)