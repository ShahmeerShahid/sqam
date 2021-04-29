import os

RABBITMQ_HOST = os.environ.get("RABBITMQ_HOST", "rabbitmq")
# RABBITMQ_HOST = "localhost"
RABBITMQ_USERNAME = os.environ.get("RABBITMQ_USERNAME", "guest")
RABBIT_MQ_PASSWORD = os.environ.get("RABBITMQ_PASSWORD", "guest")
JSON_RESULT_FILENAME = "result.json"
# TEMPLATE_PATH must be an absolute path
# TEMPLATES_PATH = "/mnt/c/Users/Sandy Wang/sqam/automarker/SQAM/SQAM/result_formating/templates"
TEMPLATES_PATH = "/automarker/SQAM/SQAM/result_formating/templates"

# =============== MYSQL_Creds =====================
MYSQL_db_user_name = os.getenv("MYSQL_USER", "automarkercsc499")
MYSQL_db_password = os.getenv("MYSQL_PASSWORD", "csc499")
MYSQL_root_username = "root"
MYSQL_root_password = os.getenv("MYPASSWD")

MYSQL_db_host = "mysqlam"
MYSQL_db_port = 3306
MYSQL_LOGIN_DETAILS = (
    MYSQL_db_user_name,
    MYSQL_db_password,
    MYSQL_root_username,
    MYSQL_root_password,
    MYSQL_db_host,
    MYSQL_db_port,
)

# =============== PostgreSQL ===================
POSTGRESQL_db_username = os.getenv("POSTGRES_USER", "automarkercsc499")
POSTGRESQL_db_password = os.getenv("POSTGRES_PASSWORD", "csc499")
POSTGRESQL_db_port = 5432
POSTGRESQL_db_host = "postgres"
# POSTGRESQL_db_host = "localhost"
# POSTGRESQL_db_password = '123456'
# POSTGRESQL_db_username = 'postgres'
# Currently, PostgreSQL is only be able to set the superuser's username and password.
# Later hotfix will add the feature that create a new user only can access the current task database.

# ============= GRADING SETTINGS ==============
DETERMINE_GRADE = max  # Function to determine grade from grades of multiple solutions

# max is the bulitin python function which returns the max from a given list
#Binary Marker setting
CHECK_COLUMN_NAMES = False
#The maximum characters of the output string for each type of the information in report for each question.
LIMIT_OF_CHARACTER = 0