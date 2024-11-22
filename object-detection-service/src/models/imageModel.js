import mongoose from 'mongoose';

const imageSchema = new mongoose.Schema({
  imageId: { type: String, required: true },
  description: { type: String, required: true },
});

export default mongoose.model('Image', imageSchema);