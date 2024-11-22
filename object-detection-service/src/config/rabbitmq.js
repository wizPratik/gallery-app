import amqplib from "amqplib";

let channel;

export const connectRabbitMQ = async () => {
	try {
		const connection = await amqplib.connect(process.env.RABBITMQ_URI);
		channel = await connection.createChannel();
		await channel.assertQueue(process.env.INBOUND_QUEUE);
		console.log("Connected to RabbitMQ");
	} catch (error) {
		console.error("RabbitMQ connection error:", error);
	}
};

export const getChannel = () => channel;
