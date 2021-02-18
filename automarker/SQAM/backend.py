from flask import Flask, render_template, request, jsonify
from SQAM.task import Task
import SQAM.settings
from threading import Thread
import requests
app = Flask(__name__)

@app.route("/", methods=['GET'])
def root():
    return "Automarker is Running"

def sendResponse(tid, status):
    send_info = {"status": status}
    res = requests.patch("http://admin_api/api/tasks/status/{tid}".format(tid=tid), json=send_info)
    # TODO Log the response and possibly error handle a failed request

def runTaskThread(config_json):
    try:
        task = Task(config_json)
        task.run()
        print(f"Task with TID {config_json['tid']} ran successfully", flush=True)
        sendResponse(config_json['tid'], "Complete")
    except Exception as e:
        print(e)
        print(f"Task with TID {config_json['tid']} failed", flush=True)
        sendResponse(config_json["tid"], "Error")
        # TODO Log the Error

# Pass config arguments and start automarker
@app.route('/runJob', methods=['POST'])
def runJob():
    config_json = request.get_json()
    req_args = ["tid", "assignment_name", "solutions", "submissions", "submission_file_name",
                    "max_marks", "max_marks_per_question", "question_names", "db_type", "marking_type"]
    for arg in req_args:
        if arg not in config_json:
            return jsonify({'Status' : f"Missing Argument {arg}"}), 400
    
    task_thread = Thread(target=runTaskThread, args=(config_json,))
    task_thread.start()
    return jsonify({'Status' : "Success"})

if __name__ == '__main__':
    app.run(debug=True, port=SQAM.settings.PORT,host='0.0.0.0')



