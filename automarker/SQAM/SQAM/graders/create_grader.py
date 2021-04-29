from SQAM.graders.string_similarity_grader import String_Similarity_Grader
from SQAM.graders.binary_grader import Binary_Grader

def create_grader(grader_type, questions, solution_results):
    grader = None
    if grader_type == "partial":
        grader = String_Similarity_Grader(
            questions, solution_results, grader_type)
    elif grader_type == "binary":
        grader = Binary_Grader(questions, solution_results)
    assert grader, f"Grader {grader_type} not supported"
    return grader
