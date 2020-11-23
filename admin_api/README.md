Node.js API utilizing Mongoose to interface with the Mongo database.

## Models

Defined schemas for User/Tasks

## Resources

<!-- Endpoints for Users/Tasks -->

### Tasks

**Definition**
`GET /api/tasks`

**Response**

- `200 OK` on success
  List of all tasks

```json
[
  {
    "status": "Pending",
    "num_submissions": 0,
    "extra_fields": [
      {
        "markus_URL": "http://markus.com"
      }
    ],
    "_id": "5fb47041ad3adeea41e2e105",
    "connector": "markus-connector",
    "tid": 1,
    "name": "CSC343 Fall Test 1",
    "submissions": [
      {
        "status": "Pending",
        "_id": "5fb4713645f940001851ea56",
        "name": "servilla"
      },
      {
        "status": "Pending",
        "_id": "5fb4713645f940001851ea57",
        "name": "shahmeer"
      }
    ],
    "updatedAt": "2020-11-18T00:56:22.257Z",
    "logs": [
      {
        "text": "line 1",
        "source": "connector",
        "timestamp": "2021-20-1"
      }
    ]
  },
  {
    "status": "Error",
    "connector": "markus-connector",
    "num_submissions": 0,
    "extra_fields": [
      {
        "markus_URL": "http://markus.com",
        "assignment_id": 1
      }
    ],
    "_id": "5fb47041ad3adeea41e2e106",
    "tid": 2,
    "name": "CSC343 Fall A1",
    "submissions": []
  }
]
```

**Definition**
`POST /api/tasks`

**Parameters**

- name
- connector (["markus-connector"])
  optional:
- status (["Pending", "Downloading", "Downloaded", "Error", "Marking", "Complete"])
- extra_fields
- submissions (list of submission objects)

CANNOT PASS IN: tid, \_id

**Response**

- `201 CREATED` on success
  The task created

```json
{
  "status": "Marking",
  "num_submissions": 0,
  "extra_fields": [
    {
      "markus_URL": "http://www.test-markus.com",
      "assignment_id": 1,
      "api_key": "dfgAHFDFUSF="
    }
  ],
  "connector": "markus-connector",
  "_id": "5fb4722245f940001851ea5e",
  "name": "CSC343 Final Exam",
  "submissions": [
    {
      "status": "Pending",
      "_id": "5fb4722245f940001851ea5f",
      "name": "servilla"
    }
  ],
  "createdAt": "2020-11-18T01:00:18.177Z",
  "updatedAt": "2020-11-18T01:00:18.177Z",
  "tid": 0,
  "__v": ,
  "logs": []
}
```

**Definition**
`PATCH /api/tasks/status/:tid`

**Parameters**

- tid
  optional:
- name
- status (["Pending", "Downloading", "Downloaded", "Error",
  "Marking", "Complete"])
- submissions
- num_submissions
- extra_fields

CANNOT PASS IN: tid, logs, connector, \_id

**Response**

- `200 OK` on success

```json
{
  "message": "Task 1 successfully updated"
}
```

**Definition**
`PATCH /api/tasks/status/:tid`

This endpoint will be removed in the future!

**Parameters**

- tid
- status (["Pending", "Downloading", "Downloaded", "Error",
  "Marking", "Complete"])
  optional:
- num_submissions

**Response**

- `200 OK` on success

```json
{
  "message": "Task 1 successfully updated to status Marking"
}
```

### Submissions

Submissions is subject to change while we shift over
the Automarker to use the concept of submissions
so keep that in mind!

**Definition**
`GET /submissions/:tid`
A list of submissions associated with task tid

**Parameters**

- tid

**Response**

- `200 OK` on success
  List of all submissions

```json
[
  {
    "status": "Pending",
    "_id": "5fb4713645f940001851ea56",
    "name": "servilla"
  },
  {
    "status": "Pending",
    "_id": "5fb4713645f940001851ea57",
    "name": "shahmeer"
  }
]
```

**Definition**
`POST /api/submissions/:tid`

**Parameters**

- tid
- names (list of submission names)

  **Response**

- `201 CREATED` on success

```json
{
  "message": "Submission(s) successfully added to task 1"
}
```

**Definition**
`PATCH /api/submissions/status/:sid`

**Parameters**

- sid (Mongo generated ObjectId)
- status (["Pending", "Error", "Marking", "Complete"])

**Response**

- `200 OK` on success

```json
{
  "message": "Submission 1 successfully updated to status Marking"
}
```

### Logs

**Definition**
`PUT /api/tasks/:tid/logs`

Note: List of supplied logs will be appended to existing list of logs for given task.

**Parameters**

- logs: a list of strings
- source: String, must be one of "frontend", "automarker", "connector", or "api"

**Example Request Body**

```json
{
    "logs": [
        "first line",
        "second line",
        "third line",
        "fourth line"
    ],
    "source": "automarker"
}
```

**Response**

- `200 OK` on success

```json
{
  "message": "Task :tid logs successfully updated"
}
```
- `404 Not found` if no such task with given tid

### Connectors

**Definition**
`GET /api/connectors`

**Response**

- `200 OK` on success
  List of connectors (name and url) that can be used to download submissions

```json
[
  {
    "name": "Markus",
    "url": "http://markus-connector",
    "port": 8001
  },
  {
    "name": "Example",
    "url": "http://example",
    "port": 3000
  }
]
```

---

## `config.json`

`config.json` can be used to configure the system settings.

### Connectors

To add/remove connectors, specify the name and url of the connector in `config.json`.
