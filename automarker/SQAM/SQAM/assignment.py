from SQAM.result_formating.templator import aggregate_report_SQAM
from SQAM.result_formating.aggregator import Aggregate_SQAM
from SQAM.submission import Submission
import SQAM.settings
import os

class Assignment:
    def __init__(self, name, path_to_submissions, path_to_solutions, file_name,
                questions, extractor, max_marks, querier):
        self.assignment_name = name
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
                                    self.questions.keys(),
                                    self.extractor)
            submissions.append(submission)
        return submissions

    def dump_results_to_json(self):
        for submission in self.submissions:
            submission.dump_json_output_to_submission_folder()

    def get_average(self):
        all_totals = []
        for submission in self.submissions:
            all_totals.append(submission.total_grade)
        return (sum(all_totals)/len(all_totals))/self.max_marks if len(all_totals)>0 else 0

    def run_templator(self):
        aggregate_report_SQAM(os.path.join(self.path_to_submissions, "aggregated.json"),
                                SQAM.settings.TEMPLATES_PATH,
                                os.path.join(self.path_to_submissions + "report"))

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

    def get_results_for_all_submissions(self):
        for submission in self.submissions:
            submission.get_results_for_submission(self.querier)
            