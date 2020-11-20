import sys
import os
import csv
import random
from pathlib import Path
from typing import List


def studentCSVlist(submissions: List[str]) -> List[str]:
    output = []
    for index, name in enumerate(submissions):
        # student0,student0,studentlast0,24800,email
        output.append(f"student{index},student{index},studentlast{index},{index},{index},email\n")

    return output

def groupTXTlist(submissions: List[str]) -> List[str]:
    output = []
    for index, name in enumerate(submissions):
        # group_0003,group_0003,student0
        output.append(f"{name},{name},student{index}\n")
    return output

def dirsNamesTXTlist(submissions: List[str], path_to_submissions_folder) -> List[str]:
    output = []
    for index, name in enumerate(submissions):
        # ./Demo/Submissions/group_0003,group_0003
        output.append(f"{path_to_submissions_folder}/{name},{name}\n")
    return output



def startPatch(path_to_submissions):
    """
        Creates the required dumby files for the automarker to function without student and group names.
        Files created: dirs_and_names.txt, groups.txt, students.csv
        Arguments:
            inputPath : Path where the student submissions are. e.g -> (PATHSUBMISSION)
            outputPath : Path where the new generated files need to be placed e.g -> (PATHOUTPUT)

    """
  
    submissions = [ item for item in os.listdir(path_to_submissions) if os.path.isdir(os.path.join(path_to_submissions, item)) ]

    print("list of folders",submissions)

    CSVlist = studentCSVlist(submissions)
    with open(f"{path_to_submissions}/students.csv", "w") as f:
        f.writelines(CSVlist)

    TXTlist = groupTXTlist(submissions)
    with open(f"{path_to_submissions}/groups.txt", "w") as f:
        f.writelines(TXTlist)
    
    dirslist = dirsNamesTXTlist(submissions, path_to_submissions)
    with open(f"{path_to_submissions}/dirs_and_names.txt", "w") as f:
        f.writelines(dirslist)
        