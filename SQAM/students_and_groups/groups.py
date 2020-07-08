import re
import sys
import json
from SQAM.config import SUBMISSION_FILE_NAME
all_groups = 0
class StudentGroup:
    """
    A representation of a student group
    """
    @staticmethod
    def make_group_from_group_file(path_to_submission, group_line, student_list):
        '''
        Instantiate and return a Student group from a line in Markus generated groups.txt file
        group file format:
        group-name,dir-name,student-id-1,student-id-2,...
        '''
        group_info = group_line.strip().split(',')
        dct = {k:v for k,v in zip(['group_id', 'dir_name'],group_info[:2])}
        dct['number_of_students'] = len(group_info)-2
        dct['path_to_submission'] = path_to_submission
        dct['students'] = [student_list.get(s_id) for s_id in group_info[2:] if s_id]
        if not any(dct['students']):
            print(f'No students found for group: {dct["group_id"]}')
        return StudentGroup(**dct)

    def __init__(self, **kwargs):
        self.group_id = kwargs.get('group_id')
        self.number_of_students = kwargs.get('number_of_students')
        self.path_to_submission = kwargs.get('path_to_submission') + kwargs.get('dir_name') +'/'+ SUBMISSION_FILE_NAME
        self.path_to_group_dir = kwargs.get('path_to_submission') + kwargs.get('dir_name')
        self.assignment = None

        self.total_grade = 0.0
        self.all_grades = {}
        self.all_similarity_scores = {}
        self.submission_graded = False

        self.query_errors = {}
        self.query_results = {}
        self.results_collected = False

        self.queries = {}
        self.queries_extracted = False

        self.incorrect_query_details = {}
        self.extra_columns_per_query = {}

        self.results_as_test_cases = {}

        self.students = kwargs.get('students')

    def set_grade_for_students(self, grade):
        self.total_grade = grade
        for student in self.students:
            student.grade = grade
        self.submission_graded = True

    def extract_query(self, query_name, regex_extractor):
        query_regex = re.compile(regex_extractor(query_name))
        with open(self.path_to_submission, 'r') as fd:
            file = fd.read()
            match = query_regex.search(file)
            self.queries[query_name] = match.group() if match else None

    def extract_all_queries(self, query_names, regex_extractor):
        for query in query_names:
            self.extract_query(query, regex_extractor)
        self.queries_extracted = True

    def get_students_as_dict(self):
        students = []
        for student in self.students:
            students.append(student.student_as_dict())
        return students

    def dump_json_output_to_student_folder(self, json_results_name):
        output = {}
        output["students"] = self.get_students_as_dict()
        output['results'] = self.results_as_test_cases
        target_location = self.path_to_group_dir + '/' + json_results_name
        try:
            with open(target_location, 'w') as tgt:
                tgt.write('%s\n' % self.to_json(output))
        except IOError as error:
            print('Could not write JSON result to file. %s' % error,
                  file=sys.stderr)

    def to_dict(self):
        ret_dict = {}
        ret_dict['Group Name'] = self.group_id
        for student in self.students:
            ret_dict['Student 1\'s ID'] = student.student_id


    def to_json(self, results):
        '''Produce a UAM compatible JSON result string, suitable for
        templating and aggregating.
        '''
        try:
            return json.dumps(results)
        except TypeError as err:
            print('Cannot generate a JSON string from test results. %s' % err,
                  file=sys.stderr)
            return '{}'

    @property
    def students(self):
        return self.__students

    @students.setter
    def students(self, list_of_students):
        if list_of_students:
            self.__students = list_of_students
            for student in self.__students:
                student.group = self
    
    @property
    def assignment(self):
        return self.__assignment
    
    @assignment.setter
    def assignment(self, assign):
        self.__assignment = assign
    
    @property
    def total_grade(self):
        return self.__total_grade
    
    @total_grade.setter
    def total_grade(self, value):
        self.__total_grade = value



