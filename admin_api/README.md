# Admin API

This service acts as the "brain" of the system. It uses MongoDB to manage the state of the system, exposes RESTful endpoints for the frontend to use, and communicates with other services through RabbitMQ.

## Getting Started

This service is written in Typescript. Install Node and Typescript, then run `npm start` or `npm start dev`.

## Directory Structure

```
├── Dockerfile
├── README.md
├── config.json # Configuration file, see Configuration below
├── constants.ts
├── errors.ts
├── helpers.ts
├── models    # Database models
│   ├── task.model.ts
│   └── user.model.ts
├── package-lock.json
├── package.json
├── rabbitmq    # RabbitMQ "routes"
│   ├── consumers
│   │   ├── grades.consumer.ts
│   │   ├── index.ts
│   │   ├── logs.consumer.ts
│   │   └── status.consumer.ts
│   └── init.ts
├── routes    # HTTP endpoints
│   ├── connectors.router.ts
│   ├── submissions.router.ts
│   └── tasks.router.ts
├── server.ts  # Main file
├── services
│   ├── connectors.service.ts
│   ├── grades.service.ts
│   ├── index.ts
│   ├── logs.service.ts
│   ├── status.service.ts
│   └── tasks.service.ts
├── tests
│   ├── setup.js
│   ├── submissionsRouter.test.js
│   └── tasksRouter.test.js
└── tsconfig.json
```

## Configuration

### Environment variables

The following environment variables are required to be configured in `docker-compose.yml`

- PORT
- PROD_DB_URL
- DB_USERNAME
- DB_PASSWD



### Connectors
Available connectors must be configured in `config.json`. Each connector must provide a name, info, and extra fields, as shown below:

```json
{
  "connectors": [
    {
      "name": "Markus",
      "info": "Any extra general information to be displayed on the submission page/form e.g. group names must not have whitespace characters. Field specific should be provided as shown below.",
      "extra_fields": {
        "markus_URL": {
          "type": "string",
          "required": true,
          "info": "Information specific to this field e.g. Example: http://www.test-markus.com, NOT www.test-markus.com or http://www.test-markus.com/en/main",
          "placeholder": "http://www.test-markus.com"
        },
        "assignment_id": {
          "type": "number",
          "required": true,
          "info": "Found in the URL when editing the assignment. E.g. http://www.test-markus.com/en/assignments/1/edit would have ID 1.",
          "placeholder": "1"
        },
        "api_key": {
          "type": "string",
          "required": true,
          "info": "Found on the homepage of your Markus instance.",
          "placeholder": "hasf08etJSkf="
        }
      }
    }
  ]
}
```
For each field, `type`, `required` and `placeholder` MUST be provided. `info` is optional. JSON types can be found at https://json-schema.org/understanding-json-schema/reference/type.html. The frontend will dynamically render a form based on this configuration.

The connector name must be the same as what was provided in `docker-compose.yml`, see `connectors/README.md` for more information.

## API Documentation

After starting the system, visit http://localhost:9000/docs to view our API documentation using Swagger UI.

## Future Work

- Migrate business logic from `routes/tasks.router.ts` to `services/*.service.ts`