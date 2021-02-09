# SQAM

The SQL (DDL) Automarker (aka SQAM).

It is a framework for testing SQL assignments,
collecting the results, and exporting them for easy viewing using
templates.

We welcome collaboration and contributions!

## Requirements:

You need Docker 3.0 or above.

## Steps For Running With Docker

Database setup is not currently automatic, you must do the following
to start running the automarker.

1. make build
2. make setup app=sql

After the setup you just need to do make build, and you're good to go!

You must have the admin_api Running in Docker for tha automarker backend to work correctly.

#
## flask server endpoints
**Definition: route: /**
#### request: GET
#### purpose: 'Check if automarker is running'

**Response**

- `200 OK` on success
  -  Response "Automarker is Running"

# 
**Definition: route: /runJob/**

#### request: POST
#### purpose: Create and Run an Automarker Task
body_example:

```json
{
    "tid": 7,
    "assignment_name": "A2",
    "create_tables":"/automarker/SQAM/Demo/createTable.sql",
    "create_trigger":"/automarker/SQAM/Demo/createTrigger.sql",
    "create_function":"/automarker/SQAM/Demo/createFunction.sql",
    "load_data":"/automarker/SQAM/Demo/loadData.sql",
    "solutions":"/automarker/SQAM/Demo/solutions_winter_2020.sql",
    "submissions": "/automarker/SQAM/Demo/",
    "submission_file_name": "queries.sql",
    "max_marks": 80,
    "max_marks_per_question": [3,4,3,3,4,4,2,2,4,5,3,4,4,4,3,5,6,7],
    "question_names": ["Q1","Q2","Q3.A","Q3.B","Q3.C","Q4.A","Q4.B","Q4.C","Q5.A","Q5.B","Q6.A", "Q6.B","Q6.C","Q7.A","Q7.B","Q8","Q9","Q10"],
    "db_type": "mysql",
    "marking_type": "partial"
}
```

**Response**
- `200 OK` on success 
  - Response Body: {'Status' : "Success"}
  - Results files should show up in the submission folders
- `400 Bad Request` if missing information in body.
  - Response Body: {'Status' : "Missing Argument {Name of Missing Argument}"}


## Demo Post Request Body to test with Postgresql
```json
{
    "tid": 0,
    "assignment_name": "A2",
    "create_tables":"/automarker/SQAM/Demo_Postgres/createTables.sql",
    "create_trigger":"/automarker/SQAM/Demo_Postgres/createTrigger.sql",
    "create_function":"/automarker/SQAM/Demo_Postgres/createFunction.sql",
    "load_data":"/automarker/SQAM/Demo_Postgres/loadData.sql",
    "solutions":"/automarker/SQAM/Demo_Postgres/solutions.sql",
    "submissions": "/automarker/SQAM/Demo_Postgres/Submissions",
    "submission_file_name": "a2.sql",
    "max_marks": 50,
    "max_marks_per_question": [5,5,5,5,5,5,5,5,5,5],
    "question_names": ["Query 1","Query 2","Query 3","Query 4","Query 5","Query 6","Query 7","Query 8","Query 9","Query 10"],
    "db_type": "postgresql",
    "marking_type": "binary"
}
```