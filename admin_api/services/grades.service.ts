import { GradesMessage } from "../constants";

function handleGrades(grades: GradesMessage) {
	console.log(`Received grades from task ${grades.tid}`);
}

export default { handleGrades };
