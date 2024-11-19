const amqplib = require('amqplib');

let channel;

const connectRabbitMQ = async () => {
  try {
    const connection = await amqplib.connect(process.env.RABBITMQ_URI);
    channel = await connection.createChannel();
    await channel.assertQueue(process.env.QUEUE_NAME);
    console.log('Connected to RabbitMQ');
  } catch (error) {
    console.error('RabbitMQ connection error:', error);
  }
};

const getChannel = () => channel;

module.exports = { connectRabbitMQ, getChannel };