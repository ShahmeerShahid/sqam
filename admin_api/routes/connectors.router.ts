import {Router} from "express";
const router = Router()
import * as config from "../config.json"

router.route("/").get((_req, res) => {
  res.json(config.connectors);
});

router.route("/:connector_name/extra_fields").get((req, res) => {
  let connector_name = req.params.connector_name;
  let connector_data = config.connectors.find(data => {
    return data.name === connector_name;
  })
  if (connector_data == undefined) {
    res.status(404).send()
  } else {
    res.json(connector_data)
  }
})

router.route("/results").post((_req, _res) => {});

router.route("/downloads").post((_req, _res) => {});

export default router;
