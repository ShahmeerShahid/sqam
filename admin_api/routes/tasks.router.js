require("dotenv").config();
const router = require("express").Router();
const { param, body, validationResult } = require("express-validator");
const axios = require("axios");
const fs = require("fs");
let { Task, TaskSchema, Log } = require("../models/task.model");
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
				res.sendStatus(404);
			}
			res.status(200).json(task);
		})
		.catch((err) => {
			console.log(err);
			res.status(500).send("An internal server error occurred.");
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
						res.status(201).json(updatedTask);
					});
				});
			} catch (e) {
				res.status(500).send("An internal server error occurred.");
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
					res.sendStatus(404);
				} else if (err) {
					console.log(err);
					res.sendStatus(500);
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

			Task.findOneAndUpdate({ tid: tid }, update, function (err, doc) {
				if (doc === null) {
					res.sendStatus(404);
				} else if (err) {
					console.log(err);
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
