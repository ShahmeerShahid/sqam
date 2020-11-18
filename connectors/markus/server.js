import cors from "cors";
import express from "express";
import helmet from "helmet";
import morgan from "morgan";

import { schemas as reqSchemas } from "./lib/request_schemas.js";
import markusApi from "./lib/markus_API.js";
import {
	extract_zip_to_dir,
	move_all_files_to_dir,
	remove_file,
	does_dir_exist,
} from "./lib/fs_helpers.js";
import adminApi from "./lib/admin_api.js";

const BATCH_SIZE = process.env.BATCH_SIZE || 5;

const app = express();
app.use(helmet()); // To remove insecure HTTP headers
app.use(morgan("common")); // For logging
app.use(cors()); // To set request origin policy
app.use(express.json()); // All requests must be JSON

app.get("/extra_fields", async (req, res) => {
	res.json({
		info:
			"Any extra general information to be displayed on the submission page/form e.g. group names must not have whitespace characters. Field specific should be provided as shown below.",
		extra_fields: {
			markus_URL: {
				type: "string",
				required: true,
				info:
					"Information specific to this field e.g. Example: http://www.test-markus.com, NOT www.test-markus.com or http://www.test-markus.com/en/main",
				placeholder: "http://www.test-markus.com",
			},
			assignment_id: {
				type: "number",
				required: true,
				info:
					"Found in the URL when editing the assignment. E.g. http://www.test-markus.com/en/assignments/1/edit would have ID 1.",
				placeholder: "1",
			},
			api_key: {
				type: "string",
				required: true,
				info: "Found on the homepage of your Markus instance.",
				placeholder: "hasf08etJSkf=",
			},
		},
	});
});

app.post("/tasks", async (req, res) => {
	const req_validity = reqSchemas.task.validate(req.body);
	if (req_validity.error) {
		res.status(400);
		res.json({
			message: "Invalid request",
			error: req_validity.error.details,
		});
		return;
	}
	const {
		tid,
		download_directory,
		markus_URL,
		assignment_id,
		api_key,
	} = req.body;

	const api = new markusApi(markus_URL, api_key);

	if (!(await api.is_api_key_valid())) {
		res.status(401).json({ message: "Invalid API key" });
		return;
	}

	if (!(await api.is_assignment_id_valid(assignment_id))) {
		res.status(404).json({
			message: "Invalid assignment ID, no such assignment",
		});
		return;
	}

	if (!does_dir_exist(download_directory)) {
		res.status(500).json({ message: "Download directory does not exist" });
		return;
	}

	// get group IDs and names
	const groups = await api.get_groups(assignment_id);

	// download by group IDs
	console.log(
		"Downloading submissions from",
		Object.keys(groups).length,
		"groups"
	);
	res.json({
		status: "Downloading",
		message: `Downloading files from ${Object.keys(groups).length} groups`,
	});

	const admin = new adminApi();


	try {
		for (let i = 0; i < Object.keys(groups).length; i += BATCH_SIZE) {
			const promises = Object.keys(groups)
				.slice(i, i + BATCH_SIZE)
				.map((group_name) => {
					return (async () => {
						let group_id = groups[group_name];

						console.log(`Downloading group with id ${group_id}`);
						await api.download_submission_zip(
							groups[group_name],
							assignment_id,
							download_directory
						);

						console.log(`Extracting group with id ${group_id}`);
						await extract_zip_to_dir(
							`${download_directory}/${group_id}.zip`,
							`${download_directory}/${group_name}`
						);
						await move_all_files_to_dir(
							`${download_directory}/${group_name}`,
							`${download_directory}/${group_name}`
						);
						await remove_file(
							`${download_directory}/${group_id}.zip`
						);
					})();
				});

			await Promise.all(promises);
		}
	} catch (err) {
		await admin.update_task(tid, false);
    console.log(err);
    return;
  }
	await admin.update_task(tid, true, Object.keys(groups).length);
});

const port = process.env.PORT || 8080; // Port 80 if started by docker-compose
app.listen(port, () => {
	console.log(`Listening on port ${port}`);
});
