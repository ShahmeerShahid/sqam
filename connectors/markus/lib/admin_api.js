import fetch from "node-fetch"



export default function adminAPI() {
    this.tid = tid;
    
    this._send_patch_request = async (tid, body) => {
        await fetch(`http://admin_api/api/task/status/${tid}`, { method: 'PATCH', body });
    }
    
    this.update_task = async (tid, success = true, num_submissions = 0) => {
        let body = {};
        if (success) {
            body.status = "Downloaded";
            body.num_submissions = num_submissions;
        } else {
            body.status = "Error"
        }
        await this._send_patch_request(tid, body);
    }
}


