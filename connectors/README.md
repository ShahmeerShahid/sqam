# Connectors Specifications


All connectors MUST connect to RabbitMQ. The host, username, and passwords are provided through environement variables `RABBITMQ_HOST`, `RABBITMQ_USERNAME`, and `RABBITMQ_PASSWORD`, respectively.

The connector name must be configured through environment variable `CONNECTOR_NAME`. This name must match with the connector name configured in the Admin API configuration.

All connectors must be configured in the Admin API, read Admin API documentation for further info.

Read `rabbitmq/README.md` for information on which queues are available to consume from/publish to.

When a connector is finished downloading all files, it must publish a status of `Downloaded` (see RabbitMQ documentation for more info on message/queue specifics). Once this message is published, marking of the files will begin.


