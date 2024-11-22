import os

# RabbitMQ configuration
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "localhost")
RABBITMQ_PORT = int(os.getenv("RABBITMQ_PORT", 5672))
INBOUND_QUEUE = os.getenv("INBOUND_QUEUE", "core.metadata.queue")
OUTBOUND_QUEUE = os.getenv("OUTBOUND_QUEUE", "metadata.core.queue")

# MySQL configuration
MYSQL_CONFIG = {
    "host": os.getenv("MYSQL_HOST", "localhost"),
    "port": int(os.getenv("MYSQL_PORT", 3306)),
    "user": os.getenv("MYSQL_USER", "root"),
    "password": os.getenv("MYSQL_PASSWORD", "P4ssw0rd"),
    "database": os.getenv("MYSQL_DATABASE", "metadata_db"),
}

# Flask configuration
FLASK_PORT = int(os.getenv("FLASK_PORT", 5001))
