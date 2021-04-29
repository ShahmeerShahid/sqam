import SQAM.settings
from SQAM.queriers.MySQL import MySQLQuerier
from SQAM.queriers.PostGreSQL import PostGreSQLQuerier


def create_querier(db_type, db_name, init_file):
    querier = None
    if db_type == "mysql":
        login_details = SQAM.settings.MYSQL_LOGIN_DETAILS
        querier = MySQLQuerier(*login_details, db_name, init_file)
    elif db_type == "postgresql":
        querier = PostGreSQLQuerier(db_name, init_file)
    assert querier, f"Querier {db_type} not supported"
    return querier
