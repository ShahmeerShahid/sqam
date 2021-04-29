from SQAM.assignment import Assignment
from SQAM.queriers.create_querier import create_querier
import SQAM.settings
from SQAM.publish import log_message


class Task:
    """ 
    A Class used to represent an Automarking Job

    Attributes:
    assignment : Representation of Assignment that will be marked
    querier: SQL Language the Assignment uses
    grader: Either Partial or Binary. Used to convert query results into grades
    """

    def __init__(self, channel, config):
        """
        Creates all required attributes based off the provided 
        configuration information. 

        Precondition: Provided configuration information contains all required keys
        """
        self.channel = channel
        self.tid = config["tid"]

        db_name = "t"+str(self.tid)
        querier = create_querier(config["db_type"], db_name, config["init"])
        self.assignment = Assignment(config["assignment_name"],
                                     config["submissions"],
                                     config["solutions"],
                                     config["submission_file_name"],
                                     {q_num: max_grade for q_num, max_grade in zip(
                                         config["question_names"], config["max_marks_per_question"])},
                                     config["max_marks"],
                                     querier,
                                     config.get("refresh_level",
                                                "per_submission"),
                                     config["marking_type"])

    async def run(self):
        """
        Runs an automarking job on an entire assignment
        Creates reports in each submission repo as well as aggregates results into
        one json file. 
        """
        try:
            await log_message(self.tid, "Beginning Assignment Marking", self.channel)
        except:
            print("Beginning Assignment Marking")
        self.assignment.mark_submissions()
        try:
            await log_message(self.tid, "Graded all Submissions", self.channel)
        except:    
            print("Graded all Submissions")
        try:
            await log_message(self.tid, "Generating all Result Files", self.channel)
        except:
            print("Generating all Result Files")
        self.assignment.run_aggregator()
        self.assignment.run_templator()
        try:
            await log_message(self.tid, "Assignment Marking Complete", self.channel)
        except:
            print("Assignment Marking Complete")
        self.assignment.querier.remove_database()
        print(
            f'Done Grading Submissions. Class Average: {self.assignment.get_average()*100:.2f}%')
