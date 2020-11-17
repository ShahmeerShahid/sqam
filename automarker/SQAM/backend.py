import os
from flask import Flask, render_template, request, jsonify
from SQAM.job import Job
import json
from threading import Thread
app = Flask(__name__)

@app.route("/", methods=['GET'])
def root():
    return """
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
    
    """

def run_job(config_json):
    job = Job(config_json)
    job.run()
    # TODO Use Config Info to Send Back Response that Job is Done Marking

# Pass config arguments and start automarker
@app.route('/config', methods=['POST'])
def ChangeAll():
    config_json = request.get_json()
    # We Should Add Validation that All Required Feilds are correct and have Correct Values
    if len(config_json) == 24:
        job_thread = Thread(target=run_job, args=(config_json,))
        job_thread.start()
        return jsonify({'Status' : "Success", "Results": config_json["submissions"]})
    else:
        return jsonify({'Status' : "Failure", "Message": "Invalid parameters"})


if __name__ == '__main__':
    #Setting ports by reading the environment variable that set by docker compose file.
  listen = os.getenv('PORT', '9005')
  app.run(debug=True, port=listen,host='0.0.0.0')


