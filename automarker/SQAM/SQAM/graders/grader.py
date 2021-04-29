from abc import ABC, abstractmethod
from SQAM.graders.my_utils import sort_list_of_tuples
import SQAM.settings as settings


class Grader(ABC):
    def __init__(self, questions, solution_results):
        self.questions = questions
        self.solution_results = solution_results

    @abstractmethod
    def grade_question_for_group(self, group_results, solution_results, max_marks):
        """
        @param group's results: result tuples returned by running group's SQL query
        @param solution's results: result tuples returned by running solution's SQL query
        @param max_marks: maximum marks for given query
        @return: marks earned by group as float
        """
        pass

    def grade_question(self, submission_results, submission_error, solution_results_lst, max_marks):
        if len(solution_results_lst) == 0:
            return (0, 0, 0) if(submission_error) else (max_marks, 1, 0)
        grades = []
        similarity_scores = []
        for solution_result in solution_results_lst:
            grade, similarity = self.grade_question_for_group(
                submission_results,
                solution_result,
                max_marks)
            grades.append(grade)
            similarity_scores.append(similarity)
        if len(grades) == 0:
            return 0, 0, 0
        final_grade = settings.DETERMINE_GRADE(grades)
        max_grade_idx = grades.index(max(grades))
        final_similarity = settings.DETERMINE_GRADE(similarity_scores)
        return final_grade, final_similarity,max_grade_idx

    def grade_group(self, group, all_questions):
        for q in self.questions.keys():
            group.all_grades[q], group.all_similarity_scores[q],max_grade_idx = self.grade_question(
                group.query_results[q],
                group.query_errors[q],
                self.solution_results[q],
                self.questions[q])
            group.incorrect_query_details[q] = self.get_incorrect_answer_details(
                group.query_results[q],
                self.solution_results[q], max_grade_idx)
            group.extra_columns_per_query[q] = self.get_num_extra_columns(
                group.query_results[q],
                self.solution_results[q], max_grade_idx)
            group.submission_graded = True
            if q in all_questions:
                all_questions[q].append(group.all_grades[q]) 
            else:
                all_questions[q] = [group.all_grades[q]]
            group.total_grade += group.all_grades[q]
            group.query_results.pop(q)

    def get_num_extra_columns(self, answer, solutions,max_grade_idx):
        solutions_length = len(solutions[max_grade_idx][0]) if len(solutions) > 0 else 0
        if answer and not answer[0] == ('', ''):
            return len(answer[0]) - solutions_length
        return -solutions_length

    def get_incorrect_answer_details(self, answer, solutions,max_grade_idx):
        missingRows = 'All rows missing'
        extraRows = 'None'
        studentcols = 'None'
        requiredcols = 'None'
        if len(solutions) > 0 and answer:
            missingRows = self.getMissingRows(answer[1:], solutions[max_grade_idx][1:])
            extraRows = self.getExtraRows(answer[1:], solutions[max_grade_idx][1:])
            requiredcols = solutions[max_grade_idx][0]
            if answer[0] != ('', ''):
                studentcols = answer[0]

        return {'missingRows': missingRows,
                'requiredcols': requiredcols,
                'extraRows': extraRows,
                'studentcols': studentcols}

    def getExtraRows(self, answer, solutions):
        ans_set = set(sort_list_of_tuples(answer))
        sol_set = set(sort_list_of_tuples(solutions))
        diff = ans_set.difference(sol_set)
        if not diff or diff == {('', '')}:
            return 'None'
        return str(diff)

    def getMissingRows(self, answer, solutions):
        ans_set = set(sort_list_of_tuples(answer))
        sol_set = set(sort_list_of_tuples(solutions))
        diff = sol_set.difference(ans_set)
        if not diff or diff == {('', '')}:
            return 'None'
        return str(diff)



    #Binary marker setting

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

    def get_failed_test_cases(self, student_passes, max_marks, percent_per_pass, incorrect_answers_details,
                                ratio, question_number, failures, errors, error_messages=None):
        for i in range(student_passes + 1, max_marks + 1):
            failure_dict = {}
            dict_to_use = failures if not error_messages else errors
            percentage_earned = i * percent_per_pass if i < max_marks else 100
            key_string = self.get_query_correctness_as_string(
                question_number, percentage_earned)
            description_string = 'Query: {0:} -- Answer is not {1:.2f}% correct.'.format(question_number,
                                                                                            percentage_earned)
            message_string = 'Results produced by query {0:} are {1:.2f}% different from correct solutions.'.format(
                question_number, 100 * (1 - ratio))
            if i >= max_marks:
                if(settings.LIMIT_OF_CHARACTER!=0):
                    details_string = 'Columns Required:\n\t' + \
                        str(incorrect_answers_details['requiredcols'][:min(len(incorrect_answers_details[
                        'requiredcols']),int(settings.LIMIT_OF_CHARACTER))]) + "\n\t... ..."
                    details_string += '\nColumns Provided:\n\t{}'.format(
                        str(incorrect_answers_details['studentcols'][:min(len(incorrect_answers_details[
                        'studentcols']),int(settings.LIMIT_OF_CHARACTER))])) + "\n\t... ..."
                    details_string += '\nNote: If provided columns are correct but named differently, marks were not deducted.'
                    details_string += '\nRows missing from student results:\n\t' + incorrect_answers_details[
                        'missingRows'][:min(len(incorrect_answers_details[
                        'missingRows']),int(settings.LIMIT_OF_CHARACTER))] + "\n\t... ..."
                    details_string += '\nExtra rows in student results:\n\t' + \
                        incorrect_answers_details['extraRows'][:min(len(incorrect_answers_details[
                        'extraRows']),int(settings.LIMIT_OF_CHARACTER))] + "\n\t... ..."
                else:
                    details_string = 'Columns Required:\n\t' + \
                        str(incorrect_answers_details['requiredcols']) 
                    details_string += '\nColumns Provided:\n\t{}'.format(
                        str(incorrect_answers_details['studentcols'])) 
                    details_string += '\nNote: If provided columns are correct but named differently, marks were not deducted.'
                    details_string += '\nRows missing from student results:\n\t' + incorrect_answers_details[
                        'missingRows'] 
                    details_string += '\nExtra rows in student results:\n\t' + \
                        incorrect_answers_details['extraRows'] 
            else:
                details_string = 'Details below...'
            failure_dict['description'] = description_string
            failure_dict['message'] = message_string
            if error_messages and i >= max_marks:
                details_string = '\nError messages produced by query:\n' + \
                    str(error_messages)
            failure_dict['details'] = details_string
            dict_to_use[key_string] = failure_dict
        return failures, errors
