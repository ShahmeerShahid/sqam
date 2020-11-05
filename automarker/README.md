# SQAM

The SQL (DDL) Automarker (aka SQAM). 

It is a framework for testing SQL assignments,
collecting the results, and exporting them for easy viewing using
templates.

We welcome collaboration and contributions!

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

GRANT ALL PRIVILEGES ON *.* TO 'username'@'localhost';

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

__Note:__ SQAM assumes that the submission belongs to a group, not a student.


# Run SQAM automarker (NEW)
### Run automarker with config.json without backend flask server 
> cd ./sqam
start the sql server inside docker
> make run 
run the automarker to mark assignment
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
{
route: /config/,
request: POST,
example:
{
"ASSIGNMENT_NAME": 'A2',
"USING_WINDOWS_SYSTEM": False,
"SUBMISSION_FILE_NAME": 'a2.sql',
"JSON_OUTPUT_FILENAME": 'result.json',
"LECTURE_SECTION": 'LEC101',
"STUDENTS_CSV_FILE": 'SQAM/Student_Information_and_Submissions/students.csv',
"STUDENT_GROUPS_FILE": 'SQAM/Student_Information_and_Submissions/groups.txt',
"DIR_AND_NAME_FILE" : 'SQAM/Student_Information_and_Submissions/dirs_and_names.txt',
"TIMEOUT": 100,
"MAX_MARKS": 70,
"maxMarksPerQuestion": [3,4,3,3,4,4,2,2,4,5,3,4,4,4,3,5,6,7],
"questionNames": ['Q1','Q2','Q3.A','Q3.B','Q3.C','Q4.A','Q4.B','Q4.C','Q5.A','Q5.B','Q6.A',
'Q6.B','Q6.C','Q7.A','Q7.B','Q8','Q9','Q10']
}
purpose: 'update all variables in the config at the same time it runs the program'
},
}

**Definition**
route: /config/,
request: POST,
purpose: 'update all variables in the config at the same time it runs the program'
body_example: 
{
    "ASSIGNMENT_NAME": 'A2',
    "USING_WINDOWS_SYSTEM": False,
    "SUBMISSION_FILE_NAME": 'a2.sql',
    "JSON_OUTPUT_FILENAME": 'result.json',
    "LECTURE_SECTION": 'LEC101',
    "STUDENTS_CSV_FILE": 'SQAM/Student_Information_and_Submissions/students.csv',
    "STUDENT_GROUPS_FILE": 'SQAM/Student_Information_and_Submissions/groups.txt',
    "DIR_AND_NAME_FILE" : 'SQAM/Student_Information_and_Submissions/dirs_and_names.txt',
    "TIMEOUT": 100,
    "MAX_MARKS": 70,
    "maxMarksPerQuestion": [3,4,3,3,4,4,2,2,4,5,3,4,4,4,3,5,6,7],
    "questionNames": ['Q1','Q2','Q3.A','Q3.B','Q3.C','Q4.A','Q4.B','Q4.C','Q5.A','Q5.B','Q6.A',
        'Q6.B','Q6.C','Q7.A','Q7.B','Q8','Q9','Q10']
}

**Response**
- `200 OK` on success: 
- Response Body: 
{'Status' : "Success", "Results": file_status["submissions"]}

**Response**
- `200 OK` on success. To be returned once the connector is sure it is able to start downloading submissions
- `400 Bad Request` if missing information in body.
- `401 Unauthorized` if unable to authorize with given information
- `404 Not Found` if unable to download specified assignment submissions
- `500 Internal Server Error` on other error