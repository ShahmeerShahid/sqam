from abc import ABC, abstractmethod


class Querier(ABC):
    """
    Querier is an abstract class that defines the structure a Querier must follow.
    """

    def __init__(self, database_name, init_path, autocommit=True):
        self.database_name = database_name
        self.init_path = init_path
        self.autocommit = autocommit
        self.setup()

    @abstractmethod
    def setup(self):
        """
        Setup is responsible for creating the database and loading the init.sql
         file into the database. 
        """
        pass

    @abstractmethod
    def remove_database(self):
        """
        Is responsible for removing the database from the DBMS
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
