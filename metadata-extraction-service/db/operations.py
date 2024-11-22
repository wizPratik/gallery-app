from db.connection import create_connection

def insert_metadata(image_id, location, width, height, img_format, mode):
    conn = create_connection()
    cursor = conn.cursor()
    try:
        cursor.execute("""
            INSERT INTO image_metadata (image_id, location, width, height, format, mode)
            VALUES (%s, %s, %s, %s, %s, %s)
        """, (image_id, location, width, height, img_format, mode))
        conn.commit()
    finally:
        cursor.close()

def fetch_metadata(image_id):
    conn = create_connection()
    cursor = conn.cursor(dictionary=True)
    try:
        cursor.execute("SELECT * FROM image_metadata WHERE image_id = %s", (image_id,))
        return cursor.fetchone()
    finally:
        cursor.close()
