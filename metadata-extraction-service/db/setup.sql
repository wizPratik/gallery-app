CREATE DATABASE IF NOT EXISTS metadata_db;  -- Create the database if it doesn't exist

USE metadata_db;  -- Use the database

CREATE TABLE IF NOT EXISTS image_metadata (
    image_id VARCHAR(255) PRIMARY KEY,
    location VARCHAR(255),
    width INT,
    height INT,
    format VARCHAR(50),
    mode VARCHAR(50)
);
