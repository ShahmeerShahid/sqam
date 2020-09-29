from abc import ABC, abstractmethod
from SQAM.autograder.my_utils import *

class Grader(ABC):
    def __init__(self, assignment, query_language):
        self.assignment = assignment
        self.query_language = query_language

    @abstractmethod
    def grade_question_for_group(self, group_results, solution_results, max_marks):
        """
        @param group's results: result tuples returned by running group's SQL query
        @param solution's results: result tuples returned by running solution's SQL query
        @param max_marks: maximum marks for given query
        @return: marks earned by group as float
        """
        pass

    def grade_group(self, group):
        if group.results_collected:
            for q in self.assignment.questions.keys():
                group.all_grades[q], group.all_similarity_scores[q] = self.grade_question_for_group(
                            group.query_results[q], self.assignment.solution_results[q], self.assignment.questions[q])
                group.incorrect_query_details[q] = self.get_incorrect_answer_details(group.query_results[q],
                                                                                 self.assignment.solution_results[q])
                group.extra_columns_per_query[q] = self.get_number_of_extra_columns_in_student_answer(group.query_results[q],
                                                                                 self.assignment.solution_results[q])
                group.submission_graded = True
                group.total_grade +=group.all_grades[q]
        else:
            return False
        return True

    def grade_all_student_groups(self):
        for i, group in enumerate(self.assignment.class_list):
            self.grade_group(group)
            print(f'Finished Grading {i} groups -- Group {group.group_id} mark: {group.total_grade}')

    def get_number_of_extra_columns_in_student_answer(self, answer, solutions):
        number_extra = len(answer[0]) - len(solutions[0]) if answer and not answer[0] == ('', '') else -len(solutions[0])
        return number_extra

    def get_incorrect_answer_details(self, answer, solutions):
        return {'missingRows': self.getMissingRows(answer[1:], solutions[1:]),
                'requiredcols': solutions[0],
                'extraRows': self.getExtraRows(answer[1:], solutions[1:]),
                'studentcols': answer[0] if not answer[0] == ('', '') else 'None'} if answer else \
                {'missingRows': 'All rows missing.',
                'requiredcols': solutions[0],
                'extraRows': 'None',
                'studentcols': 'None'}

    def getExtraRows(self, answer, solutions):
        answer1 = sort_list_of_tuples(answer)
        solutions1 = sort_list_of_tuples(solutions)
        ans_set = set(answer1)
        sol_set = set(solutions1)
        diff = ans_set.difference(sol_set)
        if not diff or diff == {('', '')}:
            return 'None'
        else:
            return str(diff)

    def getMissingRows(self, answer, solutions):
        answer1 = sort_list_of_tuples(answer)
        solutions1 = sort_list_of_tuples(solutions)
        ans_set = set(answer1)
        sol_set = set(solutions1)
        diff = sol_set.difference(ans_set)
        if not diff or diff == {('', '')}:
            return 'None'
        else:
            return str(diff)