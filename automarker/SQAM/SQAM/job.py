from SQAM.config_singleton import Config
from SQAM.assignment.assignment import Assignment
from SQAM.query_languages.MySQL import MySQLQuerier
# from SQAM.query_languages.PostGreSQL import PostGreSQLQuerier
from SQAM.query_languages.query_runner import QueryRunner
from SQAM.autograder.partial_marking_grader import Partial_Marking_Grader
from SQAM.autograder.binary_grader import Binary_Marking_Grader
from SQAM.createPatch import startPatch

class Job:
    """ 
    A Class used to represent an Automarking Job
    
    Attributes:
    config : Configuation details passed into the Constructor
    assignment : Representation of Assignment that will be marked
    query_language: SQL Language the Assignment uses
    query_runner: Object that runs submission queries
    grader: Either Partial or Binary. Used to convert query results into grades
    """
    def __init__(self, raw_config):
        """
        Creates all required attributes based off the provided 
        configuration information. 
        
        Precondtion: Provided configuration information contains all required keys
        """
        self.config = Config.get_instance()
        self.config.load_config(raw_config)
        startPatch(self.config.vars["submissions"])


        self.assignment = Assignment(self.config.vars["assignment_name"],
                                     self.config.vars["submissions"],
                                     self.config.vars["student_groups_file"],
                                     self.config.vars["students_csv_file"],
                                     self.config.vars["solutions"],
                                     self.config.vars["assignment_structure"])
        self.assignment.extract_queries()

        if(self.config.vars["db_type"] == "mysql"):
            self.query_language = MySQLQuerier(*self.config.vars["login_details"])
        elif(self.config.vars["db_type"] == "postgresql"):
            # Need to add postgres requirements to requirements.txt before uncommenting
            # self.query_language = PostGreSQLQuerier(*self.config.vars["login_details"])
            pass
        else:
            exit(1)

        self.query_runner = QueryRunner(self.assignment, self.query_language)

        if self.config.vars["marking_type"] == "partial":
            self.grader = Partial_Marking_Grader(self.assignment,self.query_language)
        elif self.config.vars["marking_type"] == "binary":
            self.grader = Binary_Marking_Grader(self.assignment,self.query_language)
        else:
            exit(1)
    
    def run(self):
        """
        Runs an automarking job on an entire assignment
        Creates reports in each submission repo as well as aggregates results into
        one json file. 
        """
        self.query_runner.get_results_for_all_student_groups()
        self.grader.grade_all_student_groups()
        self.grader.generate_unit_tests_for_student_groups()
        self.assignment.dump_results_to_json(self.config.vars["json_output_filename"])
        self.assignment.run_aggregator()
        self.assignment.run_templator()
        print(f'Done Grading Submissions. Class Average: {self.assignment.get_class_average()*100:.2f}%')
