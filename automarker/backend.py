import os
from flask import Flask, render_template, request, jsonify
from SQAM.SQAM.config_singleton import Config
import subprocess
import json
app = Flask(__name__)

config = Config.get_instance()

def createArgs(configDict):
    DELIMITER = chr(255)
    resultKey = ""
    resultValue = ""
    for key in configDict:
        if key == "max_marks_per_question":
            resultKey += str(key) +  DELIMITER 
            resultValue += ','.join(str(num) for num in configDict[key])  + DELIMITER

        elif key == "question_names":
            resultKey += str(key) +  DELIMITER
            resultValue +=  ','.join(configDict[key])  + DELIMITER
        else:
            resultKey +=  str(key) + DELIMITER 
            resultValue += str(configDict[key]) + DELIMITER

    return resultKey, resultValue

@app.route("/", methods=['GET'])
def root():
    # 👷‍♂️ resolver for "/" route
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
            purpose: 'update all variables in the config at the same time'
        },
    }
    
    """



@app.route('/config', methods=['POST'])
def ChangeAll():
    file_status = request.get_json()
    # h = json.dumps(file_status)  
    # Change the config.json 
    # config.load_config(h) 
    resultKey, resultValue = createArgs(file_status)
    # resultKey = createArgs(file_status)
    # print(resultKey)
    print("working ..")
    if len(file_status) == 24:
        os.system("docker exec -it sqam_mysql_1 bash -c 'mysql -uroot -psomewordpress wordpress < /var/lib/mysql-files/start.sql'")
        os.system("cd /Users/vaishvik/Desktop/sqam/automarker/SQAM/ && python3 SQAM_v3.py " + resultKey + " " + resultValue + "&")
        return jsonify({'Status' : "Success", "Results": file_status["submissions"]})
    else:
        return jsonify({'Status' : "Failure", "Message": "Invalid parameters"})

@app.route('/config/start', methods=['POST'])
def startAll():
    file_status = request.get_json()
    print(file_status)
    
    if file_status["name"] == "CSC343":
        return jsonify({'Status' : "Success"})
    else:
        return jsonify({'Status' : "Failure"})

if __name__ == '__main__':
  app.run(debug=True, port=5050)



