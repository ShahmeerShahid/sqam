import fs from "fs"
import { execFileSync, execFile } from "child_process"

export const extract_zip_to_dir = (zip_path, out_path) => {
    return new Promise((resolve, reject) => {
        execFileSync(`${process.env.PWD}/lib/unzip.sh`, [zip_path, out_path]);
        resolve();
    })
}

export const move_all_files_to_dir = (fromPath, toDir) => {
    return new Promise((resolve, reject) => {
        execFileSync(`${process.env.PWD}/lib/mv.sh`, [`${fromPath}`]);
        resolve();
    })
}

export const remove_file = (file_path) => {
    return new Promise((resolve, reject) => {
        fs.unlinkSync(file_path);
        resolve();
    })

}

export const does_dir_exist = (dirPath) => {
    return fs.existsSync(dirPath);
}