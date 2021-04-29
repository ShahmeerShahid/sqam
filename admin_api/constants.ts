const statuses = [
	"Pending",
	"Downloading",
	"Downloaded",
	"Error",
	"Marking",
	"Complete",
];

const apps = ["automarker", "connectors", "admin", "admin_api"];

const connectors = ["markus-connector"];

const logSources = ["frontend", "automarker", "connector", "api"];

const requiredFiles = [
	"create_tables",
	"create_trigger",
	"create_function",
	"load_data",
	"solutions",
];

const dbTypes = ["mysql", "postgresql"];

const markingTypes = ["partial", "binary"];

export interface LogMessage {
	tid: number;
	source: "frontend" | "automarker" | "connector" | "api";
	message: string;
}

export interface GradesMessage {
	tid: number;
}

export interface StatusMessage {
	tid: number;
	status:
		| "Pending"
		| "Downloading"
		| "Downloaded"
		| "Error"
		| "Marking"
		| "Complete"
		| string;
}

export interface TaskMessage {
	tid: number;
	assignment_name: string;
	solutions: string;
	submissions: string;
	submission_file_name: string;
	max_marks: number;
	max_marks_per_question: number[];
	question_names: string[];
	db_type: "mysql" | "postgresql" | string;
	marking_type: "partial" | "binary" | string;
	init: string;
}

export default {
	apps,
	connectors,
	logSources,
	requiredFiles,
	statuses,
	dbTypes,
	markingTypes,
};
