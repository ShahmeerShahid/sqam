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
      },
      {
        "status": "Pending",
        "_id": "5fb4713645f940001851ea58",
        "name": "vaishvik"
      },
      {
        "status": "Pending",
        "_id": "5fb4713645f940001851ea59",
        "name": "sandy"
      }
    ],
    "updatedAt": "2020-11-18T00:56:22.257Z"
  },
  {
    "status": "Error",
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
- status (["Pending", "Error", "Marking", "Complete"])
  optional:
- extra_fields
- submissions (list of submission objects)

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
  "__v": 0
}
```

**Definition**
`PATCH /api/tasks/status/:tid`

**Parameters**

- tid
- status ([
  "Pending",
  "Downloading",
  "Downloaded",
  "Error",
  "Marking",
  "Complete",
  ])
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
`POST /api/logs/`

**Parameters**

- app (["automarker", "connectors", "admin", "admin_api"])
- filename (must end with .txt)
- text

**Response**

- `200 OK` on success

```json
{
  "message": "Log for app automarker with text Submission 5 marked written to servilla.txt"
}
```

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
