from django.db import models

class Job(models.Model):
    Assignment_Name = models.CharField(max_length=50)
    Max_Marks = models.IntegerField()
    Create_Tables = models.FileField(upload_to='uploads/%Y/%m/%d/')
    Load_Data = models.FileField(upload_to='uploads/%Y/%m/%d/')
    # TODO Add Required Feilds
    
    class Meta:
        ordering = ['id']

class MarkusJob(Job):
    MarkusSetting1 = models.CharField(max_length=50)
    MarkUsSetting2 = models.CharField(max_length=50)
    # TODO Add Required Feilds

    class Meta:
        ordering = ['id']


