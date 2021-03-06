# SQAM

The SQL (DDL) Automarker (aka SQAM).

It is a framework for testing SQL assignments,
collecting the results, and exporting them for easy viewing using
templates.

We welcome collaboration and contributions!

## Requirements:

You need Docker 3.0 or above.

## Steps For Running With Docker

To run the automarker you need to do:
1. docker-compose build
2. docker-compose up

When you are developing the automarker you can find it useful to only run the parts of the system you require. In most cases for testing the automarker you only need docker-compose up automarker postgres rabbitmq

The automarker system uses rabbitMQ to recieve Tasks to complete. These messages are send by the admin API and you can find the code that accepts the RabbitMQ messages in main.py. When developing the automarker you do not need to use the admin_api to send messages. You can directly send messages using the rabbitMQ web interface(default url is localhost:15672 The port, password, and usernmae are set in docker-compose.yml). From the web interface login, navigate to the queue tab. If the task_to_mark queue does not exist than click add queue (use default settings). After selecting the task_to_mark queue navigate to Publish Message and provide a JSON object in the Payload feild. 

Here is an example:

### Example RabbitMQ Messge:

```json
{
"tid": 0,
"assignment_name": "A2",
"init":"/automarker/SQAM/Demo_Postgres/init.sql",
"solutions":"/automarker/SQAM/Demo_Postgres/solutions.sql",
"submissions": "/automarker/SQAM/Demo_Postgres/Submissions",
"submission_file_name": "a2.sql",
"max_marks": 50,
"max_marks_per_question": [5,5,5,5,5,5,5,5,5,5],
"question_names": ["Query 1","Query 2","Query 3","Query 4","Query 5","Query 6","Query 7","Query 8","Query 9","Query 10"],
"db_type": "postgresql",
"marking_type": "partial"
}
```

The above json payload uses the Postgres DEMO files that you can find in Demo_Postgres.

### RabbitMQ Message Feilds:
 - tid : Task ID Used by the admin_api to keep track of tasks. When submitting your own task manually you should be save picking any nonnegative number( Note you can not have two tasks of the same tid running at the same time)
 - assignment_name : String that represents the name of the assignment. Used in the reports generated by the automarker
 - init : File path to the init.sql file. init.sql should include the create table and load data queries. 
 - solutions: File path to the solution File. 
 - submissions: File Path to the Folder that contains all submissions. 
 - Submission_file_name: The name of the sql file that studnets submitted. Used to find the submission for each group within the submissions folder. 
 - max_marks: The total of all marks on the assignment. 
 - max_marks_per_question: List of marks for each question.
 - question_names: names of each query. Note this is important. It is used in the regex to extract queries. 
 - db_type: The database used. Example postgres, mysql
 - marking_type: Type of grader to use. Example: binary, partial
 - refresh_level: Level of refreshness of the database. per_assignment means once for all submissions. per_submission means to refresh between every submission, per_query means refresh between every question. Note refresh_level is optional, by default uses per_submission. 

# Overview of How the Automarker works

## Rundown of Program Flow
1. main.py gets a message from the "task_to_mark" queue. 
2. main.py creates a Task to represent the information given in the rabbitMQ message. Currently the only Task is an assignment.
3. In assignment.py you can see what happens in the __init__ class. 
    - Creates Querier
    - Creates Grader 
    - Runs get_solution_results. This gets the results of all the solution queries. You can checkout how it works in solution.py.
    - submissions are created using create_submissions(). It gets submission names from the folder names located in the submission folder provided. 
4. Run() is executed in main.py
    - run begins the marking process.
    - For an assignment task it Calls mark_submissions()
5. Mark_submissions loops through all submissions calling submission.grade_submission() found in submission.py. 
6. Grade Submission.py then:
    - Runs extract_all_queries. Extract_all_queries uses regex to extract the queries from the submission. It uses the Question_name to match correctly.
    - Runs get_results_for_submission, which uses the querier to get the results of each query for the submission. For more details on queries look at the querier Folder. 
    - Runs grade_group(). This uses the grader to generate marks for a submission. Look at the grader folder for more details.
    - Runs generate_test_results_for_group(). THis uses the grader to generate feedback for a submission. Look at the grader folder for more details. 
    - Runs dump_json_output_to_submission_folder() this creates a json file in the submission folder. 
7.  runs run_aggreator() and run_templator() found in results_formating folder. This generates the formated results files.
8. Lastly it removes the database that was used for grading.
9. Finished! 

## Graders

Graders are responsible for providing marks and feedback for queries. grader.py describes what all graders need to have. A grader needs to be able to grade_question() which provides a numerical mark for a given query. Currently the feedback is generated within grader.py but can be overwritten by implementing generate_test_results_for_group(). Currently there is a binary grader and a string_similarity_grader which provides partial marks. If you wish to add another grader you must inherent grader.py and add the grader to create_grader.py.  

### Steps of similarity Grader

1. If student_results == solution_results then give them 100
2. If we dont care about column names then check if student_rows == solution_rows give them 100
3. Grade Using String Similarity
    - GetDistance
        - Sort student_results and solution_results
        - for item1, item2 in zip(student_results, solution_results): <--- problem zip uses length of smaller one
            - Levenshtein.seqratio(student_row, solution_row)
            - for item1, item2 in zip(newtup1, newtup2): <---- Same zip problem
                - fuzz.token_sort_ratio(student_row_item, solution_row_item)
            - for item1, item2 in zip(newtup1, newtup2):
                - textdistance.jaro_winkler(student_row_item, solution_row_item)
            - for item1, item2 in zip(newtup1, newtup2):
                - textdistance.ratcliff_obershelp(student_row_item, solution_row_item)
        - If binary Mode then take the mean of all similarity scores
        - If Partial Mode then take the max of all similarity scores
    - Caculate grade using similarity score <--- Is Rounded
    - If binary Mode then give students either 100 if the similarity score is > 0.95 and 0 otherwise
    - If Partial Mode then give students 100 if simiarity score is > 0.90 and partial marks otherwise

## Queriers

Queriers are responsible for the connection to the database. The querier standard is defined in querier.py. All queriers need to implement setup(), remove_database(), run_multi_query(). Currently their is a mysql querier and a postgres querier. The postgres querier is the most stable and was used for 2021 CSC343. The postgres querier uses sqlparse to split up the queries extracted. An improvement to the queriers would be to use sqlparse to extract queries for all queriers and implement run_single_query instread of multiple query. The MySQL querier was inherented from the previous automarker team, it has not been thoroughly tested. I would recommend thorough testing before using to mark an assignment. 

## Tasks
Tasks are responsible for using the queriers and graders. Tasks are described in task.py. Currently there is an assignment task which is described in assignment.py. Assignment.py also uses submission.py to describe assignment submissions. In the future when adding PCRS support you need to implement a task. 

## result formatting
  Result formatting is built off of the UAM project. It contains the aggreator and templator used in assignment.py. If the results are put into a database, then the result_formatting folder can be removed from the repo.  

## settings.py
  All configuration settings for the automarker should be defined in this file. This file should be the one stop shop for all configuration settings making it easier to configure the automarker. 

## requirements.txt 
  All python requirements are listed here. If you add more python requirements you must rebuild the container. `docker build automarker` 

## Dockerfile
  The dockerfile describes the build process for the docker container

## Testing Files
  The repo contains two sets of Demo files. SQAM/DEMO contains files for a MYSQL assignment. The files in this repo must be updated before running on the latest automarker. The solution and student submissions must be changed updated to have the START END format. You can use the SQAM/reformat.sh file as an example of how to update the file format. SQAM/reformat.sh can be removed from the repo after updating the mysql demo files. Additonally the other sql files should be combined into a single init.sql file. SQAM/Demo_Postgres contains a postgres assignment which can be used to test the postgres querier. Any configurations settings that you need for a querier should be set in settings.py. 

## Solution Format
  The solution file can contain multiple answers for each question. If multiple solutions appear in the solution file they will all be used when caculating grades. If a professor wants to give 100% for any submission that does not error they do not put an answer in the solution file. Do not put START Query_name END Query_name. You simply do not put anything in the file.

## Assignment Format
  Refer to Demo_Postgres/Submissions to see the format assignments should be in.


# Future Work

 - Change the message from RabbitMQ to use a dictionary field "questions": {"Query 1": 5, "Query 2": 5} instead of having two list feilds. max_marks_per_question: [5, 5] question_names: ["Query 1", "Query 2"]
 - Put marking results into a database instead of using files. After marking is finished we could directly send the results to a database. This could be done in the graders.  
    - Phase out the requirement for max_marks in the task message from rabbitMQ
    - After switching to a database for results we can also phase out the following functions in assignment.py run_templator, run_aggreator, get_average. 

- Refactor the Graders found in the grader folder. This code has lots of room for improvement. 
    - Create marking test cases to ensure the grading is correct
    - Refactor the string_similarity_grader. Problems:
        - Does not work well with answers of different lengths. look at the use of zip(item1, item2) in the grader. zip only interates for the length of the smallest list so the grader does not know if the student submission had extra of too little rows. 
    
    - Improve the Memory ussage
      - Refactor the automarker to mark one question at a time. Currently all questions for a submission are marked together. This would require some work restructuring the graders and changing how submission.py uses the grader. 

      - Refactor the graders to stop directly using submission attributes as well as use more local variables. If you look at submission.py refresh_dic function you will see that a bunch of attributes have to be reset in order to free the memory. All of these attributes should really just be local variables in the grader. 

      - I believe it would be better if a grader was treated more like a comparer. A grader should take two submissions and return if they are the same / their similarity. All of the details of a submission should remain in submission.py and not be included in a grader. This would also make it easier to implement a PCRS task. 

    - Support PCRS. This requires creating a new Task class. You would need to be able to accept the Task in main.py as well. Refer to the Tasks section to see how a Task works.

    - Note that the automarker was not robustly tested for concurrency correctness. Testing should be done to confirm that the automarker works correctly when multiple tasks are running at the same time. 
  
   - Integrate User Authenticaion Work. Work in this has been completed but has not be merged into the master branch because the security team at uoft should be connected for advise on how to do authentication.

   - Implement CI/CD.

   - Provide configuration settings for each question. For example it would be nice if professors could select wheather to mark using column name spelling or not. This should be a settings per question instead of a setting in settings.py
