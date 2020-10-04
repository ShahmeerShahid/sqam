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

### Create database and user
Before running the autograder, we need to create a SQL database and user.

1. Log in to MySQL as the root user.

mysql -u username -p

2. Create a user and grant permissions.

GRANT ALL PRIVILEGES ON *.* TO 'username'@'localhost';

3. Create a database (read-only in this example).

CREATE DATABASE a1_db;

### Create drop_all_tables procedure (TEMPORARY)


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


### Run SQAM

run SQAM_v3

TODO
