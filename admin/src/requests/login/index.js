//import { postRequest } from "../../network";


const bcrypt = require('bcrypt');
const saltRounds = 10;

function hashHelper({ password, saltRounds }){
bcrypt.hash(password, saltRounds).then(function(hash) {
    return hash;
}).catch(err => {
    alert(err.message)
});
}

export async function login({ username, password }) {
    const hash = hashHelper({ password, saltRounds })
    const body = {
      username: username,
      hash:hash
    };
    try {
        bcrypt.compare(password, hash).then(function(result){
          //const response = await postRequest("/api/TODO", body);
          //return response.data;
        return result;
    }).catch (err => {
      return {
        error: true,
        status: err.response && err.response.status,
        message: err.response && err.response.data,
      };
    });
} catch(e) {
    alert(e.message);
}
}