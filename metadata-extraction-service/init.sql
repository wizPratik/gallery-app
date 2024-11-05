CREATE DATABASE IF NOT EXISTS gallery_database;  -- Create the database if it doesn't exist
USE gallery_database;  -- Use the database

CREATE TABLE IF NOT EXISTS image_metadata (
    image_id VARCHAR(255) PRIMARY KEY,
    location VARCHAR(255),
    width INT,
    height INT,
    format VARCHAR(50),
    mode VARCHAR(50)
);