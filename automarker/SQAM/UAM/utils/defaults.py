from SQAM.config import PATH_TO_UAM
import os

DEFAULT_TEMPLATE_TYPE = 'txt'
DEFAULT_AGGREGATE_TEMPLATE = 'aggregated.tpl'
DEFAULT_INDIVIDUAL_TEMPLATE = 'individual.tpl'
DEFAULT_JINJA_EXTENSIONS = ['jinja2.ext.do']
DEFAULT_TEMPLATE_DIR = os.path.join(PATH_TO_UAM, 'templates')
DEFAULT_REPORT_NAME = 'report'
DEFAULT_IN_JSON_FILE = 'result.json'
DEFAULT_OUT_JSON_FILE = 'aggregated.json'

DEFAULT_TIMEOUT = 2
DEFAULT_VERBOSITY = 2
