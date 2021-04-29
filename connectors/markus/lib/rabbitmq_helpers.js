
export function publishLogMessage(channel, logContent, tid) {
	let message = {
		tid,
		source: "connector",
		message: logContent,
	};
    console.log(`TID ${tid} Logging message: ${logContent}`)
	channel.sendToQueue("logs", Buffer.from(JSON.stringify(message)));
}

export function publishErrorMessage(channel, logContent, tid) {
    publishLogMessage(channel, logContent, tid)
    publishErrorStatus(channel, tid)
}

function publishStatusMessage(channel, status, tid) {
	let message = {
		tid,
		status,
	};
    console.log(`TID ${tid} Publishing status: ${status}`);
	channel.sendToQueue("status", Buffer.from(JSON.stringify(message)));
}

export function publishDownloadingStatus(channel, tid) { 
    publishStatusMessage(channel, "Downloading", tid)
}

export function publishDownloadedStatus(channel, tid) {
    publishStatusMessage(channel, "Downloaded", tid)
}

export function publishErrorStatus(channel, tid) {
    publishStatusMessage(channel, "Error", tid)
}