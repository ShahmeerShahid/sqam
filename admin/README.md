
## 📝 Table of Contents
- [📝 Table of Contents](#-table-of-contents)
- [🏁 Getting Started <a name = "getting_started"></a>](#-getting-started-)
- [Folder Structure <a name = "folder_structure"></a>](#folder-structure-)
- [Built Using <a name = "built_using"></a>](#built-using-)
- [DevTools <a name = "dev_tools"></a>](#devtools-)
	- [Prerequisites <a name = "prerequisites"></a>](#prerequisites-)
	- [Installation <a name = "installation"></a>](#installation-)
- [🎈 Usage <a name="usage"></a>](#-usage-)
- [🌲Environment Variables<a name = "environment_variables"></a>](#environment-variables)
- [Current Issues <a name = "issues"></a>](#current-issues-)
- [Authors <a name = "authors"></a>](#authors-)


## 🏁 Getting Started <a name = "getting_started"></a>
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See [deployment](#deployment) for notes on how to deploy the project on a live system.

## Folder Structure <a name = "folder_structure"></a>
- components/
	- ExampleComponent/
		- index.js
		- ExampleComponent.test.js
- pages/
- network/
- requests/
	- connectors/
	- tasks/
- helpers/
- testing/

Each component/page/request is contained within a folder accompanied with a test file.
Components/pages connect to the backend by calling requests, with either useAsync or within a useEffect.

## Built Using <a name = "built_using"></a>

- React!
- Chakra-UI
- Axios
- Formik
- Yup
- React-Testing-Library

## DevTools <a name = "dev_tools"></a>
Here are some extensions that you may find useful:
- React Developer Tools (viewing the component tree)
- Testing Playground (generating queries for testing)
- Robo 3T (access the MongoDB database)

### Prerequisites <a name = "prerequisites"></a>
What things you need to install and run the application
- Docker
- Node.js runtime

### Installation <a name = "installation"></a>
A step by step series of examples that tell you how to get a development environment running.

1. Check out the latest code on the develop branch
2. Create an environment file in the root directory
    - See [here](#environment_variables) for more details about what to put in the .env file
3. From the root directory, run make build and then make run.

## 🎈 Usage <a name="usage"></a>
- Navigate to localhost:3000 on your favorite browser

## 🌲Environment Variables<a name = "environment_variables"></a>
Your environment file should be set up like the follow below: (or look at .env example)
​```
ROOT_USERNAME=root
ROOT_PWD=#########

API_USERNAME=api
API_PWD=#########

LOCAL_URL=localhost
PROD_ADMIN_URL=admindb
​```

## Current Issues <a name = "issues"></a>
1. Routes are not protected 
	-> User should not be able to navigate to task detail page without authentication
2. Server-side rendering for shibboleth authentication.
	-> We need to use shibboleth authentication for the application. 
	-> Figure out how shibboleth authentication deals with protecting API calls. 
	-> Figure out how only using UserId divide the User, so that UI is dynamic with personal info for each user
3. UI look can be improved on the TaskView the information of Task can be organized better using cards. 
4. Log out functionality 
5. Frontend logs need to be routed to TaskView logs component.

## Authors <a name = "authors"></a>

- Shahmeer Shahid
- Jarrod Servilla
- Vaishvik Maisuria
- Terry Zhou

