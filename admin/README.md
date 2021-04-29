````
## 📝 Table of Contents
- [Getting Started](#getting_started)
- [Folder Structure](#folder_structure)
- [Built Using](#built_using)
- [Dev Tools](#dev_tools)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Environment Variables](#environment_variables)
- [Current Issues](#issues)
- [Authors](#authors)

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
- Navigate to localhost:3000 on your favourite browser

## 🌲Environment Variables<a name = "environment_variables"></a>
Your environment file should be set up like the follow below: (or look at .envexample)
​```
ROOT_USERNAME=root
ROOT_PWD=#########

API_USERNAME=api
API_PWD=#########

LOCAL_URL=localhost
PROD_ADMIN_URL=admindb
​```

## Authors

- Shahmeer Shahid
- Jarrod Servilla
- Vaishvik Maisuria
- Terry Zhou
````
