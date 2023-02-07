# Agent - Load Generator

Responsible to generate the load to a target database.
It supports QuestDB (influx line protocol, postgres connector, rest) and
MongoDB.

## Getting started

This project is using gradle and spring boot.
Therefore, to run to the project you can use this command

```
./gradlew bootRun -Dspring.profiles.active=local
```

## Requirements

- Java JDK 19 with preview features enabled - 19.0.1 (Eclipse Adoptium 19.0.1+10)
- Gradle 7.6

## Build and use in experiment

To build the project execute `./gradlew build`.
This will generate a file `build/libs/agent.jar`.
This file is committed to the repository and the userdata configured in AWS will get this file.

## Endpoints

### GET /agent/v1/version

Get the deployed version of the agent.

```json
{
    "version": "2.0.10",
    "time": "2023-02-06T19:27:03.952Z"
}
```

### GET /agent/v1/results

Get the telemetries sent and number of errors at the execution time

```json
{
  "telemetries": 186219,
  "error": 102328
}
```

### POST /agent/v1/configurations

Allow to configure the agent associated to a target database.

Payload

```json
{
    "uri": "jdbc:postgresql://localhost:8812/qdb?user=admin&password=quest&sslmode=disable",
    "db": "questdb_pg",
    "vehicles": 1
}
```

Return empty response

### PUT /agent/v1/configurations

Allow to start or stop the experiment. It uses the configured target

Payload

```json
{
    "signal": "stop"
}
```

Return empty response

## Version automation

Each time that the command `./gradlew build` is executed,
the patch version is incremented. For example,:

- version before building `1.0.3`
- version after building `1.0.4`

This information is available in the GET /version endpoint.
This is useful in order to guarantee that the correct version is deployed.

## Postman collections

In the folder `/postman`, there is a collection and 2 environments.
Import those resources in postman to test the application.

### Local environment

1. Run the application in the local environment.
2. Select the environment "local"
3. Call the endpoints

### AWS environment

1. Deploy the application using iac project (terraform)
2. Select the environment "AWS"
3. Call the endpoints

## Logs

Each time the application runs, it will generate logs in the directory `/logs`.
This is the place to collect logs using CloudWatch. This folder is ignored by git.
Make sure to delete the content.