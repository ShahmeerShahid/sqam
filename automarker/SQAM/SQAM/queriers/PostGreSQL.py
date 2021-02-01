import psycopg2
from SQAM.queriers.querier import Querier
import time

class PostGreSQLQuerier(Querier):
    def refreshDB(self):
        # self.call_procedure('drop_all_tables')
        self.executeScript(self.create_tables_path)
        self.executeEntireScript(self.create_function_path)
        self.executeEntireScript(self.create_tables_path)
        self.reloadData(self.load_data_path)
        time.sleep(10)
    def initialize(self):
        pass
    def get_cursor(self):
        """
        Create and return a connector & cursor for connecting to mysql database
        """
        cnx = psycopg2.connect(
            user=self.username, password=self.password, database=self.database_name,
            host=self.host, port=self.port
        )
        cursor = cnx.cursor()
        return cursor, cnx

    def close_cursor_connection(self, cursor, connect):
        """
        Close cursor and connection
        """
        cursor.close()
        connect.close()

    def run_single_query(self, query, verbose=None):
        """
        Run a single pSQL query and return the results and errors generated.
        @param query: pSQL query to run
        @param verbose: level of printing for debugging
        @return: results and errors produced by query
        """
        c, cnx = self.get_cursor()
        results, error = None, None
        try:
            c.execute(query)
            num_rows = c.rowcount
            if num_rows and num_rows>0:
                rows = c.fetchall()
                field_names = tuple([i[0] for i in c.description])
                results = [field_names] + rows
            else:
                results = [("", ""), ("", "")]
                if verbose and verbose>1:
                    print(f'Zero rows produced by query:\n\t{query}')
        except Exception as e:
            error = e
            if verbose and verbose > 1:
                print('--'*25,f'\nError Caught:\n\tError Message: {e}\n\tQuery: {query}\n','--'*25)
        self.close_cursor_connection(c, cnx)
        return results, error

    def run_multi_query(self, queries, verbose=None):
        """
        Run a set of related pSQL queries and return the results and errors generated.
        @param queries: pSQL queries to run
        @param verbose: level of printing for debugging
        @return: results and errors produced by query
        """
        c, cnx = self.get_cursor()
        results, error = None, None
        for sub_query in queries.split(';'):
            try:
                c.execute(sub_query)
                if c.rowcount > 0:
                    rows = c.fetchall()
                    field_names = tuple([i[0] for i in c.description])
                    results = results + rows if results else [field_names] + rows
                if 'create' in sub_query.lower() or 'drop' in sub_query.lower():
                    cnx.commit()
            except Exception as e:
                if not 'query was empty' in str(e).lower():
                    error = error +'\n'+ str(e) if error else str(e)
                    if verbose and verbose > 1:
                        print('--'*25,f'\nError Caught:\n\tError Message: {e}\n\tQuery: {sub_query}\n','--'*25)
        if not results:
            results = [("", ""), ("", "")]
        self.close_cursor_connection(c, cnx)
        return results, error

    def run_SQL_file(self, sql_file, query_names, verbose=None):
        """
        Run all pSQL queries in given file and return the results and errors generated for each query.
        @param sql_file: File containing multiple pSQL queries
        @param query_names: Name/Number given to each query
        @param verbose: level of printing for debugging
        @return: two dictionaries results and errors containing, for each query,
                 query names as keys, and results and errors as values respectively
        """
        c, cnx = self.get_cursor()
        results, error = {}, {}
        # Execute every command from the input file
        try:
            for i, query in enumerate(sql_file.split(';')):
                if query=="":
                    continue
                c.execute(query)
                key = query_names[i]
                if verbose and verbose > 1:
                    print("\n\nQuestion {}: {}".format(query_names[i], query))
                if c.rowcount > 0:
                    rows = c.fetchall()
                    field_names = tuple([i[0] for i in c.description])
                    rows = [field_names] + rows
                    results[key] = rows
                else:
                    error[key] = [("", ""), ("", "")]
                    print('\nResults for question: {} returned 0 lines.\n'.format(query_names[i]))
        except Exception as e:
            print(f'\nThe solutions file contains an error: {e}\n')
            self.close_cursor_connection(c, cnx)
            print('Please fix errors in solutions.\nExitting now...')
            exit(1)
        self.close_cursor_connection(c, cnx)
        return results, error

    def call_procedure(self, proc_name):
        c, cnx = self.get_cursor()
        c.callproc(proc_name, args=())
        self.close_cursor_connection(c, cnx)

    def reloadData(self, filename):
        fd = open(filename, 'r')
        sqlFile = fd.read()
        fd.close()
        c, cnx = self.get_cursor()
        try:
            c.execute(sqlFile, multi=True)
        except Exception as msg:
            print("Command skipped: ", msg)
        self.close_cursor_connection(c, cnx)

    def executeScript(self, filename):
        """
        Runs and collects of put of the pSQL script with given filename
        @param filename: path to script
        @return: The output collected for given script
        """
        fd = open(filename, 'r')
        sqlFile = fd.read()
        fd.close()
        result = []
        # all pSQL commands split on ';'
        sqlCommands = sqlFile.split(';')
        c, cnx = self.get_cursor()
        # Execute every command from the input file
        for command in sqlCommands:
            # This will skip and report errors
            # For example, if the tables do not yet exist, this will skip over
            # the DROP TABLE commands
            try:
                c.execute(command)
                num_rows = c.rowcount
                if num_rows and num_rows > 0:
                    result = c.fetchall()
            except Exception as msg:
                print("Command skipped: ", msg)
        self.close_cursor_connection(c, cnx)
        return result

    def executeEntireScript(self, filename):
        """
        Runs and collects output of the pSQL script with given filename
        @param filename: path to script
        @return: The output collected for given script
        """
        fd = open(filename, 'r')
        sqlFile = fd.read()
        fd.close()
        result = []
        c, cnx = self.get_cursor()
        # Execute every command from the input file
        # This will skip and report errors
        # For example, if the tables do not yet exist, this will skip over
        # the DROP TABLE commands
        try:
            c.execute(sqlFile)
            num_rows = c.rowcount
            if num_rows and num_rows > 0:
                result = c.fetchall()
        except Exception as msg:
            print("Command skipped: ", msg)
        self.close_cursor_connection(c, cnx)
        return result