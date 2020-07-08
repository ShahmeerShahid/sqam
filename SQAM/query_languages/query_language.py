from abc import ABC, abstractmethod
class QueryLanguage(ABC):
    def __init__(self, username, password, database_name, autocommit=True):
        self.username = username
        self.password = password
        self.database_name = database_name
        self.autocommit = autocommit

    @abstractmethod
    def run_single_query(self, query, verbose=None):
        """
        Run a single SQL query and return the results and errors generated.
        @param query: SQL query to run
        @param verbose: level of printing for debugging
        @return: results(list or None) and errors(string or None) produced by query
        """
        pass

    @abstractmethod
    def run_multi_query(self, queries, verbose=None):
        """
        Run a set of related SQL queries and return the results and errors generated.
        @param queries: SQL queries to run
        @param verbose: level of printing for debugging
        @return: results(list or None) and errors(string or None) produced by the queries.
        """
        pass

    @abstractmethod
    def run_SQL_file(self, sql_file, query_names, verbose=None):
        """
        Run all SQL queries in given file and return the results and errors generated for each query.
        @param sql_file: File containing multiple SQL queries
        @param query_names: Name/Number given to each query
        @param verbose: level of printing for debugging
        @return: two dictionaries results and errors containing, for each query,
                 query names as keys, and results and errors as values respectively
        """
        pass