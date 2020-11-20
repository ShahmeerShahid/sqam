import os
from flask import Flask, render_template, request, jsonify
from SQAM.job import Job
from threading import Thread
import requests
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

def sendResponse(tid, status):
    send_info = {"status": status}
    res = requests.patch("http://admin_api/api/tasks/status/{tid}".format(tid=tid), json=send_info)
    # TODO Log the response and possibly error handle a failed request

def runJobThread(config_json):
    job = Job(config_json)
    job.run()
    # try:
    #     job = Job(config_json)
    #     job.run()
    #     sendResponse(config_json['tid'], "Complete")
    # except Exception as e:
    #     print('errors!@!!223#@$#%#')
    #     sendResponse(config_json["tid"], "Error")
    #     # TODO Log the Error

# Pass config arguments and start automarker
@app.route('/runJob', methods=['POST'])
def runJob():
    config_json = request.get_json()
    # We Should Add Validation that All Required Feilds are correct and have Correct Values
    job_thread = Thread(target=runJobThread, args=(config_json,))
    job_thread.start()
    return jsonify({'Status' : "Success"})

if __name__ == '__main__':
    #Setting ports by reading the environment variable that set by docker compose file.
  listen = os.getenv('PORT', '9005')
  app.run(debug=True, port=listen,host='0.0.0.0')



