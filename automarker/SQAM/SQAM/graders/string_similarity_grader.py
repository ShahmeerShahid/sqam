from SQAM.graders.grader import Grader
import Levenshtein as lev
from fuzzywuzzy import fuzz
import textdistance
import statistics as stats
from SQAM.graders.my_utils import sort_list_of_tuples_as_strings


class String_Similarity_Grader(Grader):

    def __init__(self, questions, solution_results, kind):
        super().__init__(questions, solution_results)
        self.kind = kind

    def grade_question_for_group(self, group_results, solution_results, max_marks):
        """
        @param group's results: result tuples returned by running group's SQL query
        @param solution's results: result tuples returned by running solution's SQL query
        @param max_marks: maximum marks for given query
        @return: marks earned by group as float
        """
        if group_results == solution_results:
            group_grade = max_marks
            group_similarity_ratio = 1.0
        elif group_results:
            group_grade, group_similarity_ratio = self.getMarksForQuestion(
                group_results, solution_results, max_marks)
        else:
            group_grade, group_similarity_ratio = 0.0, 0.0
        return group_grade, group_similarity_ratio

    def getMarksForQuestion(self, studentAns, solution, maxMarksForQuestion):
        """
        Grade the students answer using string similarity metrics
        @param studentAns: Students output for a single query
        @param solution: Correct output for query
        @param maxMarksForQuestion: Max possible marks for query
        @return: The student's mark
        """
        ratio = self.getDistance(studentAns, solution)
        marks = round(maxMarksForQuestion * ratio)
        marks = maxMarksForQuestion if marks > maxMarksForQuestion else marks
        if self.kind == "binary":
            marks = maxMarksForQuestion if ratio > 0.95 else 0
        else:
            marks = maxMarksForQuestion if ratio > 0.90 else marks
        return marks, ratio

    def apply_distance_metric_on_tuples(self, tup1, tup2, func):
        vals = []
        newtup1 = list(tup1)
        newtup2 = list(tup2)
        for item1, item2 in zip(newtup1, newtup2):
            val = func(item1, item2)
            vals.append(val)
        return sum(vals) / len(vals) if len(vals) > 0 else 0

    def get_greatest_average_similarity(self, list_of_list_of_all_string_distances):
        max_score = 0
        all_scores = []
        for list_of_string_distances in list_of_list_of_all_string_distances:
            if isinstance(list_of_string_distances, list) and len(list_of_string_distances) > 0:
                score = sum(list_of_string_distances) / \
                    len(list_of_string_distances)
                score = score / 100 if score > 1 else score
                max_score = score if score > max_score else max_score
                all_scores.append(score)
        if self.kind == "binary":
            return stats.mean(all_scores)
        else:
            return max_score

    def getDistance(self, student_results, solution_results):
        student_results_lst = sort_list_of_tuples_as_strings(
            student_results[1:])
        solution_results_lst = sort_list_of_tuples_as_strings(
            solution_results[1:])
        ratcliff_dist, jaro_winkler_dist, fuzzy_token_dist, levenshtein_dist = [], [], [], []

        for item1, item2 in zip(student_results_lst, solution_results_lst):
            levenshtein_dist.append(lev.seqratio(item1, item2))
            fuzzy_token_dist.append(self.apply_distance_metric_on_tuples(
                item1, item2, fuzz.token_sort_ratio))
            jaro_winkler_dist.append(self.apply_distance_metric_on_tuples(
                item1, item2, textdistance.jaro_winkler))
            ratcliff_dist.append(self.apply_distance_metric_on_tuples(
                item1, item2, textdistance.ratcliff_obershelp))
        return self.get_greatest_average_similarity([ratcliff_dist, jaro_winkler_dist, fuzzy_token_dist, levenshtein_dist])

    def generate_test_results_for_group(self, group):
        test_results = {}
        for q, mark in group.all_grades.items():
            max_mark_for_q = self.questions[q]
            if q in group.query_errors.keys():
                test_results[q] = self.create_similarity_results_as_tests(q, mark, max_mark_for_q,
                                                                          group.all_similarity_scores[q],
                                                                          group.incorrect_query_details[q],
                                                                          error_messages=group.query_errors[q])
            else:
                test_results[q] = self.create_similarity_results_as_tests(q, mark, max_mark_for_q,
                                                                          group.all_similarity_scores[q],
                                                                          group.incorrect_query_details[q])
            group.incorrect_query_details.pop(q)
        group.results_as_test_cases = test_results

    def create_similarity_results_as_tests(self, question_number, student_marks, max_marks, ratio, incorrect_answers_details,
                                           error_messages=None):
        """
        Create a set of unit test results that can be outputted as a json to be processed by UAM.
        @param question_number: The question/query number being processed
        @param student_marks: grades for student's query
        @param max_marks: max grades for query
        @param ratio: Similarity score between student answer and correct solution as a ratio.
        @param incorrect_answers_details: Details of the mistakes within a student query
        @param error_messages: Any error message produced by running the student's query
        @return: A dictionary with unit test results for the given query number.
        """
        student_passes = int(student_marks)
        student_fails = max_marks - student_passes
        results = {}
        passes = {}
        failures = {}
        errors = {}
        percentage_earned_per_pass = round(100 / max_marks, 2)
        for i in range(1, student_passes + 1):
            percentage_earned = i * percentage_earned_per_pass if i < max_marks else 100
            key_string = self.get_query_correctness_as_string(
                question_number, percentage_earned)
            value_string = 'Query: {0:} -- Answer is {1:.2f}% correct.'.format(
                question_number, percentage_earned)
            passes[key_string] = value_string
        if student_fails > 0:
            failures, errors = self.get_failed_test_cases(student_passes, max_marks,
                                                          percentage_earned_per_pass, incorrect_answers_details,
                                                          ratio, question_number, failures, errors, error_messages)
        if passes:
            results['passes'] = passes
        if failures:
            results['failures'] = failures
        if errors:
            results['errors'] = errors
        return results

    def get_query_correctness_as_string(self, question_number, percentage):
        return 'Query: ' + question_number + ' -- ' + str(percentage) + '% correctness: '

    # def get_failed_test_cases(self, student_passes, max_marks, percent_per_pass, incorrect_answers_details,
    #                           ratio, question_number, failures, errors, error_messages=None):
    #     for i in range(student_passes + 1, max_marks + 1):
    #         failure_dict = {}
    #         dict_to_use = failures if not error_messages else errors
    #         percentage_earned = i * percent_per_pass if i < max_marks else 100
    #         key_string = self.get_query_correctness_as_string(
    #             question_number, percentage_earned)
    #         description_string = 'Query: {0:} -- Answer is not {1:.2f}% correct.'.format(question_number,
    #                                                                                      percentage_earned)
    #         message_string = 'Results produced by query {0:} are {1:.2f}% different from correct solutions.'.format(
    #             question_number, 100 * (1 - ratio))
    #         if i >= max_marks:
    #             details_string = 'Columns Required:\n\t' + \
    #                 str(incorrect_answers_details['requiredcols'][:len(incorrect_answers_details['requiredcols'])%100]) + "\n\t... ..."
    #             details_string += '\nColumns Provided:\n\t{}'.format(
    #                 str(incorrect_answers_details['studentcols'][:len(incorrect_answers_details['studentcols'])%100])) + "\n\t... ..."
    #             details_string += '\nNote: If provided columns are correct but named differently, marks were not deducted.'
    #             details_string += '\nRows missing from student results:\n\t' + incorrect_answers_details[
    #                 'missingRows'][:len(incorrect_answers_details[
    #                 'missingRows'])%100] + "\n\t... ..."
    #             details_string += '\nExtra rows in student results:\n\t' + \
    #                 incorrect_answers_details['extraRows'][:len(incorrect_answers_details['extraRows'])%100] + "\n\t... ..."
    #         else:
    #             details_string = 'Details below...'
    #         failure_dict['description'] = description_string
    #         failure_dict['message'] = message_string
    #         if error_messages and i >= max_marks:
    #             details_string = '\nError messages produced by query:\n' + \
    #                 str(error_messages)
    #         failure_dict['details'] = details_string
    #         dict_to_use[key_string] = failure_dict
    #     return failures, errors
