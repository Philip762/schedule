# Flight scheduling API

## Requirements

- Docker
- Java 21 (for development)

## Overview

This is a backend REST API used to manage flight for an airline.
This project consists of 2 applications:

- A Springboot REST API with a Swagger dashboard
- A MySql (or other) database

## Running the project

The entire project can be run via the ``docker-compose.yml`` file by executing the following command.

````
docker compose up
````

This will start a container for the MySql database and the Springboot API.
By default, the Springboot API will be available on ``http://localhost:4000``
with the swagger dashboard at ``http://localhost:4000/swagger-ui/index.html``
The database will be running on port 3000.

## Configuration

By default, the project is run in development mode.
General configuration (database credentials, port numbers) are kept in the ``.env`` file.

## Testing

All test can be run and verified by running the following command.

````
./mvnw test
````