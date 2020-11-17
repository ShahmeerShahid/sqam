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
    "submissions": [],
    "tid": 1,
    "name": "CSC343 Fall Test 1",
    "extra_fields": [
      {
        "markus_URL": "http://markus.com"
      }
    ]
  },
  {
    "status": "Error",
    "submissions": [
      {
        "status": "Pending",
        "_id": 0,
        "name": "servilla",
        "__v": 0
      }
    ],
    "tid": 2,
    "name": "CSC343 Fall A1",
    "extra_fields": [
      {
        "markus_URL": "http://markus.com",
        "assignment_id": 1
      }
    ]
  }
]
```

**Definition**
`POST /api/tasks`

**Parameters**

- name
- status (["Pending", "Error", "Marking", "Complete"])
- submissions (list of submission ids)
- extra_fields

**Response**

- `201 CREATED` on success
  The task created

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

**Definition**
`POST /api/tasks/status/:tid`

**Parameters**

- status (["Pending", "Error", "Marking", "Complete"])

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
-tid

**Response**

- `200 OK` on success
  List of all submissions

```json
[
  {
    "status": "Pending",
    "_id": 0,
    "name": "servilla",
    "__v": 0
  }
]
```

**Definition**
`POST /api/tasks`

**Parameters**

- name
- status (["Pending", "Error", "Marking", "Complete"])
- tid

  **Response**

- `201 CREATED` on success
  The submission created

```json
{
  "status": "Marking",
  "name": "dubchaeng",
  "tid": 0,
  "_id": 3,
  "__v": 0
}
```

**Definition**
`POST /api/submissions/status/:sid`

**Parameters**

- sid
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
