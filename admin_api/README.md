Node.js API utilizing Mongoose to interface with the Mongo database.

## Models

Defined schemas for User/Tasks

## Resources

<!-- Endpoints for Users/Tasks -->

### Tasks

TODO


### Connectors
**Definition**
`GET /api/connectors`

**Response**
- `200 OK` on success
List of connectors (name and url) that can be used to download submissions
```json
[
    {
        "name": "Markus",
        "url": "http://markus-connector"
    },
    {
        "name": "Example",
        "url": "http://example"
    }
]
```
***


## Known Issues

Sometimes the admin_api must be started after admindb is accepting connections



## `config.json`

`config.json` can be used to configure the system settings.

### Connectors

To add/remove connectors, specify the name and url of the connector in `config.json`.