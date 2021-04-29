import { Router } from "express";
const router = Router();
import * as config from "../config.json";

/**
 * @swagger
 * components:
 *   schemas:
 *     Connector:
 *      type: object
 *      properties:
 *        name:
 *          type: string
 *          example: "MarkUs"
 *        url:
 *          description: The URL of the connector microservice
 *          type: string
 *          example: "http://markus-connector"
 *        port:
 *          description: The port at which to access info from that microservice
 *          type: integer
 *          example: 3001
 */

/**
 * @swagger
 * paths:
 *  /connectors:
 *    get:
 *      description: Use to request all connectors
 *      responses:
 *        '200':
 *          description: Returns an array of connectors with name, url, and port.
 *          content:
 *            application/json:
 *              schema:
 *                type: array
 *                items:
 *                  $ref: '#/components/schemas/Connector'
 */

router.route("/").get((_req, res) => {
  res.json(config.connectors);
});

router.route("/:connector_name/extra_fields").get((req, res) => {
  let connector_name = req.params.connector_name;
  let connector_data = config.connectors.find((data) => {
    return data.name === connector_name;
  });
  if (connector_data == undefined) {
    res.status(404).send();
  } else {
    res.json(connector_data);
  }
});

router.route("/results").post((_req, _res) => {});

router.route("/downloads").post((_req, _res) => {});

export default router;
