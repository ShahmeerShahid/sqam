const router = require("express").Router();
const { param, body, validationResult } = require("express-validator");
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
          res.status(201).json(task);
        })
        .catch((err) => {
          console.log(err);
          res.status(500).send("An internal server error occurred.");
        });
    }
  );

/*	PATCH /tasks/status/:tid
	  @params: status
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/

router
  .route("/status/:tid")
  .patch(
    [param("tid").isInt(), body("status").isIn(constants.statuses)],
    (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const tid = req.params.tid;
      const status = req.body.status;

      Task.findOneAndUpdate({ tid: tid }, { status: status }, function (
        err,
        doc
      ) {
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
