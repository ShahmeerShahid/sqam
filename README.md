# ⚙️ SQAM

The SQL (DDL) Automarker (aka SQAM).

This system provides end to end marking of SQL assignments, including download of submission files, marking of student submissions, and delivery of results to the end user.


## 🏗 Project Architecture

SQAM uses an event-driven architecture (using RabbitMQ) with microservices.

![System Architecture](https://i.imgur.com/RNfazIG.png)

The system is split into 3 subsystems, with RabbitMQ for message driven communication between each subsystem.

- Admin System: Responsible for storing state of running tasks, exposing a frontend for end users, triggering download of submission files, and triggering marking of assignment.
- Connectors: Services used to connect to various platforms and download required submission files. See connector documentation for more info.
- Automarker: Responsible for marking submission files. See automarker documentation for more info.

All services are containerized and are ran together with `docker-compose`. See usage for more info.

## 📁 Project Structure


```text
.
├── admin                       # The frontend for the admin site
|   |── src
|   |   ├── components     
|   |   |   └── Component       # Components are contained within their own folder with an accompanying test file
|   |   |       ├── index.js
|   |   |       └── Component.test.js
|   |   ├── network             # Axios wrappers
|   |   ├── helpers
|   |   ├── pages
|   |   ├── requests            # Functions for API calls
|   |   ├── testing  
|   |   └── App.js              # Contains router, should be modified to add new pages              
|   └── README.md                         
├── admin_api
├── admindb                     # Scripts for mongodb initialization
├── automarker
├── connectors
├── rabbitmq
├── docker-compose.yml
├── Makefile                    # Provides commands for building, running, and formatting code
└── README.md                   # You are here! :)
```


`docker-compose.yml`: This file configures all services, including key environment variables. Use this file to see which ports various services are accessible on.



## 🛠️ Usage

### Building Docker containers:
```sh
docker-compose build
```

### Starting system
```sh
docker-compose up
```
Before starting system, all container images must be built. You can combine the above two steps with:
```sh
docker-compose up --build
```
We also provide a Makefile with these commands. See `Makefile` for more info.


## 💻 Technologies Used

- React
- Node.js
- Express
- MongoDB
- Flask
- RabbitMQ
- Docker

## 💡 Contributing

SQAM follows Gitflow. Please use Gitlab's merge request branch creation to generate your feature branches, format your code with prettier, always squash + merge your commits, and delete the branch after it has been merged.

```
──────────────────────── master ────────────────────────────────────    # Deployments/stable versions
        │                                   │
        └───────────── develop ─────────────                            # Development work
            │                           │
            └───────── XXX-[feature] ────                               # Feature branches
```

## 📠 Automarker

Please see the README.md in automarker/ for a more detailed overview.

## ✨Admin

Please see the README.md in admin/ for a more detailed overview.

## 🧠Admin API

Please see the README.md in admin/ for a more detailed overview.
In addition, you can visit http://localhost:9000/docs to view our API documentation using Swagger UI.
