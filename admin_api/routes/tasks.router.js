const router = require("express").Router();
let Task = require("../models/task.model");

router.route("/").get((req, res) => {
  Task.find()
    .then((tasks) => {
      res.status(200).json(tasks);
    })
    .catch((err) => res.status(400).json("Error: " + err));
});

router.route("/").post((req, res) => {
  const newTask = new Task(req.body);
  newTask
    .save()
    .then((task) => {
      res.status(200).json(task);
    })
    .catch((err) => {
      res.status(400).send("adding new task failed");
    });
});

router.route("/ping").get((req, res) => {
  res.json({ message: "Pong!" });
});

module.exports = router;
