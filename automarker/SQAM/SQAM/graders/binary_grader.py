from SQAM.graders.grader import Grader
import SQAM.settings as settings

"""
Binary Grader is a grader which marks questions as full marks or no marks.
Answers must exactly match the solution in order to get full marks. 
"""
class Binary_Grader(Grader):

    def grade_question_for_group(self, group_results, solution_results, max_marks):
        if group_results == solution_results:
            return max_marks, 1.0
        elif not settings.CHECK_COLUMN_NAMES and group_results[1:] == solution_results[1:]:
            return max_marks, 1.0
        return 0, 0
