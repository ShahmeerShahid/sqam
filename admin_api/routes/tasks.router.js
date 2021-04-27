require("dotenv").config();
const router = require("express").Router();
const { param, body, validationResult } = require("express-validator");
const axios = require("axios");
const fs = require("fs");
let { Task, TaskSchema, Log } = require("../models/task.model");
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
      const connector = req.body.connector;
      try {
        newTask.save().then(async (task) => {
          const dir = `/var/downloads/${task.tid}`;
          if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir);
          }
          fs.writeFileSync(`${dir}/aggregated.json`, JSON.stringify({}));
          const body = {
            tid: task.tid,
            download_directory: dir,
            markus_URL: task.extra_fields.markus_URL,
            assignment_id: task.extra_fields.assignment_id,
            api_key: task.extra_fields.api_key,
          };

          await axios.post(`http://${connector}/tasks`, body);
          newTask.status = "Downloading";
          newTask.initFile = `/var/downloads/${task.tid}/init.sql`;
          newTask.solutions = `/var/downloads/${task.tid}/solutions.sql`;
          newTask.save().then((updatedTask) => {
            return res.status(201).json(updatedTask);
          });
        });
      } catch (e) {
        newTask.status = "Error";
        newTask.save();
        return res
          .status(e.response && e.response.status ? e.response.status : 500)
          .json({
            message:
              e.response && e.response.message
                ? e.response.message
                : "Internal Server Error",
          });
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

/*	PUT /tasks/:tid/logs
    @params: logs
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/
router
  .route("/:tid/logs")
  .put(
    [
      param("tid").isInt({ min: 0 }),
      body("logs").isArray(),
      body("source").optional().isIn(constants.logSources),
    ],
    (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const tid = req.params.tid;
      const logsToAppend = [];
      for (line in req.body.logs) {
        logsToAppend.push(
          new Log({
            timestamp: new Date(),
            text: req.body.logs[line],
            source: req.body.source,
          })
        );
      }

      Task.findOneAndUpdate(
        { tid: tid },
        { $push: { logs: { $each: logsToAppend } } },
        function (err, doc) {
          if (doc === null) {
            res.sendStatus(404);
          } else if (err) {
            console.log(err);
            res.sendStatus(500);
          } else {
            res.status(200).json({
              message: `Task ${tid} logs successfully updated`,
            });
          }
        }
      );
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
      try {
        const task = await Task.findOne({ tid: tid });
        if (!task) {
          return res.sendStatus(404);
        }
        if (status === "Downloaded") {
          if (req.body.num_submissions)
            update.num_submissions = req.body.num_submissions;
          const body = {
            tid: tid,
            assignment_name: task.name,
            max_marks: task.max_marks,
            max_marks_per_question: task.max_marks_per_question,
            marking_type: task.marking_type,
            question_names: task.question_names,
            submission_file_name: task.submission_file_name,
            init: task.initFile,
            solutions: task.solutions,
            submissions: `/var/downloads/${tid}/`,
            timeout: 100,
            db_type: task.db_type,
          };
          const response = await axios.post(
            `http://${automarker}/runJob`,
            body,
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

/*	POST /tasks/upload/
    @params: file
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/

router.route("/upload/").post(async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  } else if (!req.files || Object.keys(req.files).length === 0) {
    return res.status(400).send("No files were uploaded");
  }
  let initFile = req.files.init;
  let solutions = req.files.solutions;
  let tid;

  try {
    const result = await Task.findOne().sort("-tid").limit(1).exec();
    if (!result) {
      tid = 0;
    } else {
      tid = result.tid + 1;
    }

    const dir = `/var/downloads/${tid}`;
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir);
    }

    initFile.mv(`${dir}/init.sql`);
    solutions.mv(`${dir}/solutions.sql`);
    return res.status(200).json({
      message: `Files uploaded for task ${tid}`,
    });
  } catch (e) {
    console.log(e);
    return res.sendStatus(
      e.response && e.response.status ? e.response.status : 500
    );
  }
});

/*	GET /tasks/report/:tid
	  @params: tid 
    @return: the generated report (aggregated.json)
    ON SUCCESS: 200
    ON FAILURE: 404, 500
*/

router
  .route("/reports/:tid")
  .get([param("tid").isInt({ min: 0 })], (req, res) => {
    const tid = req.params.tid;
    Task.findOne({ tid })
      .then((task) => {
        if (task === null) {
          return res.sendStatus(404);
        }

        const file = `/var/downloads/${tid}/aggregated.json`;
        if (!fs.existsSync(file)) {
          return res.sendStatus(404);
        }
        res.download(file);
      })
      .catch((e) => {
        return res.sendStatus(
          e.response && e.response.status ? e.response.status : 500
        );
      });
  });

module.exports = router;
