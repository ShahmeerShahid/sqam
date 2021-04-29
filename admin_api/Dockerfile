# Use a lighter version of Node as a parent image
FROM node:14-alpine
# Set the working directory to /api
WORKDIR /admin_api
# copy package.json into the container at /api
COPY package*.json ./
# install dependencies
RUN npm install
# Copy the current directory contents into the container at /api
COPY . .
# Run the app when the container launches
CMD ["npm", "run", "dev"]