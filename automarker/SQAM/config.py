import os

ASSIGNMENT_NAME = 'A2'
USING_WINDOWS_SYSTEM = False

# CHANGE sqamv3_path
sqamv3_path = '/Users/SaihielBakshi/sqam_v3'

CREATE_TABLES = './Winter_2020/createTable.sql'
LOAD_DATA = './Winter_2020/loadData.sql'
SOLUTIONS = './Winter_2020/solutions_winter_2020.sql'
SUBMISSIONS = "./Winter_2020/Submissions/"
SUBMISSION_FILE_NAME = "queries.sql"
JSON_OUTPUT_FILENAME = "result.json"
LECTURE_SECTION = "LEC101"

PATH_TO_UAM = os.path.join(sqamv3_path, 'UAM/')
STUDENTS_CSV_FILE = os.path.join(sqamv3_path, 'SQAM/Student_Information_and_Submissions/students.csv')
STUDENT_GROUPS_FILE = os.path.join(sqamv3_path, 'SQAM/Student_Information_and_Submissions/groups.txt')
DIR_AND_NAME_FILE = os.path.join(sqamv3_path, 'SQAM/Student_Information_and_Submissions/dirs_and_names.txt')

template_dir = os.path.join(PATH_TO_UAM, 'templates')
TIMEOUT = 100
TIMEOUT_OPERATION = lambda: open('timedout', 'w').close()

# THIS IS THE REGEX PARSER YOU MUST WRITE FOR YOUR ASSIGNMENT STRUCTURE
def query_extractor_re(section):
    return r'[-+\s]+{}[-+\s]+[^;]+;'.format(section)

# MAX MARKS IN ASSIGNMENT
MAX_MARKS = 70
# MARKS BREAKDOWN PER QUESTION
max_marks_per_question = [3,4,3,3,4,4,2,2,4,5,3,4,4,4,3,5,6,7]
# QUESTION NAMES IN ASSIGNMENT TEMPLATE
question_names = ['Q1','Q2','Q3.A','Q3.B','Q3.C','Q4.A','Q4.B','Q4.C','Q5.A','Q5.B','Q6.A',
                  'Q6.B','Q6.C','Q7.A','Q7.B','Q8','Q9','Q10']

questions = {q_num:max_grade for q_num,max_grade in zip(question_names,max_marks_per_question)}
ASSIGNMENT_STRUCTURE = {'file_name': "queries.sql", 'questions': questions, 'extractor': query_extractor_re}

# Database info # CHANGE THIS
DB_AUTOCOMMIT = True
DB_USER_NAME = 'pythonUser'
DB_PASSWORD = 'Password123'
DB_NAME = 'sb'
LOGIN_DETAILS = (DB_USER_NAME,DB_PASSWORD,DB_NAME,DB_AUTOCOMMIT)

