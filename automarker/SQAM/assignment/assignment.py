from SQAM.students_and_groups.class_list import ClassList
from SQAM.config import TIMEOUT, TIMEOUT_OPERATION, MAX_MARKS,USING_WINDOWS_SYSTEM, \
                        PATH_TO_UAM, DIR_AND_NAME_FILE, STUDENTS_CSV_FILE, STUDENT_GROUPS_FILE
import subprocess
import signal
import os

class Assignment:
    def __init__(self):
        self.assignment_name = ""
        self.solution_results = {}
        self.path_to_solutions = None
        self.file_name = None
        self.questions = None
        self.extractor = None
        self.class_list = None
        self.path_to_submissions = None
        self.groups_list_file = None
        self.student_list_file = None

    def generate_class_list(self):
        self.class_list = ClassList(self.path_to_submissions,self.groups_list_file,self.student_list_file)

    def set_assignment_structure(self, assignment_structure):
        self.file_name = assignment_structure['file_name']
        self.questions = assignment_structure['questions']
        self.extractor = assignment_structure['extractor']

    def __setattr__(self, key, value):
        self.key=value

    def extract_queries(self):
        for group in self.class_list:
            group.extract_all_queries(self.questions.keys(), self.extractor)

    def dump_results_to_json(self, json_name):
        for group in self.class_list:
            group.dump_json_output_to_student_folder(json_name)

    def get_class_average(self):
        all_totals = []
        for group in self.class_list:
            all_totals.append(group.total_grade)
        return (sum(all_totals)/len(all_totals))/MAX_MARKS if len(all_totals)>0 else 0

    # def create_to_grades_csv(self):


    def run_templator(self):
        temp_py = PATH_TO_UAM + "templator.py "
        run_templator_cmd = "python3 "+ temp_py+"aggregated.json txt"
        cmd = run_templator_cmd
        self.run_as_subprocess(cmd)

    def run_aggregator(self):
        agg_py = PATH_TO_UAM + "aggregator.py "
        python_type = "python3 " if not USING_WINDOWS_SYSTEM else "python"
        run_aggregator_cmd = python_type + agg_py + self.assignment_name+" "
        paths = DIR_AND_NAME_FILE+" "+STUDENTS_CSV_FILE+" "+STUDENT_GROUPS_FILE
        cmd = run_aggregator_cmd + paths
        self.run_as_subprocess(cmd)

    def run_as_subprocess(self, cmd):
        proc = subprocess.Popen(cmd.split(), stdout=subprocess.PIPE)
        try:
            stdout_data, stderr_data = proc.communicate(timeout=TIMEOUT)
        except subprocess.TimeoutExpired:
            TIMEOUT_OPERATION()
            try:
                os.killpg(proc.pid, signal.SIGTERM)
            except ProcessLookupError:
                pass  # the process terminated after the timeout was generated
        if proc.returncode != 0:
            print("Test terminated abnormally")

