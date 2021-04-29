import { Channel } from "amqplib";
import { TaskMessage, LogMessage } from "../constants";
import { Request } from "express";
import { Task, TaskDoc } from "../models/task.model";
import { connectorsService, logsService } from "./index";
import fs from "fs";

// TODO: Migrate all business logic from routes/tasks.router.ts to here

function publishTaskToMark(channel: Channel, taskMessage: TaskMessage) {
  channel.sendToQueue("task_to_mark", Buffer.from(JSON.stringify(taskMessage)));
}

async function createNewTaskFromRequest(req: Request) {
  const newTask = new Task(req.body);
  const connector = req.body.connector;
  await newTask.save();
  return newTask;
}

function createDownloadsFolderForTask(tid: number) {
  const dir = `/var/downloads/${tid}`;
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir);
  }
  fs.writeFileSync(`${dir}/aggregated.json`, JSON.stringify({}));
}

async function downloadTaskSubmissions(task: TaskDoc, channel: Channel) {
  task.initFile = `/var/downloads/${task.tid}/init.sql`;
  task.solutions = `/var/downloads/${task.tid}/solutions.sql`;
  const updatedTask = await task.save();
  const log: LogMessage = {
    tid: task.tid,
    message: "Initiating submissions download",
    source: "api",
  };
  logsService.addLog(log);
  connectorsService.publishTaskToDownload(updatedTask, channel);
}

export default {
  publishTaskToMark,
  createNewTaskFromRequest,
  createDownloadsFolderForTask,
  downloadTaskSubmissions,
};
