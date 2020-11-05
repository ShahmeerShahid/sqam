const router = require("express").Router();
const config = require("../config.json");

router.route("/").get((req, res) => {
  res.json(config.connectors);
});


module.exports = router;
