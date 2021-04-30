from SQAM.graders.string_similarity_grader import String_Similarity_Grader
from SQAM.graders.binary_grader import Binary_Grader

"""
create_grader is used by tasks to create a Grader. This keeps all code
for a grader within the graders folder. 
"""


def create_grader(grader_type, questions, solution_results):
    grader = None
    if grader_type == "partial":
        grader = String_Similarity_Grader(
            questions, solution_results, grader_type)
    elif grader_type == "binary":
        grader = Binary_Grader(questions, solution_results)
    assert grader, f"Grader {grader_type} not supported"
    return grader
