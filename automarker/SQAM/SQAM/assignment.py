from SQAM.result_formating.templator import aggregate_report_SQAM, individual_reports_SQAM
from SQAM.result_formating.aggregator import Aggregate_SQAM
from SQAM.submission import Submission
from SQAM.graders.create_grader import create_grader
from SQAM.solution import get_solution_results
import SQAM.settings
import os
import shutil
import json


class Assignment:
    """
    Representation of assignment. An assigment has many submissions.
    The assigment creates the submissions using the path_to_submissions filepath.
    """

    def __init__(self, name, path_to_submissions, path_to_solutions, file_name,
                 questions, max_marks, querier, refresh_level, marking_type="binary"):
        self.assignment_name = name
        self.refresh_level = refresh_level
        self.file_name = file_name
        self.questions = questions
        self.path_to_submissions = path_to_submissions
        self.submissions = self.create_submissions()
        self.max_marks = max_marks
        self.querier = querier
        solution_results, solution_errors = get_solution_results(
            path_to_solutions, questions, querier)
        # TODO Handle Errors in the solution File. Should send solution file errors to user
        self.grader = create_grader(marking_type, questions, solution_results)
        self.all_questions = {}
        self.group_marks = {}

    def create_submissions(self):
        submissions = []
        submission_paths = [item for item in os.listdir(
            self.path_to_submissions) if os.path.isdir(os.path.join(self.path_to_submissions, item))]
        for name in submission_paths:
            path_to_submission_dir = os.path.join(
                self.path_to_submissions, name)
            path_to_submission_file = os.path.join(
                path_to_submission_dir, self.file_name)
            path_to_result_file = os.path.join(
                path_to_submission_dir, SQAM.settings.JSON_RESULT_FILENAME)
            submission = Submission(name,
                                    path_to_submission_file,
                                    path_to_result_file,
                                    self.refresh_level)
            submissions.append(submission)
        return submissions

    def mark_submissions(self):
        if self.refresh_level == "per_assignment":
            self.querier.setup()
        for submission in self.submissions:
            submission.grade_submission(
                self.questions.keys(), self.querier, self.grader, self.all_questions, self.group_marks)

    def get_average(self):
        all_totals = []
        for submission in self.submissions:
            all_totals.append(submission.total_grade)
        return (sum(all_totals)/len(all_totals))/self.max_marks if len(all_totals) > 0 else 0

    def run_templator(self):
        aggregate_report_SQAM(os.path.join(self.path_to_submissions, "aggregated.json"),
                              SQAM.settings.TEMPLATES_PATH,
                              os.path.join(self.path_to_submissions, "report"))
        individual_reports_SQAM(os.path.join(self.path_to_submissions, "aggregated.json"),
                                SQAM.settings.TEMPLATES_PATH,
                                "report")
        results_directory = os.path.join(
            self.path_to_submissions, "all_results")
        submission_paths = [item for item in os.listdir(
            self.path_to_submissions) if os.path.isdir(os.path.join(self.path_to_submissions, item))]
        print(submission_paths)
        os.mkdir(results_directory)
        for name in submission_paths:
            try:
                shutil.copyfile(os.path.join(self.path_to_submissions, name, "report.txt"),
                                os.path.join(results_directory, f"{name}-report.txt"))
            except:
                print(
                    "Fail to copy the report.txt file for the submission ", submission_paths)
        shutil.make_archive(os.path.join(
            self.path_to_submissions, "aggregated"), "zip", results_directory)
        shutil.rmtree(results_directory)
        # Generate aggregate.txt
        with open(self.path_to_submissions+os.sep+"aggregate.txt", "w")as f:
            for q, lst in self.all_questions.items():
                zero_count = str(lst.count(0))
                max_count = str(lst.count(self.questions[q]))
                avg = "%.3f" % (sum(lst) / len(lst))
                f.write(q+"      avg:"+avg+"/" +
                        str(self.questions[q])+"      0%:"+zero_count+"       100%:"+max_count+"\n")
        # Generagte breakdown.txt
        with open(self.path_to_submissions+os.sep+"breakdown.txt", "w")as f:
            f.write(json.dumps(self.group_marks))

    def run_aggregator(self):
        Aggregate_SQAM(self.assignment_name,
                       self.path_to_submissions,
                       SQAM.settings.JSON_RESULT_FILENAME,
                       os.path.join(self.path_to_submissions, "aggregated.json"))
