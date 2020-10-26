from SQAM.assignment.assignment import Assignment
from SQAM.autograder.partial_marking_grader import Partial_Marking_Grader
from SQAM.autograder.binary_grader import Binary_Marking_Grader
from SQAM.query_languages.MySQL import MySQLQuerier
from SQAM.query_languages.PostGreSQL import PostGreSQLQuerier
from SQAM.query_languages.query_runner import QueryRunner
from SQAM.config import SUBMISSIONS, STUDENTS_CSV_FILE, STUDENT_GROUPS_FILE, \
    ASSIGNMENT_STRUCTURE, SOLUTIONS, LOGIN_DETAILS, JSON_OUTPUT_FILENAME, ASSIGNMENT_NAME,\
        DB_TYPE,MARKING_TYPE

if __name__ == "__main__":
    assignment = Assignment()
    setattr(assignment, 'assignment_name', ASSIGNMENT_NAME)
    setattr(assignment, 'path_to_submissions', SUBMISSIONS)
    setattr(assignment, 'groups_list_file', STUDENT_GROUPS_FILE)
    setattr(assignment, 'student_list_file', STUDENTS_CSV_FILE)
    setattr(assignment, 'path_to_solutions', SOLUTIONS)
    assignment.set_assignment_structure(ASSIGNMENT_STRUCTURE)
    assignment.generate_class_list()
    assignment.extract_queries()
    if DB_TYPE == "mysql":
        query_language = MySQLQuerier(*LOGIN_DETAILS)
    elif DB_TYPE = "postgresql":
        query_language = PostGreSQLQuerier(*LOGIN_DETAILS)
    else:
        print("Database type not supported.")
        exit(0)
    query_runner = QueryRunner(assignment, query_language)
    query_runner.get_results_for_all_student_groups()
    if MARKING_TYPE == "partial":
        grader = Partial_Marking_Grader(assignment,query_language)
    elif MARKING_TYPE == "binary":
        grader = Binary_Marking_Grader(assignment,query_language)
    else:
        print("Marking type not supported.")
        exit(0)
    grader.grade_all_student_groups()
    grader.generate_unit_tests_for_student_groups()
    assignment.dump_results_to_json(JSON_OUTPUT_FILENAME)
    assignment.run_aggregator()
    assignment.run_templator()
    print(f'Done Grading Submissions. Class Average: {assignment.get_class_average()*100:.2f}%')
