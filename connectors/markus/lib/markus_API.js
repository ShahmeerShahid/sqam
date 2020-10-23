import fetch from "node-fetch"
import fs from "fs"

function markusAPI(base_url, api_key) {

    this.api_url = `${base_url}/api/`;
    this.api_key = api_key;

    const _headers = {
        "Authorization": `MarkUsAuth ${this.api_key}`,
        "Content-Type": "application/json"
    }

    this._send_get_request = async (request) => {
        const res = await fetch(`${this.api_url}/${request}`, { method: 'GET', headers: _headers });
        return await res.json();
    }

    // https://stackoverflow.com/questions/37614649/how-can-i-download-and-save-a-file-using-the-fetch-api-node-js
    this._download_file = (async (url, path) => {

        const res = await fetch(url, { method: 'GET', headers: _headers });
        const fileStream = fs.createWriteStream(path);
        await new Promise((resolve, reject) => {
            res.body.pipe(fileStream);
            res.body.on("error", (err) => {
                reject(err);
            });
            fileStream.on("finish", function () {
                resolve();
            });
        });
    });

    this.is_api_key_valid = async () => {
        const res = await fetch(`${this.api_url}/assignments`, { method: 'GET', headers: _headers });
        return res.status !== 403
    }

    this.is_assignment_id_valid = async (assignment_id) => {
        const res = await fetch(`${this.api_url}/assignments/${assignment_id}.json`, { method: 'GET', headers: _headers });
        return res.status === 200;
    }

    // Returns group names by id
    this.get_groups = (assignment_id) => {
        return this._send_get_request(`/assignments/${assignment_id}/groups/group_ids_by_name.json`);
    }

    this.download_submission_zip = async (group_id, assignment_id, download_directory) => {
        // GET /api/assignments/:assignment_id/groups/:group_id/submission_files
        try {
            return await this._download_file(`${this.api_url}/assignments/${assignment_id}/groups/${group_id}/submission_files`, `${download_directory}/${group_id}.zip`)
        } catch (err) {
            throw (err);
        }
    }
}

export default markusAPI;