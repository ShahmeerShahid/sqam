import psycopg2
import SQAM.settings
from SQAM.queriers.querier import Querier
import sqlparse

"""
Querier for postgresql
"""
class PostGreSQLQuerier(Querier):
    def remove_database(self):
        cursor, cnx = self.get_admin_cursor()
        cnx.autocommit = True
        cursor.execute(f"DROP DATABASE IF EXISTS {self.database_name}")
        self.close_cursor_connection(cursor, cnx)

    def setup(self):
        # Create DB
        cursor, cnx = self.get_admin_cursor()
        cnx.autocommit = True
        print("Started to (re)initialized the database")
        cursor.execute(f'DROP SCHEMA IF EXISTS public CASCADE')
        cursor.execute(f'CREATE SCHEMA public')
        cursor.execute(f'SET search_path TO public')
        cursor.execute(f"DROP DATABASE IF EXISTS {self.database_name} WITH (FORCE)")
        cursor.execute(f"CREATE DATABASE {self.database_name}")
        cursor.execute('''CREATE PROCEDURE _DropTableTriggers()
                            AS
                            $$
                            DECLARE
                                _rec    RECORD;
                            BEGIN
                                FOR _rec IN
                                    SELECT  DISTINCT    event_object_table, trigger_name
                                    FROM    INFORMATION_SCHEMA.triggers
                                LOOP
                                    EXECUTE 'DROP TRIGGER ' || _rec.trigger_name || ' ON ' || _rec.event_object_table || ';';
                                END LOOP;
                            END
                            $$ LANGUAGE plpgsql SECURITY DEFINER''')
        self.close_cursor_connection(cursor, cnx)
        # Load init.sql
        cursor, cnx = self.get_cursor()
        cnx.autocommit = True
        with open(self.init_path, "r") as f:
            cursor.execute(f.read())
        self.close_cursor_connection(cursor, cnx)
        print("Initialization is done!")
    def get_admin_cursor(self):
        """
        Create and return a connector & cursor for connecting to PostgreSQL with root privilege
        """
        cnx = psycopg2.connect(
            user=SQAM.settings.POSTGRESQL_db_username,
            password=SQAM.settings.POSTGRESQL_db_password,
            host=SQAM.settings.POSTGRESQL_db_host,
            port=SQAM.settings.POSTGRESQL_db_port,
        )
        cursor = cnx.cursor()
        return cursor, cnx

    def get_cursor(self):
        """
        Create and return a connector & cursor for connecting to PostgreSQL database
        """
        cnx = psycopg2.connect(
            user=SQAM.settings.POSTGRESQL_db_username,
            password=SQAM.settings.POSTGRESQL_db_password,
            host=SQAM.settings.POSTGRESQL_db_host,
            port=SQAM.settings.POSTGRESQL_db_port,
        )
        cursor = cnx.cursor()
        return cursor, cnx

    def close_cursor_connection(self, cursor, connect):
        """
        Close cursor and connection
        """
        cursor.close()
        connect.close()

    def run_multi_query(self, queries, verbose=None):
        """
        Run a set of related pSQL queries and return the results and errors generated.
        @param queries: pSQL queries to run
        @param verbose: level of printing for debugging
        @return: results and errors produced by query
        """
        c, cnx = self.get_cursor()
        results, error = None, None
        for sub_query in sqlparse.split(queries):
            sub_query = sqlparse.format(sub_query, strip_comments=True, reindent=True)
            if not sub_query:
                break
            try:
                c.execute(sub_query)
                if c.rowcount > 0 and "create" not in sub_query.lower():
                    rows = c.fetchall()
                    field_names = tuple([i[0] for i in c.description])
                    results = [field_names] + rows
                # if "create" in sub_query.lower() or "drop" in sub_query.lower():
                #     cnx.commit()
            except Exception as e:
                print("ERROR ", e)
                if not "query was empty" in str(e).lower():
                    error = error + "\n" + str(e) if error else str(e)
                    if verbose and verbose > 1:
                        print(
                            "--" * 25,
                            f"\nError Caught:\n\tError Message: {e}\n\tQuery: {sub_query}\n",
                            "--" * 25,
                        )
        if not results:
            results = [("", ""), ("", "")]
        self.close_cursor_connection(c, cnx)
        return results, error
