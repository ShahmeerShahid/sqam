# Markus Connector

This service is used to download Markus submissions

## Environment Variables:

- BATCH_SIZE: Default 5

The number of Markus assignments to download and extract asynchronously. Reccommended to be no higher than 10, and only higher than 5 if running on SSD with good internet speeds.
- CONNECTOR_NAME: Default "Markus"
