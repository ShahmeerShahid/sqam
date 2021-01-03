const router = require("express").Router();
const { param, body, validationResult } = require("express-validator");
let Task = require("../models/task.model");
const constants = require("../constants");

/*	GET /submissions/:tid
	  @params: none 
    @return: a list of all submissions for a task
    ON SUCCESS: 200
    ON FAILURE: 500
*/

router.route("/:tid").get([param("tid").isInt()], (req, res) => {
  Task.findOne({ tid: req.params.tid })
    .then((task) => {
      res.status(200).json(task.submissions);
    })
    .catch((err) => res.status(500).send("An internal server error occurred."));
});

/*	POST /submissions/:tid
	  @params: names
    @return: 
    ON SUCCESS: 201
    ON FAILURE: 400, 409, 500
*/

router
  .route("/:tid")
  .post([param("tid").isInt(), body("names").notEmpty()], (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }
    const tid = req.params.tid;
    const names = req.body.names;

    const submissions = names.map((name) => {
      return {
        name: name,
      };
    });

    Task.findOneAndUpdate(
      { tid: tid },
      { $set: { submissions: submissions } },
      function (err) {
        if (err) {
          res.status(404).json({
            status: 404,
            message: `Could not find task with ${tid}`,
          });
        }
        res.status(201).json({
          message: `Submission(s) successfully added to task ${tid}`,
        });
      }
    ).catch((err) => {
      res.status(500).send("An internal server error occurred.");
    });
  });

/*	PATCH /submissions/status/:sid
	  @params: status
    @return:
      ON SUCCESS: 200
      ON FAILURE: 404
*/

router
  .route("/status/:sid")
  .patch(
    [param("sid").notEmpty(), body("status").isIn(constants.statuses)],
    (req, res) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }
      const sid = req.params.sid;
      const tid = req.body.tid;
      const status = req.body.status;

      Task.findOneAndUpdate(
        { tid: tid, submissions: { $elemMatch: { _id: sid } } },
        { $set: { "submissions.$.status": status } },
        function (err, task) {
          if (task === null) {
            res.sendStatus(404);
          } else if (err) {
            res.sendStatus(500);
          } else {
            res.status(200).json({
              message: `Submission ${sid} successfully updated to status ${status}`,
            });
          }
        }
      );
    }
  );

module.exports = router;
