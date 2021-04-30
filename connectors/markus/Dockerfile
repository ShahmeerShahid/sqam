FROM node:12
WORKDIR /app
COPY package*.json ./
COPY . .
RUN npm install
CMD [ "npm", "start" ]
