const router = require("express").Router();
const { param, body, validationResult } = require("express-validator");
const fs = require("fs");
let Task = require("../models/task.model");
const constants = require("../constants");

/*	GET /tasks/
	  @params: none 
    @return: a list of all tasks
    ON SUCCESS: 200
    ON FAILURE: 500
*/

router.route("/").get((req, res) => {
  Task.find()
    .then((tasks) => {
      res.status(200).json(tasks);
    })
    .catch((err) => {
      console.log(err);
      res.status(500).send("An internal server error occurred.");
    });
});

/*	POST /tasks/
	  @params: name, status, [submissions, extra_fields] 
    @return: 
    ON SUCCESS: 201
    ON FAILURE: 400, 409, 500

    tid should not be supplied
*/

router
  .route("/")
  .post(
    [body("name").notEmpty(), body("status").isIn(constants.statuses)],
    (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }

      const newTask = new Task(req.body);
      newTask
        .save()
        .then((task) => {
          const dir = `/var/downloads/${task.tid}`;

          if (!fs.existsSync(dir)) {
            fs.mkdirSync(dir);
          }

          res.status(201).json(task);
        })
        .catch((err) => {
          console.log(err);
          res.status(500).send("An internal server error occurred.");
        });
    }
  );

/*	PATCH /tasks/status/:tid
	  @params: status, [num_submissions]
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/

router
  .route("/status/:tid")
  .patch(
    [
      param("tid").isInt(),
      body("status").isIn(constants.statuses),
      body("num_submissions").optional(),
    ],
    (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const tid = req.params.tid;
      const status = req.body.status;

      let update = {
        status: status,
      };

      if (status === "Downloaded") {
        //do stuff relevant to that
        const num_submissions = req.body.num_submissions;
        if (num_submissions) update.num_submissions = num_submissions;
      }

      if (req.body.num_groups)
        Task.findOneAndUpdate({ tid: tid }, update, function (err, doc) {
          if (doc === null) {
            res.sendStatus(404);
          } else if (err) {
            res.sendStatus(500);
          } else {
            res.status(200).json({
              message: `Task ${tid} successfully updated to status ${status}`,
            });
          }
        });
    }
  );

module.exports = router;
