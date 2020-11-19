require("dotenv").config();
const router = require("express").Router();
const { param, body, validationResult } = require("express-validator");
const axios = require("axios");
const fs = require("fs");
let { Task, TaskSchema } = require("../models/task.model");
const constants = require("../constants");

const automarker = process.env.AUTOMARKER_URL || "localhost";
const config = { headers: { "Content-Type": "application/json" } };

/*	GET /tasks/
	  @params: none 
    @return: a list of all tasks
    ON SUCCESS: 200
    ON FAILURE: 500
*/

router.route("/").get((req, res) => {
  Task.find()
    .then((tasks) => {
      return res.status(200).json(tasks);
    })
    .catch((e) => {
      return res.sendStatus(
        e.response && e.response.status ? e.response.status : 500
      );
    });
});

/*	GET /tasks/:tid
	  @params: tid 
    @return: a task
    ON SUCCESS: 200
    ON FAILURE: 404, 500
*/

router.route("/:tid").get([param("tid").isInt({ min: 0 })], (req, res) => {
  Task.findOne({ tid: req.params.tid })
    .then((task) => {
      if (task === null) {
        return res.sendStatus(404);
      }
      return res.status(200).json(task);
    })
    .catch((e) => {
      return res.sendStatus(
        e.response && e.response.status ? e.response.status : 500
      );
    });
});

/*	POST /tasks/
	  @params: connector, name, [status, submissions, extra_fields] 
    @return: 
    ON SUCCESS: 201
    ON FAILURE: 400, 500
*/

router
  .route("/")
  .post(
    [
      body("connector").isIn(constants.connectors),
      body("name").notEmpty(),
      body("status").optional().isIn(constants.statuses),
      body("tid").isEmpty(),
      body("_id").isEmpty(),
    ],
    (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const newTask = new Task(req.body);

      try {
        newTask.save().then(async (task) => {
          const dir = `/var/downloads/${task.tid}`;
          if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir);
          }
          //await axios.post(`${connector}/tasks`, task);
          newTask.status = "Downloading";
          newTask.save().then((updatedTask) => {
            return res.status(201).json(updatedTask);
          });
        });
      } catch (e) {
        return res.sendStatus(
          e.response && e.response.status ? e.response.status : 500
        );
      }
    }
  );

/*	PATCH /tasks/:tid
    @params: status, name, submissions, num_submissions, extra_fields
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/

router
  .route("/:tid")
  .patch(
    [
      param("tid").isInt({ min: 0 }),
      body("status").optional().isIn(constants.statuses),
      body("name").optional().notEmpty(),
      body("submissions").optional().isArray({ min: 0 }),
      body("num_submissions").optional().isInt({ min: 0 }),
      body("extra_fields").optional().notEmpty(),
    ],
    (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const tid = req.params.tid;
      let update = {};
      const restrictedKeys = ["tid", "logs", "connector", "_id"];

      Object.keys(TaskSchema.obj).forEach((key) => {
        if (restrictedKeys.includes(key)) return;
        //TODO: handle the case where they pass in logs!
        else if (key === "submissions" && req.body[key]) {
          const submissions = req.body.submissions.map((name) => {
            return {
              name: name,
            };
          });
          update.submissions = submissions;
        } else if (req.body[key]) update[key] = req.body[key];
      });

      Task.findOneAndUpdate({ tid: tid }, update, function (err, doc) {
        if (doc === null) {
          return res.sendStatus(404);
        } else if (err) {
          return res.sendStatus(500);
        } else {
          res.status(200).json({
            message: `Task ${tid} successfully updated`,
          });
        }
      });
    }
  );

/*	PATCH /tasks/status/:tid

    This endpoint will be removed in the future!

	  @params: status, [num_submissions]
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/

router
  .route("/status/:tid")
  .patch(
    [
      param("tid").isInt({ min: 0 }),
      body("status").isIn(constants.statuses),
      body("num_submissions").optional().isInt({ min: 0 }),
    ],
    async (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const tid = req.params.tid;
      let status = req.body.status;
      let update = {
        status: status,
      };

      const mockTask = {
        assignment_name: "A2",
        using_windows_system: false,
        sqamv3_path: "/automarker/SQAM",
        create_tables: "./Demo/Winter_2020/createTable.sql",
        create_trigger: "./Demo/Winter_2020/createTrigger.sql",
        create_function: "./Demo/Winter_2020/createFunction.sql",
        load_data: "./Demo/Winter_2020/loadData.sql",
        solutions: "./Demo/Winter_2020/solutions_winter_2020.sql",
        submissions: "./Demo/Submissions/",
        submission_file_name: "queries.sql",
        json_output_filename: "result.json",
        lecture_section: "LEC101",
        timeout: 100,
        max_marks: 80,
        max_marks_per_question: [
          3,
          4,
          3,
          3,
          4,
          4,
          2,
          2,
          4,
          5,
          3,
          4,
          4,
          4,
          3,
          5,
          6,
          7,
        ],
        question_names: [
          "Q1",
          "Q2",
          "Q3.A",
          "Q3.B",
          "Q3.C",
          "Q4.A",
          "Q4.B",
          "Q4.C",
          "Q5.A",
          "Q5.B",
          "Q6.A",
          "Q6.B",
          "Q6.C",
          "Q7.A",
          "Q7.B",
          "Q8",
          "Q9",
          "Q10",
        ],
        db_autocommit: true,
        db_user_name: "automarkercsc499",
        db_password: "csc499",
        db_name: "c499",
        db_host: "mysql",
        db_port: 3306,
        db_type: "mysql",
        marking_type: "partial",
      };
      try {
        const task = await Task.findOne({ tid: tid });
        if (status === "Downloaded") {
          if (req.body.num_submissions)
            update.num_submissions = req.body.num_submissions;
          const response = await axios.post(
            `http://${automarker}/runJob`,
            mockTask,
            config
          );
          status = response.status === 200 ? "Marking" : "Error";
          update.status = status;
        }
        await Task.findOneAndUpdate({ tid: tid }, update);
        return res.status(200).json({
          message: `Task ${tid} successfully updated to status ${status}`,
        });
      } catch (e) {
        console.log(e);
        return res.sendStatus(
          e.response && e.response.status ? e.response.status : 500
        );
      }
    }
  );

module.exports = router;
