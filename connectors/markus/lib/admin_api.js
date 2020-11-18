import fetch from "node-fetch"



export default function adminAPI() {

    this._send_patch_request = async (tid, reqBody) => {
        return await fetch(`http://admin_api/api/tasks/status/${tid}`, { method: 'PATCH', body: JSON.stringify(reqBody), headers: { 'Content-Type': 'application/json' } });
    }

    this.update_task = async (tid, success = true, num_submissions = 0) => {
        let body = {};
        if (success) {
            body.status = "Downloaded";
            body.num_submissions = num_submissions;
        } else {
            body.status = "Error"
        }
        return await this._send_patch_request(tid, body);
    }
}


