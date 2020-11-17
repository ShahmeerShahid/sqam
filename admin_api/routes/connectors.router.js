const router = require("express").Router();
const config = require("../config.json");

router.route("/").get((req, res) => {
  res.json(config.connectors);
});

router.route("/results").post((req, res) => {});

router.route("/downloads").post((req, res) => {});

module.exports = router;
