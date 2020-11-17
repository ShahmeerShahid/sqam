const router = require("express").Router();
const fs = require("fs");
const mkdirp = require("mkdirp");
const { body, validationResult } = require("express-validator");
const constants = require("../constants");

/*	POST /logs/
	  @params: app, filename, text
    @return: 
    ON SUCCESS: 200
    ON FAILURE: 400, 500
*/

router.route("/").post(
  [
    body("app").isIn(constants.apps),
    body("filename")
      .notEmpty()
      .custom((value, { req }) => value.endsWith(".txt")),
    body("text").notEmpty(),
  ],
  (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ errors: errors.array() });
    }

    const app = req.body.app;
    const filename = req.body.filename;
    const text = req.body.text;

    const date = new Date();
    const dateString = date.toISOString().slice(0, 10);

    try {
      mkdirp(`logs/${app}/${dateString}`).then(() => {
        var stream = fs.createWriteStream(
          `logs/${app}/${dateString}/${filename}`,
          {
            flags: "a",
          }
        );
        stream.write(text + "\n");
        stream.end();
      });
      return res
        .status(200)
        .send(`Log for ${app} with text ${text} written to ${filename}`);
    } catch (e) {
      return res.status(500).send("An error occurred!");
    }
  }
);

module.exports = router;
