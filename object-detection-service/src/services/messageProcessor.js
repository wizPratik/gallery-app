import { getChannel } from "../config/rabbitmq.js";
import { processImage } from "./imageProcessor.js";
import Image from "../models/imageModel.js";

const startConsumer = async () => {
	const channel = await getChannel();

	await channel.consume(process.env.INBOUND_QUEUE, async (msg) => {
		if (msg !== null) {
			const { image_id, location } = JSON.parse(msg.content.toString());

			console.log(`Processing image ID: ${image_id}, URL: ${location}`);

			const description = await processImage(location);

			// Save to database
			const newImage = new Image({ imageId: image_id, description });
			await newImage.save();

			console.log(
				`Processed Image ID: ${image_id}, Description: ${description}`
			);
			channel.ack(msg);

			const newMessage = {
				image_id: image_id,
				status: true,
			};

			channel.sendToQueue(
				process.env.OUTBOUND_QUEUE,
				Buffer.from(JSON.stringify(newMessage)),
				{
					persistent: true, // Make the message persistent
				}
			);
			console.log(
				`Published Message for Image ID: ${image_id}, Message: ${JSON.stringify(
					newMessage
				)}`
			);
		}
	});
};

export { startConsumer };
