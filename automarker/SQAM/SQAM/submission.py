import re
import json

class Submission:
    """
    A representation of a Submission
    """
    def __init__(self, name, path_to_submission_file, path_to_results_file):
        self.name = name
        self.path_to_submission = path_to_submission_file
        self.path_to_results_file = path_to_results_file

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

    def grade_submission(self, questions, regex_extractor, querier, grader):
        try:
            self.extract_all_queries(questions, regex_extractor)
            self.get_results_for_submission(querier)
            grader.grade_group(self)
            grader.generate_test_results_for_group(self)
            self.dump_json_output_to_submission_folder()
            print(f"Finished Grading: {self.name} Mark: {self.total_grade}", flush=True)
        except Exception as e:
            print(f"Grading Failed for {self.name} Exception: {e}", flush=True)

    def extract_query(self, query_name, regex_extractor):
        query_regex = re.compile(regex_extractor(query_name))
        with open(self.path_to_submission, 'r') as fd:
            file = fd.read()
            match = query_regex.search(file)
            self.queries[query_name] = match.group() if match else None

    def extract_all_queries(self, query_names, regex_extractor):
        for query in query_names:
            self.extract_query(query, regex_extractor)

    def dump_json_output_to_submission_folder(self):
        output = {}
        output['results'] = self.results_as_test_cases
        
        target_location = self.path_to_results_file
        with open(target_location, 'w') as tgt:
            tgt.write('%s\n' % json.dumps(output))
       
    def get_results_for_submission(self, query_language):
        for q_num, query in self.queries.items():
            if query:
                result, error = query_language.run_multi_query(query) if query.count(';') > 1 \
                        else query_language.run_single_query(query)
                if error:
                    self.query_errors[q_num] = error
                self.query_results[q_num] = result
            else:
                self.query_results[q_num] = [("", ""), ("", "")]
        self.results_collected = True



