# ⚙️ SQAM

The SQL (DDL) Automarker (aka SQAM).

It is a framework for testing SQL assignments,
collecting the results, and exporting them for easy viewing using
templates.

We welcome collaboration and contributions!

## 📁 Project Structure

SQAM uses an event-driven architecture (using RabbitMQ) with microservices to decouple logic and allow for further extensibility.
All applications are dockerized.

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

## 🛠️ Usage

> make run

Starts up all services across the automarker and admin sides

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
