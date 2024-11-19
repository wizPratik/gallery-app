const Image = require('../models/imageModel');

const getImageDescription = async (req, res) => {
  try {
    const { imageId } = req.params;
    const image = await Image.findOne({ imageId });

    if (!image) {
      return res.status(404).json({ message: 'Image not found' });
    }

    res.json({ imageId: image.imageId, description: image.description });
  } catch (error) {
    res.status(500).json({ message: 'Server error' });
  }
};

module.exports = { getImageDescription };