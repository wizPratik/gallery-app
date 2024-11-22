import { recognize } from 'tesseract.js';

export const processImage = async (imageUrl) => {
  try {
    const { data } = await recognize(imageUrl, 'eng');
    return data.text;
  } catch (error) {
    console.error('Error processing image:', error);
    return 'Processing failed';
  }
};