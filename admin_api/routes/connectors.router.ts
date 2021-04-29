import {Router} from "express";
const router = Router()
import * as config from "../config.json"

router.route("/").get((_req, res) => {
  res.json(config.connectors);
});

router.route("/results").post((_req, _res) => {});

router.route("/downloads").post((_req, _res) => {});

export default router;
