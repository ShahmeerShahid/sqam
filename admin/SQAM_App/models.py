from django.db import models


class Job(models.Model):
    assignment_name = models.CharField(max_length=50)
    max_marks = models.IntegerField()
    create_tables = models.FileField(upload_to='uploads/%Y/%m/%d/')
    load_data = models.FileField(upload_to='uploads/%Y/%m/%d/')
    # TODO Add Required Fields

    class Meta:
        ordering = ['id']


class MarkusJob(Job):
    markus_setting1 = models.CharField(max_length=50)
    markus_setting2 = models.CharField(max_length=50)
    # TODO Add Required Fields

    class Meta:
        ordering = ['id']
