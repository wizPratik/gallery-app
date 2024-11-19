const { getChannel } = require('../config/rabbitmq');
const { processImage } = require('./imageProcessor');
const Image = require('../models/imageModel');

const startConsumer = async () => {
  const channel = getChannel();

  channel.consume(process.env.QUEUE_NAME, async (msg) => {
    if (msg !== null) {
      const { imageId, imageUrl } = JSON.parse(msg.content.toString());

      console.log(`Processing image ID: ${imageId}, URL: ${imageUrl}`);

      const description = await processImage(imageUrl);

      // Save to database
      const newImage = new Image({ imageId, description });
      await newImage.save();

      console.log(`Processed image ID: ${imageId}, Description: ${description}`);
      channel.ack(msg);
    }
  });
};

module.exports = { startConsumer };