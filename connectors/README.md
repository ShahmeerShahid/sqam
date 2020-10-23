# Connectors Specifications

All connectors MUST provide the endpoints `/extra_fields` and `/tasks`.

When `POST`ing to a `/tasks`, `task_id` and `download_directory` will always be sent. However, any other fields (required or optional) MUST be documented using `/extra_fields` as shown below:

### Required Fields
**Definition**
`GET /extra_fields`

**Response**
- `200 OK` on success
  
Fields APART FROM `task_id` and `download_directory` that this connector requires when `POST`ing download tasks.
```json
{
    "info": "Any extra general information to be displayed on the submission page/form e.g. group names must not have whitespace characters. Field specific should be provided as shown below.",
    "extra_fields": {
        "markus_URL": {
            "type": "string",
            "required": true,
            "info": "Information specific to this field e.g. Example: http://www.test-markus.com, NOT www.test-markus.com or http://www.test-markus.com/en/main"
        },
        "assignment_id": {
            "type": "number",
            "required": true,
            "info": "Found in the URL when editing the assignment. E.g. http://www.test-markus.com/en/assignments/1/edit would have ID 1."
        },
        "api_key": {
            "type": "string",
            "required": true,
            "info": "Found on the homepage of your Markus instance."
        }
    }
}
```
For each field, `type` and `required` MUST be provided. `info` is optional. JSON types can be found at https://json-schema.org/understanding-json-schema/reference/type.html

***

Furthermore, each connector MUST expose the following HTTP request methods for the `/tasks` endpoint:

### Tasks
**Definition**
`POST /tasks`

**Pre-condition:** The supplied download directory **must** exist.

Body:
```json
{
    "task_id": 1,
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




