
import datetime
import json
import os

class TestReport:
    def __init__(self, assignment_name, path_to_submissions, file_name):
        self.name = assignment_name
        self.date = datetime.datetime.now().isoformat().split('.')[0]
        self.results = []

        submission_paths = [ item for item in os.listdir(path_to_submissions) if os.path.isdir(os.path.join(path_to_submissions, item)) ]
        for name in submission_paths:            
            with open(os.path.join(path_to_submissions, name, file_name)) as json_path:
                test_result = json.loads(json_path.read())
        
            test_result['date'] = self.date
            test_result['assignment'] = assignment_name
            self.results.append(test_result)

    def to_json(self):
        return json.dumps({'results': self.results, 'name': self.name, 'date': self.date})

def Aggregate_SQAM(assignment, path_to_submissions,file_name, output_file_name):
    TEST_REPORT = TestReport(assignment, path_to_submissions, file_name).to_json()
    
    with open(output_file_name, 'w') as report:
        report.write('%s\n' % TEST_REPORT)
