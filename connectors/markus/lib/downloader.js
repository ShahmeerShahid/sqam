import markusApi from "./markus_API.js";
import {
	extract_zip_to_dir,
	move_all_files_to_dir,
	remove_file,
	does_dir_exist,
} from "./fs_helpers.js";

import {
	publishLogMessage,
	publishDownloadingStatus,
	publishDownloadedStatus,
	publishErrorMessage,
} from "./rabbitmq_helpers.js";

const BATCH_SIZE = process.env.BATCH_SIZE || 5;

export async function downloadSubmissions(
	tid,
	download_directory,
	markus_URL,
	assignment_id,
	api_key,
	channel
) {
	try {
		const api = new markusApi(markus_URL, api_key);
		if (!(await api.is_api_key_valid())) {
			publishErrorMessage(channel, "Error: Invalid API key", tid);
			return;
		}

		if (!(await api.is_assignment_id_valid(assignment_id))) {
			publishErrorMessage(
				channel,
				"Error: Invalid assignment ID, no such assignment",
				tid
			);
			return;
		}

		if (!does_dir_exist(download_directory)) {
			publishErrorMessage(
				channel,
				"Error: Download directory does not exist",
				tid
			);
			return;
		}

		// get group IDs and names
		const groups = await api.get_groups(assignment_id);

		// download by group IDs
		publishLogMessage(
			channel,
			`Downloading submissions from ${Object.keys(groups).length} groups`,
			tid
		);
		publishDownloadingStatus(channel, tid);

		for (let i = 0; i < Object.keys(groups).length; i += BATCH_SIZE) {
			const promises = Object.keys(groups)
				.slice(i, i + BATCH_SIZE)
				.map((group_name) => {
					return (async () => {
						let group_id = groups[group_name];

						publishLogMessage(
							channel,
							`Downloading group with id ${group_id}`,
							tid
						);
						await api.download_submission_zip(
							groups[group_name],
							assignment_id,
							download_directory
						);

						publishLogMessage(
							channel,
							`Extracting group with id ${group_id}`,
							tid
						);
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
		publishErrorMessage(channel, err.message, tid);
		console.log(err);
		return;
	}
	publishDownloadedStatus(channel, tid);
}
