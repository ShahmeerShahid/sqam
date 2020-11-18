export const demo = async ({ username, password }) => {
	return new Promise((resolve, reject) => {
	  setTimeout(() => {
		if (username === 'test@utoronto.ca' && password === 'password') {
		  resolve();
		} else {
		  reject();
		}
	  }, 3000);
	});
};