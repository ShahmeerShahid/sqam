# SQAM

The SQL (DDL) Automarker (aka SQAM). 

It is a framework for testing SQL assignments,
collecting the results, and exporting them for easy viewing using
templates.

We welcome collaboration and contributions!

## Usage

### AutoMarker
Change sqamv3_path in /sqam/automarker/SQAM/UAM/utils/config.py and /sqam/automarker/SQAM/SQAM/config.py
Start the docker image so you can use the SQL server inside the image
>> make run
Open a new terminal
Setup the Database inside the docker image and installs all the 
>> make test_automarker

### Admin
