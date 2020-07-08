from SQAM.students_and_groups.students import StudentList
from SQAM.students_and_groups.groups import StudentGroup

class ClassList:
    def __init__(self, path_to_submissions, groups_list_file, student_list_file):
        self._groups = {}
        self._students = StudentList(student_list_file)
        with open(groups_list_file, 'r') as file:
            for group_info in file.readlines():
                group = StudentGroup.make_group_from_group_file(path_to_submissions,group_info, self._students)
                self._groups[group.group_id] = group

    def get_student(self, student_id):
        '''
        Return a Student with the given student_id, or None if no such
        Student.
        '''
        return self._students.get(student_id)

    def add_student(self, student):
        '''
        Add a given Student. Complain on stderr yet proceed if a Student
        with student's student_id already exists.
        '''
        self._students.add(student)

    def add_group(self, group):
        '''Add a given Group. Complain on stderr yet proceed if a Group with
        group's group_id already exists.
        '''
        if group.group_id in self._groups:
            print('Warning: %s is an existing group. ' +
                  'Adding/updating its students.' % group.group_id)
        self._groups[group.group_id] = group

    def get_group(self, group_id):
        '''Return a Group with group_id, or None if it doesn't exist.
        '''
        return self._groups.get(group_id)

    def __iter__(self):
        return iter(self._groups.values())

    def __str__(self):
        return '\n'.join(str(grp) for grp in self._groups.values())