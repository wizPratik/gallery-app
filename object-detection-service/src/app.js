import dotenv from "dotenv";
import express from "express";
import connectDB from "./db/dbConnection.js";
import { connectRabbitMQ } from "./config/rabbitmq.js";
import { startConsumer } from "./services/messageProcessor.js";
import imageRoutes from "./routes/imageRoute.js";

const app = express();

const startApp = async () => {
	try {
		dotenv.config();
		await connectDB();
		await connectRabbitMQ();

		startConsumer();

		app.use("/images", imageRoutes);

		const port = process.env.PORT || 3000;
		app.listen(port, () => console.log(`Server running on port ${port}`));
	} catch (error) {
		console.error("App initialization error:", error);
		process.exit(1);
	}
};

startApp();
