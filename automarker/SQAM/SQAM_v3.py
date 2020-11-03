#!/usr/bin/python
from SQAM.assignment.assignment import Assignment
from SQAM.autograder.partial_marking_grader import Partial_Marking_Grader
from SQAM.autograder.binary_grader import Binary_Marking_Grader
from SQAM.query_languages.query_runner import QueryRunner
from SQAM.config_singleton import Config
import sys, json

if __name__ == "__main__":
    
    # from SQAM.config import SUBMISSIONS, STUDENTS_CSV_FILE, STUDENT_GROUPS_FILE, \
    #     ASSIGNMENT_STRUCTURE, SOLUTIONS, LOGIN_DETAILS, JSON_OUTPUT_FILENAME, ASSIGNMENT_NAME, \
    #         DB_TYPE,MARKING_TYPE
    config = Config.get_instance()
    if (len(sys.argv) > 2):
        DELIMITER = chr(255)
        newConfigListkey = sys.argv[1].split(DELIMITER)
        newConfigListvalues = sys.argv[2].split(DELIMITER)
        ConfigDict = dict(zip(newConfigListkey, newConfigListvalues))
        ConfigDict["max_marks_per_question"] = list(map(int, ConfigDict["max_marks_per_question"].split(",")))
        ConfigDict["question_names"] = ConfigDict["question_names"].split(",")
        ConfigDict["max_marks"] = float(ConfigDict["max_marks"])
        ConfigDict["db_port"] = int(ConfigDict["db_port"])
        ConfigDict["timeout"] = int(ConfigDict["timeout"])
        ConfigDict["using_windows_system"] = ConfigDict["using_windows_system"] == "true"
        ConfigDict["db_autocommit"] = ConfigDict["db_autocommit"] == "true"
        h = json.dumps(ConfigDict) 
        config.load_config(h)
    else: 
        # TODO: remove this when flask server is properly using config_singleton
        with open("./SQAM/config.json","r") as config_file:
            content = config_file.read()
            config.load_config(content)

    if config.vars["db_type"] == "mysql":
        from SQAM.query_languages.MySQL import MySQLQuerier
    elif config.vars["db_type"] == "postgresql":
        from SQAM.query_languages.PostGreSQL import PostGreSQLQuerier
    else:
        print("Database type not supported.")
        exit(0)
    if __name__ == "__main__":
        assignment = Assignment()
        setattr(assignment, 'assignment_name', config.vars["assignment_name"])
        setattr(assignment, 'path_to_submissions', config.vars["submissions"])
        setattr(assignment, 'groups_list_file', config.vars["student_groups_file"])
        setattr(assignment, 'student_list_file', config.vars["students_csv_file"])
        setattr(assignment, 'path_to_solutions', config.vars["solutions"])
        assignment.set_assignment_structure(config.vars["assignment_structure"])
        assignment.generate_class_list()
        assignment.extract_queries()
        if config.vars["db_type"] == "mysql":
            query_language = MySQLQuerier(*config.vars["login_details"])
        elif config.vars["db_type"] == "postgresql":
            query_language = PostGreSQLQuerier(*config.vars["login_details"])
        else:
            print("Database type not supported.")
            exit(0)
        query_runner = QueryRunner(assignment, query_language)
        query_runner.get_results_for_all_student_groups()
        if config.vars["marking_type"] == "partial":
            grader = Partial_Marking_Grader(assignment,query_language)
        elif config.vars["marking_type"] == "binary":
            grader = Binary_Marking_Grader(assignment,query_language)
        else:
            print("Marking type not supported.")
            exit(0)
        grader.grade_all_student_groups()
        grader.generate_unit_tests_for_student_groups()
        assignment.dump_results_to_json(config.vars["json_output_filename"])
        assignment.run_aggregator()
        assignment.run_templator()
        print(f'Done Grading Submissions. Class Average: {assignment.get_class_average()*100:.2f}%')
