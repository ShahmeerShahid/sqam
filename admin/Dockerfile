FROM node:13.12.0-alpine
WORKDIR /admin
# install app dependencies
COPY ./package.json ./
# install node modules
RUN npm install --silent
RUN npm install react-scripts@3.4.0 -g --silent
# add app
COPY . ./
# start app
CMD ["npm", "start"]