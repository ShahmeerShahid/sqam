from django.urls import path
from SQAM_App.views import MarkusJobView, MarkusJobListView

urlpatterns = [
    path('newjob', MarkusJobView.as_view(), name='markusjob-create'),
    path('', MarkusJobListView.as_view(), name='markusjob-list'),
]

