from flask import Blueprint, jsonify
from db.operations import fetch_metadata

metadata_bp = Blueprint("metadata_extraction", __name__, url_prefix="/api/metadata")

@metadata_bp.route("/<image_id>", methods=["GET"])
def get_metadata(image_id):
    metadata = fetch_metadata(image_id)
    if metadata:
        return jsonify(metadata), 200
    else:
        return jsonify({"error": "Metadata not found"}), 404
