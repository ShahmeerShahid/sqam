import os

ASSIGNMENT_NAME = 'A2'
USING_WINDOWS_SYSTEM = False

# CHANGE sqamv3_path
sqamv3_path = '/Users/sandywang/sqam_old2/automarker/SQAM'

CREATE_TABLES = './Demo/Winter 2020/createTable.sql'
CREATE_TRIGGER = './Demo/Winter 2020/createTrigger.sql'
CREATE_FUNCTION = './Demo/Winter 2020/createFunction.sql'
LOAD_DATA = './Demo/Winter 2020/loadData.sql'
SOLUTIONS = './Demo/Winter 2020/solutions_winter_2020.sql'
SUBMISSIONS = "./Demo/Submissions/"
SUBMISSION_FILE_NAME = "queries.sql"
JSON_OUTPUT_FILENAME = "result.json"
LECTURE_SECTION = "LEC101"

PATH_TO_UAM = os.path.join(sqamv3_path, 'UAM/')
# STUDENTS_CSV_FILE = os.path.join(sqamv3_path, 'Student_Information_and_Submissions_F_2017/students.csv')
# STUDENT_GROUPS_FILE = os.path.join(sqamv3_path, 'Student_Information_and_Submissions_F_2017/groups.txt')
# DIR_AND_NAME_FILE = os.path.join(sqamv3_path, 'Student_Information_and_Submissions_F_2017/dirs_and_names.txt')
STUDENTS_CSV_FILE = os.path.join(sqamv3_path, 'Demo/Student_Information_and_Submissions/students.csv')
STUDENT_GROUPS_FILE = os.path.join(sqamv3_path, 'Demo/Student_Information_and_Submissions/groups.txt')
DIR_AND_NAME_FILE = os.path.join(sqamv3_path, 'Demo/Student_Information_and_Submissions/dirs_and_names.txt')

template_dir = os.path.join(PATH_TO_UAM, 'templates')
TIMEOUT = 100
TIMEOUT_OPERATION = lambda: open('timedout', 'w').close()

# THIS IS THE REGEX PARSER YOU MUST WRITE FOR YOUR ASSIGNMENT STRUCTURE
def query_extractor_re(section):
    return r'[-+\s]+{}[-+\s]+[^;]+;'.format(section)
    # return r'(-- {}.+\s*)(--.+\s*)*(((?<!\s--)|(?<!\s#)|(?<!\s\*/)|(?<!\s/\*))([iI][nN][sS][eE][rR][tT][^;]+;\s*|[sS][eE][tT][^;]+;\s*|[cC][rR][eE][aA][tT][eE][^;]+;\s*|\({{0,1}}[sS][eE][lL][eE][cC][tT][^;]+;\s*))+'.format(section)

# MAX MARKS IN ASSIGNMENT
MAX_MARKS = 80
# MARKS BREAKDOWN PER QUESTION
# max_marks_per_question = [3,4,3,3,4,4,2,2,4,5,3,4,4,4,3,5,6,7]
max_marks_per_question = [3,4,4,4,4,5,4,4,4,4,4,5,6,6,4,4,4,7]
# max_marks_per_question = [3, 3, 3, 3, 3, 3, 3, 3, 3, 3]
# max_marks_per_question = [5,5,5,5,5,5,5,5,5,5]
# QUESTION NAMES IN ASSIGNMENT TEMPLATE
# question_names = ['Q1','Q2','Q3.A','Q3.B','Q3.C','Q4.A','Q4.B','Q4.C','Q5.A','Q5.B','Q6.A',
#                   'Q6.B','Q6.C','Q7.A','Q7.B','Q8','Q9','Q10']
#question_names = ['Q1','Q2','Q3.A','Q3.B','Q3.C','Q4','Q5.A','Q5.B','Q5.C','Q6.A',
#                  'Q6.B','Q7','Q8','Q9','Q10.A','Q10.B','Q10.C','Q11']
question_names = ['Q1','Q2','Q3a','Q3b','Q3c','Q4a','Q4b','Q4c','Q5a','Q5b','Q6a',
                   'Q6b','Q6c','Q7a','Q7b','Q8','Q9','Q10']
# question_names = ['Q1','Q2','Q3','Q4','Q5','Q6','Q7','Q8','Q9','Q10']
# question_names = ['Query 1','Query 2','Query 3','Query 4','Query 5','Query 6','Query 7','Query 8','Query 9','Query 10']

questions = {q_num:max_grade for q_num,max_grade in zip(question_names,max_marks_per_question)}
ASSIGNMENT_STRUCTURE = {'file_name':SUBMISSION_FILE_NAME, 'questions': questions, 'extractor': query_extractor_re}

# Database info # CHANGE THIS
DB_AUTOCOMMIT = True
DB_USER_NAME = 'root'
# DB_USER_NAME = 'SaihielBakshi'
DB_PASSWORD = 'admin'
# DB_NAME = 'SaihielBakshi'
DB_NAME = 't343'
DB_HOST = "192.168.0.84"
DB_PORT = int(3306)
LOGIN_DETAILS = (DB_USER_NAME,DB_PASSWORD,DB_NAME,DB_HOST,DB_PORT,DB_AUTOCOMMIT)
DB_TYPE = 'mysql'
MARKING_TYPE = 'partial'
