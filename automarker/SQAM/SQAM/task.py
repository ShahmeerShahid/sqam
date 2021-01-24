from SQAM.assignment import Assignment
from SQAM.queriers.MySQL import MySQLQuerier
# from SQAM.queriers.PostGreSQL import PostGreSQLQuerier
from SQAM.graders.partial_marking_grader import Partial_Marking_Grader
from SQAM.graders.binary_grader import Binary_Marking_Grader
import SQAM.settings
import requests

class Task:
    """ 
    A Class used to represent an Automarking Job
    
    Attributes:
    assignment : Representation of Assignment that will be marked
    query_language: SQL Language the Assignment uses
    grader: Either Partial or Binary. Used to convert query results into grades
    """
    def __init__(self, config):
        """
        Creates all required attributes based off the provided 
        configuration information. 
        
        Precondition: Provided configuration information contains all required keys
        """
        self.tid = config["tid"]

        db_name = "t"+str(self.tid)
        if(config["db_type"] == "mysql"):
            login_details = SQAM.settings.MYSQL_LOGIN_DETAILS
            query_language = MySQLQuerier(*login_details,
                                            db_name,
                                            config["create_tables"], 
                                            config["load_data"],
                                            config["create_function"],
                                            config["create_trigger"])
            self.log("Setup MYSQL Database")
        elif(config["db_type"] == "postgresql"):
            # Need to add postgres requirements to requirements.txt before uncommenting
            pass
        else:
            exit(1)

        self.assignment = Assignment(config["assignment_name"],
                                     config["submissions"],
                                     config["solutions"],
                                     config["submission_file_name"],
                                    {q_num:max_grade for q_num,max_grade in zip(config["question_names"],config["max_marks_per_question"])},
                                     SQAM.settings.QUERY_EXTRACTOR_RE,
                                     config["max_marks"],
                                    query_language)
        self.log("Created the Assignment")

        if config["marking_type"] == "partial":
            self.grader = Partial_Marking_Grader(self.assignment, query_language)
        elif config["marking_type"] == "binary":
            self.grader = Binary_Marking_Grader(self.assignment, query_language)
        else:
            exit(1)
        self.log("Created the Grader")
        
    def run(self):
        """
        Runs an automarking job on an entire assignment
        Creates reports in each submission repo as well as aggregates results into
        one json file. 
        """
        self.log("Beginning Assignment Marking")
        self.assignment.mark_submissions(self.grader)
        self.log("Graded all Submissions")
        self.assignment.run_aggregator()
        self.assignment.run_templator()
        self.log("Generated all Result Files")
        self.log("Assignment Marking Complete")
        self.assignment.querier.remove_database()

        print(f'Done Grading Submissions. Class Average: {self.assignment.get_average()*100:.2f}%')
    
    def log(self, message): # TODO Make a more robust Logging System
        print(message, flush=True)
        send_info = {"source": "automarker", "logs": [message]}
        try:
            res = requests.put(f"http://admin_api/api/tasks/{self.tid}/logs", json=send_info)
        except Exception as e:
            pass
