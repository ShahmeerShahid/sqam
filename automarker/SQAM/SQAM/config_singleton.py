import os

class Config:
    _instance = None

    def __init__(self):
        if Config._instance is None:
            self.vars = dict()
            Config._instance = self
    
    def load_config(self, json_config):
        self.vars["using_windows_system"] = False
        self.vars["sqamv3_path"] = "./automarker/SQAM/"
        self.vars["json_output_filename"] = "result.json"
        self.vars["lecture_section"] = "1"

        req_args = ["tid", "assignment_name", "create_tables", "create_trigger", "create_function", "load_data", "solutions", "submissions", "submission_file_name",
        "timeout", "max_marks", "max_marks_per_question", "question_names", "db_type", "marking_type"]

        for arg in req_args:
            if arg not in json_config:
                raise Exception(f"Required argument {arg}")
            self.vars[arg] = json_config[arg]
        
        self.vars["db_autocommit"] = True
        if self.vars["db_type"] == "mysql":
            self.vars["db_user_name"] = os.getenv('MYSQL_USER', 'automarkercsc499')
            self.vars["db_password"] = os.getenv('MYSQL_PASSWORD', 'csc499')
            self.vars["db_host"] = "mysqlam"
            self.vars["db_port"] = 3306
        elif self.vars["db_type"] == "postgres":
            raise Exception("Postgres not yet supported")
        else:
            raise Exception("Invalid db_type")
    
        self.vars["db_name"] = "t"+str(self.vars["tid"])

        self.synthesize_composite_vars()

    
    def synthesize_composite_vars(self):
        self.vars["path_to_uam"] = os.path.join(self.vars["sqamv3_path"], 'UAM/')
        self.vars["students_csv_file"] = os.path.join(self.vars["submissions"], "students.csv")
        self.vars["student_groups_file"] = os.path.join(self.vars["submissions"], "groups.txt")
        self.vars["dir_and_name_file"] = os.path.join(self.vars["submissions"], 'dirs_and_names.txt')
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
