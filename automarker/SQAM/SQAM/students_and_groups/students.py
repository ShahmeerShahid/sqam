from SQAM.config_singleton import Config
# from SQAM.config import MAX_MARKS
import csv
import io
import sys

class Student:
    '''A representation of a student.
        '''

    @staticmethod
    def make_student_from_bb(bb_line):
        '''
        Instantiate a return a Student from a line in BlackBoard generated
        csv student file:
            student_id,first,last,student_number,email
        '''

        dct = zip(['student_id', 'first', 'last', 'student_number', 'email'],
                  next(csv.reader(io.StringIO(bb_line))))
        return Student(**dict(dct))

    def __init__(self, group_number=None, **kwargs):
        '''
        Instantiate this Student from given fields.
        '''
        self.student_id = kwargs.get('student_id') if kwargs else 'None'
        self.first = kwargs.get('first') if kwargs else ''
        self.last = kwargs.get('last') if kwargs else ''
        self.student_number = kwargs.get('student_number') if kwargs else 'None'
        self.email = kwargs.get('email') if kwargs else 'None'
        self.name = self.first + ' ' + self.last
        self.group_number = group_number
        self.group = None
        self.grade = 0.0

    def __str__(self):
        return ','.join([self.student_id, self.first, self.last, self.email])

    def student_as_dict(self):
        keys = ['student_id', 'first', 'last', 'student_number', 'email']
        vals = [self.student_id, self.first, self.last, self.student_number,self.email]
        return {k: v for k,v in zip(keys,vals)}

    @property
    def student_number(self):
        return self.__student_number

    @student_number.setter
    def student_number(self, student_num):
        self.__student_number = student_num.zfill(10)

    @property
    def group(self):
        return self.__group

    @group.setter
    def group(self, new_group):
        self.__group = new_group

    @property
    def grade(self):
        return self.__grade

    @grade.setter
    def grade(self, student_grade):
        config = Config.get_instance()
        self.__grade = 0 if student_grade<0 else student_grade
        self.__grade = config.vars["max_marks"] if student_grade>config.vars["max_marks"] else student_grade

class StudentList:
    @staticmethod
    def generate_student_list(path_to_students_csv):
        """
        Given student_file (a path to a file in the format:
            student_id,first,last,student_number,email),
        load all Students in this file into this ClassList.
        Raises FileNotFoundError is student_file cannot be opened.
        """
        students = {}
        with open(path_to_students_csv) as students_file:
            for line in students_file:
                try:
                    student = Student.make_student_from_bb(line)
                    students[student.student_id] = student
                except ValueError:
                    print('Warning: could not create Student from %s' % line,
                          file=sys.stderr)
                    continue
        return students

    def __init__(self, student_file):
        self._students = {}
        if student_file is not None:
            self._students = self.generate_student_list(student_file)

    def get(self, student_id):
        '''
        Return a Student with the given student_id, or None if no such
        Student.
        '''
        return self._students.get(student_id)

    def add(self, student):
        '''
        Add a given Student. Complain on stderr yet proceed if a Student
        with student's student_id already exists.
        '''
        if student.student_id in self._students.keys():
            print('Warning: %s is an existing student. ' +
                  'Adding/updating its record.' % student.student_id)
        self._students[student.student_id] = student

    def __iter__(self):
        return iter(self._students)

    def __str__(self):
        return '\n'.join([str(student) for student in self._students.values()])


