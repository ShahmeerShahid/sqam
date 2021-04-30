import re
import json
import traceback
import time


class Submission:
    """
    A representation of a Submission. A submission
    knows how to extract queries and use the querier and grader.
    """

    def __init__(self, name, path_to_submission_file, path_to_results_file, refresh_level):
        self.name = name
        self.path_to_submission = path_to_submission_file
        self.path_to_results_file = path_to_results_file
        self.refresh_level = refresh_level

        self.total_grade = 0.0
        self.all_grades = {}
        self.all_similarity_scores = {}
        self.submission_graded = False

        self.query_errors = {}
        self.query_results = {}
        self.results_collected = False
        self.incorrect_query_details = {}
        self.extra_columns_per_query = {}
        self.results_as_test_cases = {}
        self.queries = {}
        self.question_marks = {}

    def refresh_dic(self):
        """
        This function resets the submissions attributes. 
        This should be phased out from refactoring the graders 
        """
        self.total_grade = 0.0
        self.all_grades = {}
        self.all_similarity_scores = {}
        self.submission_graded = False
        self.query_errors = {}
        self.query_results = {}
        self.results_collected = False
        self.incorrect_query_details = {}
        self.extra_columns_per_query = {}
        self.results_as_test_cases = {}
        self.queries = {}

    def grade_submission(self, questions, querier, grader, all_questions, group_marks):
        """
        Grades all questions for the given submission. Uses the querier and grader.
        """
        try:
            if self.refresh_level == "per_submission":
                querier.setup()
            self.extract_all_queries(questions)
            print("Getting the queries result")
            self.get_results_for_submission(querier)
            print("Grading started")
            grader.grade_group(self, all_questions)
            grader.generate_test_results_for_group(self)
            self.dump_json_output_to_submission_folder()
            print(
                f"Finished Grading: {self.name} Mark: {self.total_grade}", flush=True)
            group_marks[self.name] = self.all_grades
            self.refresh_dic()
        except Exception as e:
            print(
                f"Grading Failed for {self.name} Exception: {traceback.print_exc()}", flush=True)

    def extract_all_queries(self, query_names):
        """
        Extract all queries 
        """
        with open(self.path_to_submission, "r") as fd:
            file = fd.read()
            for query in query_names:
                query_regex = re.compile(
                    r"([-+\s]+START {})([\s\S]*?)([-+\s]+END {})".format(query, query)
                )
                match = query_regex.search(file)
                self.queries[query] = match.group() if match else None

    def dump_json_output_to_submission_folder(self):
        """
        Dumps the submissions results into a json text file 
        """
        output = {}
        output["results"] = self.results_as_test_cases

        target_location = self.path_to_results_file
        with open(target_location, "w") as tgt:
            tgt.write("%s\n" % json.dumps(output))

    def get_results_for_submission(self, querier):
        """
        Use the querier to run the submission queries 
        """
        for q_num, query in self.queries.items():
            if query:
                if self.refresh_level == "per_query":
                    querier.setup()
                result, error = querier.run_multi_query(query)
                self.query_errors[q_num] = error
                self.query_results[q_num] = result
            else:
                self.query_results[q_num] = [("", ""), ("", "")]
        self.results_collected = True
