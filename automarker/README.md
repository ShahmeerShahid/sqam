# SQAM

The SQL (DDL) Automarker (aka SQAM).

It is a framework for testing SQL assignments,
collecting the results, and exporting them for easy viewing using
templates.

We welcome collaboration and contributions!

## Steps For Running With Docker

Database setup is not currently automatic, you must do the following
to start running the automarker.

1. make build
2. make setup app=sql

After the setup you just need to do make build, and you're good to go!

You must have the admin_api Running in Docker for tha automarker backend to work correctly.

### How to Test if its working?

Put the following in the body of a POST request to /config:

```json
{
  "tid": 2,
  "assignment_name": "A2",
  "create_tables": "/var/downloads/Demo/Winter_2020/createTable.sql",
  "create_trigger": "/var/downloads/Demo/Winter_2020/createTrigger.sql",
  "create_function": "/var/downloads/Demo/Winter_2020/createFunction.sql",
  "load_data": "/var/downloads/Demo/Winter_2020/loadData.sql",
  "solutions": "/var/downloads/Demo/Winter_2020/solutions_winter_2020.sql",
  "submissions": "/var/downloads/Demo/Submissions/",
  "submission_file_name": "queries.sql",
  "timeout": 100,
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
  "db_type": "mysql",
  "marking_type": "partial"
}
```

The POST Request should return a status of "Success"
The job running thread will create an am aggregated.json and report.txt inside the SQAM folder
There should also be a result.json and report.txt inside each submission folder.
Ex SQAM/SQAM/Demo/Submissions/group_0000

## Requirements:

You need Python >= 3.3 installed on your system. Then, install the
requirements listed in the file requirements.txt. The easiest way to
do this is to use pip (pip for Python 3):

pip install -r requirements.txt

You can use virtualenv to avoid installing the packages system-wide
(https://virtualenv.pypa.io).

## Usage

### Create database and user if not using docker

Before running the autograder, we need to create a SQL database and user.

1. Log in to MySQL as the root user.

mysql -u username -p

2. Create a user and grant permissions.

GRANT ALL PRIVILEGES ON _._ TO 'username'@'localhost';

3. Create a database (read-only in this example).

CREATE DATABASE a1_db;

### Create drop_all_tables procedure (TEMPORARY) if not using docker

### Adjust config.py

The list below is the set of required parameters that
can be specified in config.py.

1. SUBMISSIONS: path to submission directory (required)
   SQAM assumes that the submission directory organized in the following way

```
submission_dir
│
└───group_0001
│   │   f1.sql
│   │   f2.sql
...
│
└───group_0002
│   f1.sql
│   f2.sql
...
```

2. ...

**Note:** SQAM assumes that the submission belongs to a group, not a student.

# Run SQAM automarker (NEW)

### Run automarker with config.json without backend flask server

> cd ./sqam
> start the sql server inside docker
> make run
> run the automarker to mark assignment
> test_automarker

### running the Flask Server and sql server from docker to run automarker from POST request

> cd ./sqam
> make run
> cd ./sqam/automarker
> python3 backend.py

### flask server endpoints

**Definition**
route: /,
request: GET,
purpose: 'get message about instructions about enpoints'

**Response**

- `200 OK` on success
- Response Body:
  {
  {
  route: /,
  request: GET,
  purpose: 'To get this message'
  },

**Definition**
route: /runJob/,
request: POST,
purpose: 'update all variables in the config at the same time it runs the program'
body_example:

```json
{
  "tid": 2,
  "assignment_name": "A2",
  "create_tables": "/var/downloads/Demo/Winter_2020/createTable.sql",
  "create_trigger": "/var/downloads/Demo/Winter_2020/createTrigger.sql",
  "create_function": "/var/downloads/Demo/Winter_2020/createFunction.sql",
  "load_data": "/var/downloads/Demo/Winter_2020/loadData.sql",
  "solutions": "/var/downloads/Demo/Winter_2020/solutions_winter_2020.sql",
  "submissions": "/var/downloads/Demo/Submissions/",
  "submission_file_name": "queries.sql",
  "timeout": 100,
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
  "db_type": "mysql",
  "marking_type": "partial"
}
```

**Response**

- `200 OK` on success:
- Response Body:
  {'Status' : "Success"}

**Response**

- `200 OK` on success. To be returned once the connector is sure it is able to start downloading submissions
- `400 Bad Request` if missing information in body.
- `401 Unauthorized` if unable to authorize with given information
- `404 Not Found` if unable to download specified assignment submissions
- `500 Internal Server Error` on other error
