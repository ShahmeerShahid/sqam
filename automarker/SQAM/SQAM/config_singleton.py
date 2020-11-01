import os
import json

class Config:
    _instance = None

    def __init__(self):
        if Config._instance is None:
            self.vars = dict()
            Config._instance = self
    
    def load_config(self, json_string):
        h = json.loads(json_string)
        self.vars = h
        self.synthesize_composite_vars()
    
    def synthesize_composite_vars(self):
        self.vars["path_to_uam"] = os.path.join(self.vars["sqamv3_path"], 'UAM/')
        self.vars["students_csv_file"] = os.path.join(self.vars["sqamv3_path"], "Demo/Student_Information_and_Submissions/students.csv")
        self.vars["student_groups_file"] = os.path.join(self.vars["sqamv3_path"], "Demo/Student_Information_and_Submissions/groups.txt")
        self.vars["dir_and_name_file"] = os.path.join(self.vars["sqamv3_path"], 'Demo/Student_Information_and_Submissions/dirs_and_names.txt')
        self.vars["template_dir"] = os.path.join(self.vars["path_to_uam"], "templates")
        self.vars["timeout_operation"] = lambda : open("timedout", "w").close()
        self.vars["query_extractor_re"] = lambda section : r'[-+\s]+{}[-+\s]+[^;]+;'.format(section)
        self.vars["questions"] = {q_num:max_grade for q_num,max_grade in zip(self.vars["question_names"],self.vars["max_marks_per_question"])}
        self.vars["assignment_structure"] = {'file_name':self.vars["submission_file_name"], 'questions': self.vars["questions"], 'extractor': self.vars["query_extractor_re"]}
        self.vars["login_details"] = (self.vars["db_user_name"],self.vars["db_password"],self.vars["db_name"],self.vars["db_host"],self.vars["db_port"],self.vars["db_autocommit"])

    def get_instance():
        if Config._instance is None:
            Config()
        return Config._instance