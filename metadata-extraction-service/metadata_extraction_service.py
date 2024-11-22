from flask import Flask
from routes.metadata import metadata_bp
from rabbitmq.consumer import start_consumer
import threading

app = Flask(__name__)

# Register API routes
app.register_blueprint(metadata_bp)

if __name__ == "__main__":
    # Start RabbitMQ consumer in a separate thread
    threading.Thread(target=start_consumer, daemon=True).start()

    # Run the Flask app
    app.run(host="0.0.0.0", port=5001)
