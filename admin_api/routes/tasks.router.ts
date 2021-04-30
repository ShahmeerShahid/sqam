require("dotenv").config();
import { Router, Request, Response, NextFunction } from "express";
const router = Router();
import { param, body, validationResult } from "express-validator";
import axios from "axios";
import fs from "fs";
import { Task, TaskSchema, LogSchema } from "../models/task.model";
import constants, { StatusMessage } from "../constants";
import { Interface } from "readline";
import fileUpload, { UploadedFile } from "express-fileupload";
import { statusService, tasksService } from "../services/index";
import { BadRequestError } from "../errors";

const automarker = process.env.AUTOMARKER_URL || "localhost";
const config = { headers: { "Content-Type": "application/json" } };

/**
 * @swagger
 * components:
 *   schemas:
 *     Task:
 *      type: object
 *      properties:
 *        tid:
 *          type: integer
 *          example: 1
 *        name:
 *          type: string
 *          example: "CSC343 A2"
 *        status:
 *          type: string
 *          enum: ["Pending", "Downloading", "Downloaded", "Error", "Marking", "Complete"]
 *          example: "Pending"
 *        connector:
 *          type: string
 *          enum: ["markus-connector"]
 *          example: "markus-connector"
 *        submissions:
 *          type: array
 *          items:
 *            $ref: '#/components/schemas/Submission'
 *        num_submissions:
 *          type: integer
 *          example: 12
 *        max_marks:
 *          type: integer
 *          example: 100
 *        max_marks_per_question:
 *          type: array
 *          items:
 *            type: integer
 *          example: [1, 1, 2, 2, 5, 7]
 *        marking_type:
 *          type: string
 *          enum: ["partial", "binary"]
 *          example: "partial"
 *        question_names:
 *          type: array
 *          items:
 *            type: string
 *          example: ["Q1", "Q2", "Q3", "Q4a", "Q4b"]
 *        submission_file_name:
 *          type: string
 *          example: "a2.sql"
 *        initFile:
 *          type: string
 *          example: "a2-init.sql"
 *        solutions:
 *          type: string
 *          example: "a2-soln.sql"
 *        submissions_path:
 *          type: string
 *          example: ""
 *        timeout:
 *          type: integer
 *          example: 100
 *        db_type:
 *          type: string
 *          example: "mysql"
 *        extra_fields:
 *          type: object
 *        logs:
 *          type: array
 *          items:
 *            $ref: '#/components/schemas/Log'
 *     Log:
 *      type: object
 *      properties:
 *        timestamp:
 *          type: string
 *        text:
 *          type: string
 *        source:
 *          type: string
 *          enum: ["frontend", "automarker", "connector", "api"]
 */

/**
 * @swagger
 * paths:
 *  /tasks:
 *    get:
 *      description: Use to request all tasks
 *      responses:
 *        '200':
 *          description: Returns an array of all tasks.
 *          content:
 *            application/json:
 *              schema:
 *                type: array
 *                items:
 *                  $ref: '#/components/schemas/Task'
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

/**
 * @swagger
 * paths:
 *  /tasks/:tid:
 *    get:
 *      description: Use to request a specific Task
 *      parameters:
 *      - name: tid
 *        description: unique id of task
 *        required: true
 *        type: integer
 *      responses:
 *        '200':
 *          description: Returns a Task object
 *          content:
 *            application/json:
 *              schema:
 *                $ref: '#/components/schemas/Task'
 *        '404':
 *          description: Occurs when there is no Task object with that tid
 */

router
  .route("/:tid")
  .get([param("tid").isInt({ min: 0 })], (req: Request, res: Response) => {
    const tid = Number(req.params.tid);
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

/**
 * @swagger
 * paths:
 *  /tasks/:
 *    post:
 *      description: Used to create a Task. This subsequently posts to the connector, and starts the automarking chain.
 *      requestBody:
 *        required: true
 *        content:
 *          application/json:
 *            schema:
 *                properties:
 *                  connector:
 *                    required: true
 *                    type: string
 *                    enum: ["markus-connector"]
 *                    example: "markus-connector"
 *                  name:
 *                    required: true
 *                    type: string
 *                  status:
 *                    required: false
 *                    type: string
 *                    enum: ["Pending", "Downloading", "Downloaded", "Error", "Marking", "Complete"]
 *                    example: "Pending"
 *                  submissions:
 *                    required: false
 *                  extra_fields:
 *                    required: false
 *      responses:
 *       '201':
 *         description: Task object successfully created
 *       '400':
 *         description: Request body improperly formatted
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

      let newTask = undefined;
      try {
        newTask = await tasksService.createNewTaskFromRequest(req);
      } catch (err) {
        return res.status(400).json({ message: err.message });
      }

      try {
        tasksService.createDownloadsFolderForTask(newTask.tid);
        const channel = await req.rabbitmqChannelPromise;
        await tasksService.downloadTaskSubmissions(newTask, channel);
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
      return res.status(201).json(newTask);
    }
  );

/**
 * @swagger
 * paths:
 *  /tasks/:tid:
 *    patch:
 *      description: Used to modify the fields of a Task. This is called by the automarker to set the appropriate status, once the marking job is done.
 *      parameters:
 *        - name: tid
 *          description: unique id of task
 *          required: true
 *          type: integer
 *      requestBody:
 *        required: true
 *        content:
 *          application/json:
 *            schema:
 *                properties:
 *                  name:
 *                    required: false
 *                    type: string
 *                  status:
 *                    required: false
 *                    type: string
 *                    enum: ["Pending", "Downloading", "Downloaded", "Error", "Marking", "Complete"]
 *                    example: "Pending"
 *                  submissions:
 *                    required: false
 *                  num_submissions:
 *                    required: false
 *                    type: integer
 *                  extra_fields:
 *                    required: false
 *      responses:
 *       '200':
 *         description: Task object successfully modified
 *       '400':
 *         description: Request body improperly formatted
 *       '404':
 *         description: task with tid does not exist
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
          throw new BadRequestError(errors.array().toString());
        }
        const tid = Number(req.params.tid);
        let status = String(req.body.status);

        const channel = await req.rabbitmqChannelPromise;
        statusService.handleStatusMessage(channel, {
          status,
          tid,
        });
      } catch (e) {
        next(e);
      }
    }
  );

/**
 * @swagger
 * paths:
 *  /tasks/upload/:
 *    post:
 *      description: Used to upload the init and solutions files
 *      requestBody:
 *        required: true
 *        content:
 *          application/json:
 *            schema:
 *                properties:
 *                  tid:
 *                    required: true
 *                    type: integer
 *                    example: 1
 *                  initFile:
 *                    required: true
 *                  solutions:
 *                    required: true
 *      responses:
 *       '200':
 *         description: Files uploaded successfully
 *       '400':
 *         description: Request body improperly formatted
 *       '404':
 *         description: Task with tid does not exist
 */

router.route("/upload/").post(async (req, res) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ errors: errors.array() });
  } else if (!req.files || Object.keys(req.files).length === 0) {
    return res.status(400).send("No files were uploaded");
  }
  let initFile = req.files.init as UploadedFile;
  let solutions = req.files.solutions as UploadedFile;

  let tid;

  try {
    const result = await Task.findOne().sort("-tid").limit(1).exec();
    if (!result) {
      tid = 0;
    } else {
      tid = result.tid + 1;
    }

    console.log(`Putting files in folder ${tid}`);

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

/**
 * @swagger
 * paths:
 *  /tasks/reports/:tid:
 *    get:
 *      description: Serves the report files of a task
 *      parameters:
 *        - name: tid
 *          description: unique id of task
 *          required: true
 *          type: integer
 *      responses:
 *       '200':
 *         description: Files returned successfully
 *       '404':
 *         description: Task with tid does not exist
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
