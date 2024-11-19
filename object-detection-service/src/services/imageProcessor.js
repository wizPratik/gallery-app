const Tesseract = require('tesseract.js');

const processImage = async (imageUrl) => {
  try {
    const { data } = await Tesseract.recognize(imageUrl, 'eng');
    return data.text;
  } catch (error) {
    console.error('Error processing image:', error);
    return 'Processing failed';
  }
};

module.exports = { processImage };