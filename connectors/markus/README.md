# Markus Connector

This service is used to download Markus submissions

## Environment Variables:

- BATCH_SIZE: Default 5

The number of Markus assignments to download and extract asynchronously. Reccommended to be no higher than 10, and only higher than 5 if running on SSD with good internet speeds.

## Resources:

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
***

**Definition**
`POST /tasks`
```json
{
    "tid": 4,
    "download_directory": "/downloads/4/submissions/",
    "markus_URL": "www.example.com",
    "assignment_id": 4,
    "api_key": "cxNzdhMDIzNWJiMTQ3OGE"
}
```

**Response**
- `200 OK` on success
- `400 Bad Request` if `tid`, `download_directory`, `markus_URL`, `assignment_id` and/or `api_key` not provided
- `401 Unauthorized` if unable to authorize with given information
- `404 Not Found` if no assignment with `assignment_id` exists.
- `500 Internal Server Error` on internal error
