# Connectors Specifications

All connectors MUST connect to RabbitMQ. The host, username, and passwords are provided through environement variables `RABBITMQ_HOST`, `RABBITMQ_USERNAME`, and `RABBITMQ_PASSWORD`, respectively.

The connector name must be configured through environment variable `CONNECTOR_NAME`. This name must match with the connector name configured in the Admin API configuration.

All connectors must be configured in the Admin API, read Admin API documentation for further info.

Read RabbitMQ documentation for information on which queues are available to consume from/publish to.

When a connector is finished downloading all files, it must publish a status of `Downloaded` (see RabbitMQ documentation for more info on message/queue specifics). Once this message is published, marking of the files will begin.


### Required Fields
**Definition**
`GET /extra_fields`

**Response**
- `200 OK` on success
  
Fields APART FROM `tid` and `download_directory` that this connector requires when `POST`ing download tasks.
```json
{
    "info": "Any extra general information to be displayed on the submission page/form e.g. group names must not have whitespace characters. Field specific should be provided as shown below.",
    "extra_fields": {
        "markus_URL": {
            "type": "string",
            "required": true,
            "info": "Information specific to this field e.g. Example: http://www.test-markus.com, NOT www.test-markus.com or http://www.test-markus.com/en/main",
            "placeholder": "http://www.test-markus.com"
        },
        "assignment_id": {
            "type": "number",
            "required": true,
            "info": "Found in the URL when editing the assignment. E.g. http://www.test-markus.com/en/assignments/1/edit would have ID 1.",
            "placeholder": "1"

        },
        "api_key": {
            "type": "string",
            "required": true,
            "info": "Found on the homepage of your Markus instance.",
            "placeholder": "hasf08etJSkf="
        }
    }
}
```
For each field, `type`, `required` and `placeholder` MUST be provided. `info` is optional. JSON types can be found at https://json-schema.org/understanding-json-schema/reference/type.html

***

Furthermore, each connector MUST expose the following HTTP request methods for the `/tasks` endpoint:

### Tasks
**Definition**
`POST /tasks`

**Pre-condition:** The supplied download directory **must** exist.

Body:
```json
{
    "tid": 1,
    "download_directory": "/var/downloads/1/",
    extra_fields
}
```
**Response**
- `200 OK` on success. To be returned once the connector is sure it is able to start downloading submissions
- `400 Bad Request` if missing information in body.
- `401 Unauthorized` if unable to authorize with given information
- `404 Not Found` if unable to download specified assignment submissions
- `500 Internal Server Error` on other error

***
***

## AFTER FILES ARE DOWNLOADED

After the files are downloaded OR an error has occurred, the following `PATCH` request **must** be sent to `http://admin_api/api/task/status/{tid}`:

In case of error, the request body will be:
```json
{
    "status": "Error"
}
```
TODO, LOGGING:   "error_message": "Custom, descriptive error message that will be viewed by users"


In case of success, the request body will be:
```json
{
    "status": "Downloaded",
    "num_submissions": 5,
}
```
