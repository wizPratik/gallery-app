import mysql.connector
from mysql.connector import Error
from config import MYSQL_CONFIG
import time

db_connection = None

def create_connection():
    global db_connection
    for _ in range(5):  # Retry up to 5 times
        try:
            db_connection = mysql.connector.connect(**MYSQL_CONFIG)
            print("Connected to MySQL database successfully.")
            return db_connection
        except Error as e:
            print(f"Error connecting to MySQL: {e}. Retrying in 5 seconds...")
            time.sleep(5)
    return None
