# docker-entrypoint-initdb.d

This folder contains all of the bash/javascript files run on startup to
populate the MongoDB. Unfortunately there is no way to retrieve environment
variables, so you will need to replace the ----------- with the password of the
api user in the .env
