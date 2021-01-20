import json
import os
import sys
import re

import jinja2

DEFAULT_TEMPLATE_TYPE = 'txt'
DEFAULT_AGGREGATE_TEMPLATE = 'aggregated.tpl'
DEFAULT_JINJA_EXTENSIONS = ['jinja2.ext.do']

def student_list(students, format_str, fields):
    ''' (list of dict, str, list of str) -> list of str
    Prepares a list of student fields from a list of dictionaries representing
    aggregator.UTSCStudents in the given format with respective fields.
    '''

    return [format_str % tuple([student.get(field) for field in fields])
            for student in students]


def get_all_counts(results, select):
    ''' (dict of dicts) -> int
    Gets either the number of passes, failures, errors, and total of the given
    results dictionary depending on select (from the respective order) for all
    TestCases.
    '''

    return sum([get_counts(test, select) for test in results.values()])


def get_counts(results, select):
    ''' (dict of dicts/lists) -> int
    Gets either the number of passes, failures, errors, or total of the given
    results dictionary depending on select (from the respective order) for a
    single TestCase.
    '''
    select = ['passes', 'failures', 'errors', 'total'][select]

    return (len(results.get(select)) if results.get(select) else 0
            if select != 'total' else
            sum([get_counts(results, i) for i in range(3)]))


def ljust(text, amount, offset_after_first=0):
    ''' (str, int) -> str
    Pads the given text with spaces on the left until its length is the given
    amount -- in otherwords, right justify text. Every line after the first
    will be offset to the left offsetAfterFirst spaces.
    '''
    return '\n'.join([line.ljust(amount + int(bool(i)) * offset_after_first)
                      for i, line in enumerate(text.split('\n'))])


def to_gf_names(name):
    ''' (str) -> str
    Converts all non-compliant (non alphanumeric including the underscore)
    characters to underscores for usage as a grade name in a standard .gf
    file as specified by:

    http://www.cdf.toronto.edu/~clarke/grade/fileformat.shtml
    '''
    return re.sub('[^A-Za-z0-9_]', '_', name)


def exclude(collection, exclusions):
    ''' (list, list) -> list
    Removes any elements that contain exclusions from collection.
    '''
    return [element for element in collection if all(exclusion not in element for exclusion in exclusions)]

def passed(test_name, results):
    ''' (str, dict{str:dict{str: dict{str: str or dict}}}) -> int
    Returns 1 if the test testName is a key in any TestCase's passes dict
    in results and 0 otherwise.
    '''
    # flatten passes from all TestCases
    passes = set()
    for test_case in results.values():
        passes.update((test_case.get('passes') or {}).keys())
    return int(test_name in passes)


def get_balanced_weight(tests):
    '''(list) -> int

    Calculate the equally weighted value of an individual test from a
    collection of tests.

    '''
    return round(1 / len(tests) * 100, 3)




class TemplatedReport:
    '''Parent class.'''

    def __init__(self,
                 report,
                 template_file,
                 template_dir,
                 env=None,
                 plugins=None,
                 jinja_extns=None,
                 origin=None):
        '''
        ({str: dict}, str, jinja-env, [function], jijna-extns, str) -> NoneType
        '''

        self._report = report

        if plugins is None:
            plugins = []

        self._template = _load_template(env, template_dir,
                                        template_file, plugins, jinja_extns)

        if origin:
            self._report['origin'] = origin  # inject origin

    def write(self, target):
        ''' (TemplatedReport, str) -> NoneType
        Write this TemplatedReport out to the file specified by target.
        '''

        if self._template:
            try:
                with open(target, 'w') as trgt:
                    trgt.write(str(self))
            except IOError as err:
                print('Cannot generate report %s: %s' % (target, err))
        else:
            print('Warning: cannot generate report %s. ' % target +
                  'No template file.',
                  file=sys.stderr)

    def __str__(self):
        ''' (TemplatedReport) -> str
        Render this TemplatedReport.
        '''

        return self._template.render(result=self._report)


class IndividualReport(TemplatedReport):
    ''' A human readable, templated, individual report of test results.
    '''

    def __init__(self,
                 report,
                 template_file,
                 template_dir,
                 env=None,
                 plugins=None,
                 jinja_extns=None,
                 origin=None):
        '''
        ({str: dict}, jinja-env, [function], str, jijna-extns, str) -> NoneType
        '''

        TemplatedReport.__init__(self, report, template_file, template_dir,
                                 env, plugins, jinja_extns, origin)

    def write(self, target):
        ''' (IndividualReport, str) -> NoneType
        Writes this IndividualReport out to the file specified by target.
        '''

        TemplatedReport.write(self,
                              os.path.join(self._report['origin'], target))

    @staticmethod
    def from_json(source_json,
                  template_file,
                  template_dir,
                  plugins=None,
                  jinja_extns=None,
                  origin=None):
        '''Produces an IndividualReport from the given individual JSON file at
        source_json using the given template_file.

        '''

        try:
            with open(source_json) as source:
                json_report = json.loads(source.read())
        except IOError as err:
            print("Cannot open individual json file. %s" % err,
                  file=sys.stderr)
            raise err

        return IndividualReport(json_report,
                                template_file,
                                template_dir,
                                None,
                                plugins,
                                jinja_extns,
                                origin)


class AggregateReport(TemplatedReport):
    '''A human readable, templated, aggregated report (of all test
    results for all student submissions).

    '''

    def __init__(self,
                 report_dict,
                 template_file,
                 template_dir,
                 plugins=None,
                 jinja_extns=None):
        '''
        ({str: {str: dict}}, [function], str, jinja-extns) -> NoneType
        '''

        TemplatedReport.__init__(self, report_dict, template_file,
                                 template_dir, None, plugins, jinja_extns,
                                 None)

    @staticmethod
    def from_json(source_json,
                  template_file,
                  template_dir,
                  plugins=None,
                  jinja_extns=None):
        '''Produces an AggregatelReport from the given aggregate JSON file at
        source_json using the given template_file.

        '''
        try:
            with open(source_json) as source:
                json_report = json.loads(source.read())
        except IOError as err:
            print("Cannot open individual json file. %s" % err,
                  file=sys.stderr)
            raise err

        return AggregateReport(json_report,
                               template_file,
                               template_dir,
                               plugins,
                               jinja_extns)


class IndividualReports:
    '''A collection of human readable, templated, individual reports (of
    test results).

    '''

    def __init__(self,
                 report_dict,
                 template_file,
                 template_dir,
                 plugins=None,
                 jinja_extns=None):
        '''
        ({str: {str: dict}}, [function], str, jinja-extns) -> NoneType
        '''

        self._individual_reports = {
            report['origin']: IndividualReport(report,
                                               template_file,
                                               template_dir,
                                               None,
                                               plugins,
                                               jinja_extns,
                                               None)
            for report in report_dict['results']}

    def write(self, target):
        ''' (IndividualReports, str) -> NoneType
        Write all individual reports to target.
        '''

        for report in self._individual_reports.values():
            report.write(target)

    @staticmethod
    def from_json(source_json,
                  template_file,
                  template_dir,
                  plugins=None,
                  jinja_extns=None):
        '''Produces IndividualReports from the given aggregate JSON file at
        source_json using the given template_file.

        '''

        try:
            with open(source_json) as source:
                json_report = json.loads(source.read())
        except IOError as err:
            print("Cannot open  json file. %s" % err, file=sys.stderr)
            raise err

        return IndividualReports(json_report,
                                 template_file,
                                 template_dir,
                                 plugins,
                                 jinja_extns)


def _load_template(env, template_dir, template_file, plugins, jinja_extns):
    if env is None:
        env = _set_up_jinja_env(template_dir, plugins, jinja_extns)
    try:
        return env.get_template(template_file)
    except jinja2.TemplateNotFound:
        return None


def _set_up_jinja_env(template_dir, plugins, jinja_exts):
    env = jinja2.Environment(
        loader=jinja2.FileSystemLoader(template_dir),
        extensions=jinja_exts)

    # populate jinja environment with our custom filters
    for plugin in plugins:
        env.filters[plugin.__name__.lower()] = plugin

    return env

def aggregate_report_SQAM(source, template_dir, report_name):
    '''Template an aggregate report given an aggregate json.'''
    
    PLUGINS = [student_list, get_all_counts, get_counts, ljust,
            to_gf_names, exclude, passed, get_balanced_weight]
    template_file=os.path.join(DEFAULT_TEMPLATE_TYPE, DEFAULT_AGGREGATE_TEMPLATE)
    OUTPUT = '%s.%s' % (report_name, DEFAULT_TEMPLATE_TYPE)
    report = AggregateReport.from_json(source,
                                       template_file,
                                       template_dir,
                                       PLUGINS,
                                       DEFAULT_JINJA_EXTENSIONS)
    report.write(OUTPUT)

def individual_reports(source, plugins, template_file,
                       template_dir, jinja_extns, output):
    '''Template all individual reports given an aggregate json.'''

    reports = IndividualReports.from_json(source,
                                          template_file,
                                          template_dir,
                                          plugins,
                                          jinja_extns)
    reports.write(output)
