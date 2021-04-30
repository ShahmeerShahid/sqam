import re

"""
This file is used to extract the results from the soluton File.
It is similar to submission.py but supports multiple solutions.
"""


def get_solution_results(path_to_solution, query_names, querier):
    queries = extract_all_queries(path_to_solution, query_names)
    return get_solutions(querier, queries)


def extract_all_queries(path_to_file, query_names):
    all_queries = {}
    with open(path_to_file, "r") as fd:
        file = fd.read()
        for query in query_names:
            query_regex = re.compile(
                r"[-+\s]+START {}[\s\S]*?[-+\s]+END {}".format(
                    query, query)
            )
            matches = query_regex.findall(file)
            all_queries[query] = matches
    return all_queries


def get_solutions(querier, all_queries):
    solutions = {}
    errors = None
    for q_num, queries in all_queries.items():
        solutions[q_num] = []
        for query in queries:
            result, error = querier.run_multi_query(query)
            solutions[q_num].append(result)
            if error:
                errors = error
    return solutions, errors
