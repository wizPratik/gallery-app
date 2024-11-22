import requests
from PIL import Image
from io import BytesIO
from db.operations import insert_metadata

def download_image(url):
    response = requests.get(url, timeout=10)
    response.raise_for_status()
    return BytesIO(response.content)

def extract_metadata(file):
    image = Image.open(file)
    return {
        "width": image.width,
        "height": image.height,
        "format": image.format,
        "mode": image.mode
    }

def process_message(image_id, location):
    file = download_image(location)
    metadata = extract_metadata(file)
    insert_metadata(
        image_id,
        location,
        metadata["width"],
        metadata["height"],
        metadata["format"],
        metadata["mode"]
    )
