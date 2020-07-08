class QueryRunner:
    def __init__(self, assignment, query_language):
        self.assignment = assignment
        self.class_list = self.assignment.class_list
        self.query_language = query_language
        self.assignment.solution_results, self.assignment.solution_errors = self.get_solution_results(
            self.assignment.path_to_solutions, list(self.assignment.questions.keys()))

    def get_solution_results(self, solutions_file, query_names, verbose=0):
        """
        Runs and collects the solutions provided by the professor
        @param verbose: level of printing for debugging
        @return: The result collected for each query as a dictionary with query number as key and query results as values.
        """
        # Open and read the file as a single buffer
        with open(solutions_file, 'r') as fd:
            sqlFile = fd.read()
            all_solutions, errors = self.query_language.run_SQL_file(sqlFile, query_names, verbose)
        return all_solutions, errors

    def get_results_for_student_group(self, group):
        if group.queries_extracted:
            for q_num, query in group.queries.items():
                if query:
                    result, error = self.query_language.run_multi_query(query) if query.count(';') > 1 \
                            else self.query_language.run_single_query(query)
                    if error:
                        group.query_errors[q_num] = error
                    group.query_results[q_num] = result
                else:
                    group.query_results[q_num] = [("", ""), ("", "")]
            group.results_collected = True
        else:
            return False
        return True

    def get_results_for_all_student_groups(self):
        for group in self.class_list:
            if not self.get_results_for_student_group(group):
                print(f'Student Group {group.group_id} queries not found.')