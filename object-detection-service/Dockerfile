# Use Node.js 20 as the base image
FROM node:20

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy package.json and package-lock.json (if available) to the working directory
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application code to the container
COPY . .

# Expose the port your app runs on (default is 3000; adjust if different)
EXPOSE 3000

# Define the command to run the application
CMD ["node", "src/app.js"]
