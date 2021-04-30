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
- RabbitMQ: Acts as a communication plane between all services. For more info read `rabbitmq/README.md`.

All services are containerized and are ran together with `docker-compose`. See usage for more info.

## 🌊 Run-through of System

1. A task is submitted from the frontend, and API request is sent to the Admin API
2. Admin API saves the new task in MongoDB
3. Admin API publishes a message in `task_to_download_$connectorName` in queue.
4. Relevant connector consumes message and starts downloading submission files. Files are downloaded into a shared volume which is accessible to Admin API, Automarker, and connectors. See `docker-compose.yml` for more info.
5. As the connector is downloading files, it publishes messages in the `logs` queue. These messages are consumed by Admin API and are inserted into the Task object in MongoDB
6. When the connector is finished downloading, it publishes a message in the `Status` queue. Admin API consumes this message and updates the `status` field in MongoDB
7. Admin API publishes a message in the `tasks_to_download` queue. Automarker consumes this message and begins marking.
8. Automarker produces log messages as it marks.
9. When Automarker finishes marking, it publishes a status message.
10. Admin/Admin API provides report files to the end user, which can be downloaded through the frontend.

## 📁 Project Structure


```text
.
├── admin                       # The frontend for the admin site
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

Please read all documentation, including each `README.md` file in each subfolder of this project.

SQAM follows Gitflow. Please use Gitlab's merge request branch creation to generate your feature branches, format your code with prettier, always squash + merge your commits, and delete the branch after it has been merged.

```
──────────────────────── master ────────────────────────────────────    # Deployments/stable versions
        │                                   │
        └───────────── develop ─────────────                            # Development work
            │                           │
            └───────── XXX-[feature] ────                               # Feature branches
```

## 🧪 End-to-end Testing 

To run an end-to-end test, there needs to be a running Markus instance with demo assignment submissions (`automarker/SQAM/Demo_Postgres/Submissions/`). 

1. Start system
2. Navigate to frontend (`localhost:3000`)
3. Click on "Tasks" link on top left
4. Add new task
5. Choose Markus as connector
6. Configure task. The demo assignment has 10 questions, each worth 5 marks, name "Query 1", "Query 2", "Query 3" etc.
7. Upload `init.sql` and `solutions.sql` (found in `automarker/SQAM/Demo_Postgres/`)
8. Submit task
9. Navigate to Tasks page and refresh
10. Click on newly created task
11. Refresh page to update logs
12. After task is complete, click on "Download Report"


## 📝 Subsystem Testing

To test individual parts of the system, you can use the RabbitMQ management console to manually send messages in queues. Read `docker-compose.yml` to read which port the management console is available on, as well as what the username and password is. By default, the port is `15672` and the username/password is `guest`.

## 🚧 Known Issues

The Automarker is very resource intensive and requires upwards of 32 GB of RAM if provided with large solution datasets. `docker-compose` automatically limits the resource consumption of services, and this limitation is very difficult if not impossible to get around. According to online documentation Docker Swarm does not have the same limitations, and the same `docker-compose.yml` file can be used to deploy the system to a Docker Swarm stack. Alternatively, bash scripts can be written to start the Automarker natively (ie outside the Docker container).