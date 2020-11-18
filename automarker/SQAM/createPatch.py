import sys
import os
import csv
import random

PATHOUTPUT = "./Demo/Student_Information_and_Submissions/"
PATHSUBMISSION = "./Demo/Submissions/"

def createFiles(path):
    fd = open(path + "dirs_and_names.txt", "w")
    fg = open(path + "groups.txt", "w")
    fs = open(path + "students.csv", "w")
    return fd, fg, fs

if __name__ == "__main__":
    submissions, outputPath, inputPath = "", "", ""
    fd, fg, fs = None, None, None
    if len(sys.argv) == 1:
        outputPath = PATHOUTPUT
        inputPath = PATHSUBMISSION
    elif len(sys.argv) == 3:
        outputPath = sys.argv[1]
        inputPath = sys.argv[2]
        
    fd, fg, fs = createFiles(outputPath)
    submissions = os.listdir(inputPath)
    writer = csv.writer(fs)

    for count, name in enumerate(submissions):
        fd.write("./Demo/Submissions/{},{}\n".format(name, name))
        fg.write("{},{},student{}\n".format(name, name, count))
        writer.writerow(["studentID{}".format(count), "studentName{}".format(count), "studentlast{}".format(count), "{}{}{}{}{}".format(random.randrange(10), random.randrange(10), random.randrange(10), count, count), "email"])
    
    fd.close()
    fg.close() 
    fs.close()