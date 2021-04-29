require("dotenv").config();
import {Router, Request, Response, NextFunction} from "express";
const router = Router()
import { param, body, validationResult } from "express-validator";
import axios from "axios";
import fs from "fs";
import { Task, TaskSchema, LogSchema } from "../models/task.model"
import constants, {StatusMessage} from "../constants";
import { Interface } from "readline";
import fileUpload, {UploadedFile} from "express-fileupload";
import {statusService, tasksService} from "../services/index"
import { BadRequestError } from "../errors";


const automarker = process.env.AUTOMARKER_URL || "localhost";
const config = { headers: { "Content-Type": "application/json" } };

/*	GET /tasks/
	  @params: none 
    @return: a list of all tasks
    ON SUCCESS: 200
    ON FAILURE: 500
*/

router.route("/").get((_req, res) => {
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

router.route("/:tid").get([param("tid").isInt({ min: 0 })], (req: Request, res: Response) => {
  const tid = Number(req.params.tid)
  Task.findOne({ tid })
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
      body("connector").notEmpty(),
      body("name").notEmpty(),
      body("status").optional().isIn(constants.statuses),
      body("tid").isEmpty(),
      body("_id").isEmpty(),
    ],
    async (req: Request, res: Response) => {
      const errors = validationResult(req);
      if (!errors.isEmpty()) {
        return res.status(400).json({ errors: errors.array() });
      }

      let newTask = undefined
      try {
        newTask = await tasksService.createNewTaskFromRequest(req)
      } catch (err) {
        return res.status(400).json({message: err.message})
      }

      try {
        tasksService.createDownloadsFolderForTask(newTask.tid)
        const channel = await req.rabbitmqChannelPromise
        await tasksService.downloadTaskSubmissions(newTask, channel)
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
      return res.status(201).json(newTask)
    });

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
		async (req: Request, res: Response, next: NextFunction) => {
      try {
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
          throw new BadRequestError(errors.array().toString())
        }
        const tid = Number(req.params.tid);
        let status = String(req.body.status);
  
        const channel = await req.rabbitmqChannelPromise;
        statusService.handleStatusMessage(channel, {
          status,
          tid,
        });
      } catch (e) {
        next(e)
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
  let initFile = req.files.init as UploadedFile
  let solutions = req.files.solutions as UploadedFile;

  let tid;

  try {
    const result = await Task.findOne().sort("-tid").limit(1).exec();
    if (!result) {
      tid = 0;
    } else {
      tid = result.tid + 1;
    }

    console.log(`Putting files in folder ${tid}`)

    const dir = `/var/downloads/${tid}`;
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir);
    }

    initFile.mv(`${dir}/init.sql`)
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
  .get([param("tid").isInt({ min: 0 })], (req: Request, res: Response) => {
    const tid = Number(req.params.tid);
    Task.findOne({ tid })
      .then((task) => {
        if (task === null) {
          return res.sendStatus(404);
        }

        const file = `/var/downloads/${tid}/aggregated.zip`;
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

export default router;
