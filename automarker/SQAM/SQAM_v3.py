from SQAM.assignment.assignment import Assignment
from SQAM.autograder.partial_marking_grader import Partial_Marking_Grader
from SQAM.query_languages.MySQL import MySQLQuerier
from SQAM.query_languages.query_runner import QueryRunner
from SQAM.config import SUBMISSIONS, STUDENTS_CSV_FILE, STUDENT_GROUPS_FILE, \
    ASSIGNMENT_STRUCTURE, SOLUTIONS, LOGIN_DETAILS, JSON_OUTPUT_FILENAME, ASSIGNMENT_NAME

if __name__ == "__main__":
    assignment = Assignment()
    assignment.__setattr__('assignment_name', ASSIGNMENT_NAME)
    assignment.__setattr__('path_to_submissions', SUBMISSIONS)
    assignment.__setattr__('groups_list_file', STUDENT_GROUPS_FILE)
    assignment.__setattr__('student_list_file', STUDENTS_CSV_FILE)
    assignment.__setattr__('path_to_solutions', SOLUTIONS)
    assignment.set_assignment_structure(ASSIGNMENT_STRUCTURE)
    assignment.generate_class_list()
    assignment.extract_queries()
    query_language = MySQLQuerier(*LOGIN_DETAILS)
    query_runner = QueryRunner(assignment, query_language)
    query_runner.get_results_for_all_student_groups()
    grader = Partial_Marking_Grader(assignment,query_language)
    grader.grade_all_student_groups()
    grader.generate_unit_tests_for_student_groups()
    assignment.dump_results_to_json(JSON_OUTPUT_FILENAME)
    assignment.run_aggregator()
    assignment.run_templator()
    print(f'Done Grading Submissions. Class Average: {assignment.get_class_average()*100:.2f}%')
