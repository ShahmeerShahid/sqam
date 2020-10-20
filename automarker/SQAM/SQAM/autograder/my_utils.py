from operator import itemgetter
import math
import os
import re
from SQAM.config import SUBMISSIONS

def sort_list_of_tuples_as_strings(lst):
    ret = []
    try:
        lst.sort(key=itemgetter(0))
    except Exception as e:
        pass
    for tup in lst:
        tup = map_function_to_tuple(str, tup)
        tup = sorted(tup)
        ret.append(tup)
    ret.sort(key=itemgetter(0))
    return ret


def sort_list_of_tuples(lst):
    ret = []
    try:
        lst.sort(key=itemgetter(0))
    except Exception as e:
        pass
    for tup in lst:
        tup = map_function_to_tuple(str, tup)
        tup = sorted(tup)
        ret.append(tuple(tup))
    ret.sort(key=itemgetter(0))
    return ret


def map_function_to_tuple(func, tup):
    new_tuple = ()
    for itup in tup:
        new_tuple += (func(itup),)
    return new_tuple


def round_half_up(n, decimals=0):
    multiplier = 10 ** decimals
    return int(math.floor(n * multiplier + 0.5) / multiplier)

def getAllAnnotations():
    """
    Collect the path to all annotation files as a list
    @return: List of paths to annotation files
    """
    files = []
    path = SUBMISSIONS
    # r=root, d=directories, f = files
    for r, d, f in os.walk(path):
        for file in f:
            if 'report.txt' in file:
                files.append(os.path.join(r, file))
    return files

def replace_in_line(original_txt, new_txt, filein, fileout, line_num):
    i=0
    with open(filein) as fin, open(fileout, 'w') as fout:
        for line in fin:
            lineout = line
            if i==line_num:
                lineout = line.replace(original_txt, new_txt)
            fout.write(lineout)

def getGroupNumber(str):
    """
    Return the group number contained in the string
    """
    groupNumRegex = re.compile(r'.*/(?P<groupNum>.*)/.*')
    match = groupNumRegex.search(str)
    groupNum = match.group('groupNum')
    return groupNum

def move_annotations_to_annotations_folder():
    import shutil
    if not os.path.exists('Annotations'):
        os.makedirs('Annotations')
    all_annotation_files = getAllAnnotations()
    for file in all_annotation_files:
        group_num = getGroupNumber(file)
        new_file = file.replace('report.txt', str(group_num)+'.txt')
        shutil.move(new_file, "Annotations/"+str(group_num)+'.txt')
        os.remove(file)