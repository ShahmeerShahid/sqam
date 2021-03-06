# RabbitMQ Documentation

The purpose of this file is to document which queues are used in the system, and the required format of messages in each queue.

## Standards

- All queues are declared/asserted **only when consumed**
- All queues are declared/asserted **as durable**

## Queues

### `task_to_mark`
Purpose: Create and Run an Automarker Task

Expected message schema:


**Fields**:
Required:
  - "tid": Int
  - "assignment_name": String
  - "solutions": File Path
  - "submissions": File Path
  - "submission_file_name": String
  - "max_marks": Int
  - "max_marks_per_question": List of Ints
  - "question_names": List of Strings
  - "db_type": "mysql" or "postgresql"
  - "marking_type": "partial" or "binary"
  - "init": File Path

Optional:
  - "refresh_level": "per_assignment" or  "per_submission" or "per_query" (Default is "per_submission")

#### Message example:

```json
{
    "tid": 7,
    "assignment_name": "A2",
    "create_tables":"/automarker/SQAM/Demo/createTable.sql",
    "create_trigger":"/automarker/SQAM/Demo/createTrigger.sql",
    "create_function":"/automarker/SQAM/Demo/createFunction.sql",
    "init":"/automarker/SQAM/Demo/init.sql",
    "submissions": "/automarker/SQAM/Demo/",
    "submission_file_name": "queries.sql",
    "max_marks": 80,
    "max_marks_per_question": [3,4,3,3,4,4,2,2,4,5,3,4,4,4,3,5,6,7],
    "question_names": ["Q1","Q2","Q3.A","Q3.B","Q3.C","Q4.A","Q4.B","Q4.C","Q5.A","Q5.B","Q6.A", "Q6.B","Q6.C","Q7.A","Q7.B","Q8","Q9","Q10"],
    "db_type": "mysql",
    "marking_type": "partial",
    "refresh_level": "per_submission"
}
```

### `task_to_download_:connector_name`
Purpose: Trigger download of submissions for a specific task. The connector name is part of the queue name as to not make connectors conflict when consuming from the same queue. Connector name is as configured in `/admin_api/config.json`.

Expected message schema:


**Fields**:
Required:
  - "tid": Int
  - "download_directory": String
  - "extra_fields": JSON Object (see /admin_api/config.json)


#### Message example:

Queue: `task_to_download_Markus`

```json
{
    "tid": 7,
    "download_directory": "/var/downloads/7/",
    "extra_fields": {
      "markus_URL": "http://www.test-markus.com",
      "assignment_id": 1,
      "api_key": "hasf08etJSkf="
    }
}
```



### `grades` (Not currently implemented/used)

Purpose: Publish grades as a result of the automarker running

Expected message schema:
**Fields**
Required:
- "tid": Int

#### Message example:
```json
{
    "tid": 1
}
```

### `status`


Purpose: Update the status of a task

Expected message schema:
**Fields**
Required:
- "tid": Int
- "status": String, one of "Pending", "Downloading", "Downloaded", "Error", "Marking", "Complete"

#### Message example:
```json
{
    "tid": 1,
    "status": "Marking"
}
```


### `logs`

Purpose: Add to logs of a task

Expected message schema:
**Fields**
Required:
  - "tid": Int
  - "source": String, one of "frontend", "automarker", "connector", "api"
  - "message": String
  



#### Message example:

```json
{
    "tid": 1,
    "source": "automarker",
    "message": "Initiating marking for task 1"
}
```









