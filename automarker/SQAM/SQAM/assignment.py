from SQAM.result_formating.templator import aggregate_report_SQAM, individual_reports_SQAM
from SQAM.result_formating.aggregator import Aggregate_SQAM
from SQAM.submission import Submission
import SQAM.settings
import os
import shutil

class Assignment:
    def __init__(self, name, path_to_submissions, path_to_solutions, file_name,
                questions, extractor, max_marks, querier, refresh_level):
        self.assignment_name = name
        self.refresh_level = refresh_level
        self.path_to_solutions = path_to_solutions
        self.file_name = file_name
        self.questions = questions
        self.extractor = extractor
        self.path_to_submissions = path_to_submissions
        self.submissions = self.create_submissions()
        self.max_marks = max_marks
        self.querier = querier
        self.solution_results, self.solution_errors = self.get_solution_results(
            self.path_to_solutions, list(self.questions.keys()), 1)

    def create_submissions(self):
        submissions = []
        submission_paths = [ item for item in os.listdir(self.path_to_submissions) if os.path.isdir(os.path.join(self.path_to_submissions, item)) ]
        for name in submission_paths:
            path_to_submission_dir = os.path.join(self.path_to_submissions, name)
            path_to_submission_file = os.path.join(path_to_submission_dir, self.file_name)
            path_to_result_file = os.path.join(path_to_submission_dir, SQAM.settings.JSON_RESULT_FILENAME)
            submission = Submission(name,
                                    path_to_submission_file,
                                    path_to_result_file,
                                    self.refresh_level)
            submissions.append(submission)
        return submissions

    def mark_submissions(self, grader):
        if self.refresh_level == "per_assignment": self.querier.refreshDB()
        for submission in self.submissions:
            submission.grade_submission(self.questions.keys(), self.extractor, self.querier, grader)

    def get_average(self):
        all_totals = []
        for submission in self.submissions:
            all_totals.append(submission.total_grade)
        return (sum(all_totals)/len(all_totals))/self.max_marks if len(all_totals)>0 else 0

    def run_templator(self):
        aggregate_report_SQAM(os.path.join(self.path_to_submissions, "aggregated.json"),
                                SQAM.settings.TEMPLATES_PATH,
                                os.path.join(self.path_to_submissions, "report"))
        individual_reports_SQAM(os.path.join(self.path_to_submissions, "aggregated.json"),
                                SQAM.settings.TEMPLATES_PATH,
                                "report")
        results_directory = os.path.join(self.path_to_submissions, "all_results")
        submission_paths = [ item for item in os.listdir(self.path_to_submissions) if os.path.isdir(os.path.join(self.path_to_submissions, item)) ]
        os.mkdir(results_directory)
        for name in submission_paths:
            shutil.copyfile(os.path.join(self.path_to_submissions,name, "report.txt"),
                            os.path.join(results_directory, f"{name}-report.txt"))
        
        shutil.make_archive(os.path.join(self.path_to_submissions, "aggregated"), "zip", results_directory)
        shutil.rmtree(results_directory) 

    def run_aggregator(self):
        Aggregate_SQAM(self.assignment_name, 
                        self.path_to_submissions,
                        SQAM.settings.JSON_RESULT_FILENAME,
                        os.path.join(self.path_to_submissions,"aggregated.json"))

    def get_solution_results(self, solutions_file, query_names, verbose=0):
        """
        Runs and collects the solutions provided by the professor
        @param verbose: level of printing for debugging
        @return: The result collected for each query as a dictionary with query number as key and query results as values.
        """
        # Open and read the file as a single buffer
        with open(solutions_file, 'r') as fd:
            sqlFile = fd.read()
            all_solutions, errors = self.querier.run_SQL_file(sqlFile, query_names, verbose)
        return all_solutions, errors