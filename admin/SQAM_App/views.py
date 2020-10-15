from SQAM_App.models import MarkusJob
from django.views.generic import CreateView, ListView
from django.urls import reverse_lazy

class MarkusJobView(CreateView):
    template_name = 'SQAM_App/MarkusJob_Create.html'
    model = MarkusJob
    fields = '__all__'
    success_url = reverse_lazy('markusjob-list')

class MarkusJobListView(ListView):
    template_name = 'SQAM_App/MarkusJob_List.html'
    queryset = MarkusJob.objects.all()
    context_object_name = 'jobs'
    paginate_by = 10
