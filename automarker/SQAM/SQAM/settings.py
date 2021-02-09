import os

PORT = os.getenv('PORT', '9005')

JSON_RESULT_FILENAME = "result.json"
TEMPLATES_PATH = "/automarker/SQAM/SQAM/result_formating/templates"
QUERY_EXTRACTOR_RE = lambda section : r'[-+\s]+{}[-+\s]+[^;]+;'.format(section)

# =============== MYSQL_Creds =====================
MYSQL_db_user_name= os.getenv('MYSQL_USER', 'automarkercsc499')
MYSQL_db_password = os.getenv('MYSQL_PASSWORD', 'csc499')
MYSQL_root_username= "root"
MYSQL_root_password = os.getenv("MYPASSWD")

MYSQL_db_host = "mysqlam"
MYSQL_db_port = 3306
MYSQL_LOGIN_DETAILS = (MYSQL_db_user_name, MYSQL_db_password,MYSQL_root_username,MYSQL_root_password, MYSQL_db_host, MYSQL_db_port)

#=============== PostgreSQL ===================
POSTGRESQL_db_username = os.getenv('POSTGRES_USER', 'automarkercsc499')
POSTGRESQL_db_password = os.getenv('POSTGRES_PASSWORD', 'csc499')
POSTGRESQL_db_host = "postgres"
POSTGRESQL_db_port = 5432
#Currently, PostgreSQL is only be able to set the superuser's username and password. 
#Later hotfix will add the feature that create a new user only can access the current task database.
POSTGRESQL_LOGIN_DETAILS =(POSTGRESQL_db_username, POSTGRESQL_db_password,POSTGRESQL_db_username, POSTGRESQL_db_password, POSTGRESQL_db_host, POSTGRESQL_db_port)