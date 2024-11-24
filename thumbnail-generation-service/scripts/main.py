# requirements.txt
fastapi==0.104.1
python-multipart==0.0.6
uvicorn==0.24.0
Pillow==10.1.0
python-dotenv==1.0.0

# main.py
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.responses import FileResponse
from PIL import Image
import io
import os
from pathlib import Path
from typing import List
from datetime import datetime

app = FastAPI(title="Thumbnail Service")

# Configuration
UPLOAD_DIR = Path("uploads")
THUMBNAIL_DIR = Path("thumbnails")
THUMBNAIL_SIZES = [(100, 100), (200, 200), (300, 300)]  # Configurable sizes
ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"}

# Create directories if they don't exist
UPLOAD_DIR.mkdir(exist_ok=True)
THUMBNAIL_DIR.mkdir(exist_ok=True)

def create_thumbnail(image: Image.Image, size: tuple) -> Image.Image:
    """Create a thumbnail while maintaining aspect ratio"""
    thumbnail = image.copy()
    thumbnail.thumbnail(size, Image.Resampling.LANCZOS)
    return thumbnail

def get_unique_filename(original_filename: str) -> str:
    """Generate a unique filename using timestamp"""
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    return f"{timestamp}_{original_filename}"

@app.post("/upload/")
async def upload_image(file: UploadFile = File(...)):
    """Upload an image and generate thumbnails"""
    # Validate file extension
    file_ext = Path(file.filename).suffix.lower()
    if file_ext not in ALLOWED_EXTENSIONS:
        raise HTTPException(
            status_code=400,
            detail=f"File type not allowed. Supported types: {', '.join(ALLOWED_EXTENSIONS)}"
        )
    
    try:
        # Read and validate image
        image_data = await file.read()
        image = Image.open(io.BytesIO(image_data))
        
        # Generate unique filename
        unique_filename = get_unique_filename(file.filename)
        file_path = UPLOAD_DIR / unique_filename
        
        # Save original image
        image.save(file_path)
        
        # Generate thumbnails
        thumbnails = {}
        for size in THUMBNAIL_SIZES:
            thumb = create_thumbnail(image, size)
            thumb_filename = f"thumb_{size[0]}x{size[1]}_{unique_filename}"
            thumb_path = THUMBNAIL_DIR / thumb_filename
            thumb.save(thumb_path)
            thumbnails[f"{size[0]}x{size[1]}"] = thumb_filename
        
        return {
            "message": "Image uploaded successfully",
            "original_image": unique_filename,
            "thumbnails": thumbnails
        }
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error processing image: {str(e)}")

@app.get("/thumbnail/{size}/{filename}")
async def get_thumbnail(size: str, filename: str):
    """Retrieve a specific thumbnail"""
    thumbnail_path = THUMBNAIL_DIR / f"thumb_{size}_{filename}"
    if not thumbnail_path.exists():
        raise HTTPException(status_code=404, detail="Thumbnail not found")
    return FileResponse(thumbnail_path)

@app.get("/image/{filename}")
async def get_original_image(filename: str):
    """Retrieve the original image"""
    image_path = UPLOAD_DIR / filename
    if not image_path.exists():
        raise HTTPException(status_code=404, detail="Image not found")
    return FileResponse(image_path)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)