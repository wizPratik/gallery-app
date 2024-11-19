require('dotenv').config();
const express = require('express');
const connectDB = require('./db/dbConnection');
const { connectRabbitMQ } = require('./config/rabbitmq');
const { startConsumer } = require('./services/messageConsumer');
const imageRoutes = require('./routes/imageRoutes');

const app = express();

const startApp = async () => {
  try {
    await connectDB();
    await connectRabbitMQ();

    startConsumer();

    app.use('/images', imageRoutes);

    const port = process.env.PORT || 3000;
    app.listen(port, () => console.log(`Server running on port ${port}`));
  } catch (error) {
    console.error('App initialization error:', error);
    process.exit(1);
  }
};

startApp();