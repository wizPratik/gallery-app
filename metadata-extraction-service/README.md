1. REQUIRED CODE CHANGES FOR USER:
	1. In Dockerfile:
    	MYSQL_ROOT_PASSWORD: YOUR_PASSWORD -> Update with your sql password
	2. In gallery_metadat_service.py
		In function create_connection() update with your credentials -> user='YOUR_USERNAME',
                							password="YOUR_PASSWORD"

2. SETUP USING DOCKER (this will launch RabbitMQ and Mysql)
docker-compose up --build

3. RABBITMQ AND ENDPOINT:
	RabbitMQ : http://localhost:15672/
	http://127.0.0.1:5001/api/metadata/<id>  (for e.g., http://127.0.0.1:5001/api/metadata/1)

4. MYSQL DB  (init.sql is used to create the required table automatically)

CREATE DATABASE gallery_database;
USE gallery_database;

CREATE TABLE image_metadata (
    image_id VARCHAR(255) PRIMARY KEY,
    format VARCHAR(50),
    width INT,
    height INT,
    mode VARCHAR(50),
    exif_data JSON
);

5. TO RUN TEST (in separate terminal) OR YOU MAY USE CURL command for POST and/or DELETE and check
pip install pika
python test_service.py


6. API endpoints and usage

POST : Upload Image Metadata
curl -X POST http://127.0.0.1:5001/api/metadata -H "Content-Type: application/json" -d '{"image_id": "12345", "location": "/path/to/image.jpg", "metadata": {"width": 1920, "height": 1080, "format": "JPEG"}}'
	Endpoint: /api/metadata
	Method: POST
	Description: Accepts image metadata and saves it to the database.
	Request Body: JSON containing the image details (e.g., image_id, location, metadata).
	Response: Confirmation of successful upload or an error message.

GET
curl http://127.0.0.1:5001/api/metadata/1
curl http://127.0.0.1:5001/api/metadata/2
curl http://127.0.0.1:5001/api/metadata/3

Response if found:
{
  "image_id": "12345",
  "location": "/path/to/image.jpg",
  "metadata": {
    "width": 1920,
    "height": 1080,
    "format": "JPEG"
  }
}


PUT : Update Image Metadata
curl -X PUT http://127.0.0.1:5001/api/metadata/12345 -H "Content-Type: application/json" -d '{"location": "/new/path/to/image.jpg", "metadata": {"width": 1280, "height": 720, "format": "PNG"}}'
	Endpoint: /api/metadata/<image_id>
	Method: PUT or PATCH
	Description: Updates the metadata for a specific image based on its image_id.
	Request Body: JSON containing the updated metadata.
	Response: Confirmation of successful update or an error message.

DELETE :	Delete Image Metadata
curl -X DELETE http://127.0.0.1:5001/api/metadata/12345
	Endpoint: /api/metadata/<image_id>
	Method: DELETE
	Description: Deletes the metadata for a specific image based on its image_id.
	Response: Confirmation of successful deletion or an error message.