from django.test import TestCase
from SQAM_App.models import MarkusJob


class MarkUsJobObjectCreationTest(TestCase):
    def setUp(self):
        MarkusJob.objects.create(assignment_name="CSC343 A1", max_marks=100,
                                 markus_setting1="Setting1", markus_setting2="Setting2")

    def test_data(self):
        job1 = MarkusJob.objects.get(assignment_name="CSC343 A1")
        self.assertEqual(job1.assignment_name, "CSC343 A1")
