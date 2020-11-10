# docker-entrypoint-initdb.d

This folder contains all of the bash/javascript files run on startup to
populate the MongoDB.

It currently creates the sqamadmin database, users & tasks collections, a user
for api purposes, and inserts some mock tasks into the tasks collection.
