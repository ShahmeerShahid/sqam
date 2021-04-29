import { Router, Request, Response } from "express";
import { param, body, validationResult } from "express-validator";
// let Task = require("../models/task.model");
import { Task } from "../models/task.model";
import constants from "../constants";

const router = Router();
/*	GET /submissions/:tid
	  @params: none 
    @return: a list of all submissions for a task
    ON SUCCESS: 200
    ON FAILURE: 500
*/

router
	.route("/:tid")
	.get([param("tid").isInt()], async (req: Request, res: Response) => {
		const tid = Number(req.params.tid);
		const task = await Task.findOne({ tid: tid });
		if (task === null) {
			res.status(404).send("Could not find task");
			return;
		}
		try {
			res.status(200).json(task.submissions);
		} catch (err) {
			res.status(500).send("An internal server error occurred.");
		}
	});

/*	POST /submissions/:tid
	  @params: names
    @return: 
    ON SUCCESS: 201
    ON FAILURE: 400, 409, 500
*/

router
	.route("/:tid")
	.post(
		[param("tid").isInt(), body("names").notEmpty()],
		async (req: Request, res: Response) => {
			const errors = validationResult(req);
			if (!errors.isEmpty()) {
				return res.status(400).json({ errors: errors.array() });
			}
			const tid = Number(req.params.tid);
			const names = req.body.names;

			const submissions = names.map((name: string) => {
				return {
					name: name,
				};
			});

			try {
				await Task.findOneAndUpdate(
					{ tid: tid },
					{ $set: { submissions: submissions } }
				).orFail();
			} catch (err) {
				res.status(404).json({
					status: 404,
					message: `Could not find task with ${tid}`,
				});
				return;
			}
			res.status(201).json({
				message: `Submission(s) successfully added to task ${tid}`,
			});
		}
	);

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
		async (req: Request, res: Response) => {
			const errors = validationResult(req);
			if (!errors.isEmpty()) {
				return res.status(400).json({ errors: errors.array() });
			}
			const sid = req.params.sid;
			const tid = req.body.tid;
			const status = req.body.status;

			try {
				await Task.findOneAndUpdate(
					{ tid: tid, submissions: { $elemMatch: { _id: sid } } },
					{ $set: { "submissions.$.status": status } }
				).orFail();
			} catch (err) {
				res.sendStatus(404);
				return;
			}

			res.status(200).json({
				message: `Submission ${sid} successfully updated to status ${status}`,
			});
		}
	);

export default router;
