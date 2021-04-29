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
    "status": "Downloading",
    "connector": "markus-connector",
    "num_submissions": 0,
    "lecture_section": "LEC102",
    "max_marks": 80,
    "max_marks_per_question": [
      3,
      4,
      3,
      3,
      4,
      4,
      2,
      2,
      4,
      5,
      3,
      4,
      4,
      4,
      3,
      5,
      6,
      7
    ],
    "marking_type": "partial",
    "question_names": [
      "Q1",
      "Q2",
      "Q3.A",
      "Q3.B",
      "Q3.C",
      "Q4.A",
      "Q4.B",
      "Q4.C",
      "Q5.A",
      "Q5.B",
      "Q6.A",
      "Q6.B",
      "Q6.C",
      "Q7.A",
      "Q7.B",
      "Q8",
      "Q9",
      "Q10"
    ],
    "submission_file_name": "queries.sql",
    "create_tables": "./Demo/Winter_2020/createTable.sql",
    "create_trigger": "./Demo/Winter_2020/createTrigger.sql",
    "create_function": "./Demo/Winter_2020/createFunction.sql",
    "load_data": "./Demo/Winter_2020/loadData.sql",
    "solutions": "./Demo/Winter_2020/solutions_winter_2020.sql",
    "submissions_path": "./Demo/Submissions/",
    "timeout": 100,
    "db_type": "mysql",
    "_id": "5fb741d5ba9512001295d833",
    "name": "new task",
    "extra_fields": {
      "markus_URL": "http://www.test-markus.com",
      "assignment_id": 1,
      "api_key": "dfgAHFDFUSF="
    },
    "submissions": [],
    "createdAt": "2020-11-20T04:11:01.148Z",
    "updatedAt": "2020-11-20T04:11:01.191Z",
    "tid": 0,
    "__v": 0,
    "logs": [
      {
        "text": "line 1",
        "source": "connector",
        "timestamp": "2021-20-1"
      }
    ]
  }
]
```

**Definition**
`GET /api/tasks/:tid`

**Response**

- `200 OK` on success
  A task object

```json
{
  "status": "Downloading",
  "connector": "markus-connector",
  "num_submissions": 0,
  "lecture_section": "LEC102",
  "max_marks": 80,
  "max_marks_per_question": [
    3,
    4,
    3,
    3,
    4,
    4,
    2,
    2,
    4,
    5,
    3,
    4,
    4,
    4,
    3,
    5,
    6,
    7
  ],
  "marking_type": "partial",
  "question_names": [
    "Q1",
    "Q2",
    "Q3.A",
    "Q3.B",
    "Q3.C",
    "Q4.A",
    "Q4.B",
    "Q4.C",
    "Q5.A",
    "Q5.B",
    "Q6.A",
    "Q6.B",
    "Q6.C",
    "Q7.A",
    "Q7.B",
    "Q8",
    "Q9",
    "Q10"
  ],
  "submission_file_name": "queries.sql",
  "create_tables": "./Demo/Winter_2020/createTable.sql",
  "create_trigger": "./Demo/Winter_2020/createTrigger.sql",
  "create_function": "./Demo/Winter_2020/createFunction.sql",
  "load_data": "./Demo/Winter_2020/loadData.sql",
  "solutions": "./Demo/Winter_2020/solutions_winter_2020.sql",
  "submissions_path": "./Demo/Submissions/",
  "timeout": 100,
  "db_type": "mysql",
  "_id": "5fb741d5ba9512001295d833",
  "name": "new task",
  "extra_fields": {
    "markus_URL": "http://www.test-markus.com",
    "assignment_id": 1,
    "api_key": "dfgAHFDFUSF="
  },
  "submissions": [],
  "createdAt": "2020-11-20T04:11:01.148Z",
  "updatedAt": "2020-11-20T04:11:01.191Z",
  "tid": 0,
  "__v": 0
}
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
  "logs": ["first line", "second line", "third line", "fourth line"],
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


# Legacy Endpoints

```js

/*	PATCH /tasks/:tid
    @params: status, name, submissions, num_submissions, extra_fields
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/

router
  .route("/:tid")
  .patch(
    [
      param("tid").isInt({ min: 0 }),
      body("status").optional().isIn(constants.statuses),
      body("name").optional().notEmpty(),
      body("submissions").optional().isArray({ min: 0 }),
      body("num_submissions").optional().isInt({ min: 0 }),
      body("extra_fields").optional().notEmpty(),
    ],
    (req: Request, res: Response) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const tid = req.params.tid;
      let update = {};
      const restrictedKeys = ["tid", "logs", "connector", "_id"];

      Object.keys(TaskSchema.obj).forEach((key) => {
        if (restrictedKeys.includes(key)) return;
        //TODO: handle the case where they pass in logs!
        else if (key === "submissions" && req.body[key]) {
          const submissions = req.body.submissions.map((name) => {
            return {
              name: name,
            };
          });
          update.submissions = submissions;
        } else if (req.body[key]) update[key] = req.body[key];
      });

      Task.findOneAndUpdate({ tid: tid }, update, function (err, doc) {
        if (doc === null) {
          return res.sendStatus(404);
        } else if (err) {
          return res.sendStatus(500);
        } else {
          res.status(200).json({
            message: `Task ${tid} successfully updated`,
          });
        }
      });
    }
  );

/*	PUT /tasks/:tid/logs
    @params: logs
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/
router
  .route("/:tid/logs")
  .put(
    [
      param("tid").isInt({ min: 0 }),
      body("logs").isArray(),
      body("source").optional().isIn(constants.logSources),
    ],
    (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const tid = req.params.tid;
      const logsToAppend = [];
      for (line in req.body.logs) {
        logsToAppend.push(
          new Log({
            timestamp: new Date(),
            text: req.body.logs[line],
            source: req.body.source,
          })
        );
      }

      Task.findOneAndUpdate(
        { tid: tid },
        { $push: { logs: { $each: logsToAppend } } },
        function (err, doc) {
          if (doc === null) {
            res.sendStatus(404);
          } else if (err) {
            console.log(err);
            res.sendStatus(500);
          } else {
            res.status(200).json({
              message: `Task ${tid} logs successfully updated`,
            });
          }
        }
      );
    }
  );
```