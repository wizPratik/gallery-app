import { Image } from "../models";

const getImageDescription = async (req, res) => {
	try {
		const { imageId } = req.params;
		const image = await Image.findOne({ imageId });

		if (!image) {
			return res.status(404).json({ message: "Image not found" });
		}

		res.json({ imageId: image.imageId, description: image.description });
	} catch (error) {
		console.error(error);
		res.status(500).json({ message: "Server error" });
	}
};

export default { getImageDescription };
