# Control - Experiment Controller

Orchestrator of the experiment. Responsible for:

- Create tables in QuestDB and MongoDB
- Clean data in databases for new experiments
- Initialize agents with correct configuration
- Start and stop experiment
- Allow to visualize summary data of the experiment

## Getting started

This project is using gradle and spring boot.
Therefore, to run to the project you can user this command

```
./gradlew bootRun -Dspring.profiles.active=local
```

## Requirements

- Java JDK 19 with preview features enabled - 19.0.1 (Eclipse Adoptium 19.0.1+10)
- Gradle 7.6

## Build and use in experiment

To build the project execute `./gradlew build`.
This will generate a file `build/libs/control.jar`.
This file is committed to the repository and the userdata configured in AWS will get this file.

## Endpoints

### GET /control/v1/versions

Get the deployed version of the control.
After the agents are configured, the it will get the version of the agents as well.

```json
{
    "control": {
        "version": "2.0.11",
        "time": "2023-02-05T17:06:07.805Z"
    },
    "agents": {
        "http://10.0.2.30:8080": {
            "version": "2.0.9",
            "time": "2023-02-05T17:06:03.016Z"
        },
        "http://10.0.2.32:8080": {
            "version": "2.0.9",
            "time": "2023-02-05T17:06:03.016Z"
        },
        "http://10.0.2.33:8080": {
            "version": "2.0.9",
            "time": "2023-02-05T17:06:03.016Z"
        },
        "http://10.0.2.31:8080": {
            "version": "2.0.9",
            "time": "2023-02-05T17:06:03.016Z"
        }
    }
}
```

### GET /control/v1/experiments

Get the information about the experiment.
Each step will generate new metadata for the experiment.
It contains:

- Table configurations timestamps
- Agents configured
- Experiment timestamp
- One summary with the global throughput for the experiment

```json
{
    "tables": {
        "status": "completed",
        "start": "2023-02-05T17:45:30.841Z",
        "end": "2023-02-05T17:45:31.427Z"
    },
    "execution": {
        "status": "completed",
        "start": "2023-02-05T19:35:27.064Z",
        "end": "2023-02-05T19:43:38.221Z"
    },
    "agents": [
        {
            "location": "http://10.0.2.30:8080",
            "type": "questdb_pg",
            "datasource": "jdbc:postgresql://10.0.2.20:8812/qdb?user=admin&password=quest&sslmode=disable",
            "vehicles": 1
        },
        {
            "location": "http://10.0.2.31:8080",
            "type": "questdb_rest",
            "datasource": "http://10.0.2.21:9000",
            "vehicles": 20
        },
        {
            "location": "http://10.0.2.32:8080",
            "type": "questdb_influx",
            "datasource": "10.0.2.22:9009",
            "vehicles": 1
        },
        {
            "location": "http://10.0.2.33:8080",
            "type": "mongodb",
            "datasource": "mongodb://10.0.2.23",
            "vehicles": 200
        }
    ],
    "results": {
        "questdb_pg": {
            "inserted": 2612332,
            "duration": {
                "total_in_seconds": 491,
                "total_in_minutes": 8,
                "total_in_hours": 0
            },
            "throughput": {
                "rps": 5320.431771894094,
                "rpm": 326541.5,
                "rph": "Infinity"
            }
        },
        "questdb_rest": {
            "inserted": 0,
            "duration": {
                "total_in_seconds": 491,
                "total_in_minutes": 8,
                "total_in_hours": 0
            },
            "throughput": {
                "rps": 0.0,
                "rpm": 0.0,
                "rph": "NaN"
            }
        },
        "questdb_influx": {
            "inserted": 8158019,
            "duration": {
                "total_in_seconds": 491,
                "total_in_minutes": 8,
                "total_in_hours": 0
            },
            "throughput": {
                "rps": 16615.109979633402,
                "rpm": 1019752.375,
                "rph": "Infinity"
            }
        },
        "mongodb": {
            "inserted": 0,
            "duration": {
                "total_in_seconds": 491,
                "total_in_minutes": 8,
                "total_in_hours": 0
            },
            "throughput": {
                "rps": 0.0,
                "rpm": 0.0,
                "rph": "NaN"
            }
        }
    }
}
```

### POST /control/v1/setups/tables

Create the tables in the databases.

Payload

```json
[
  {
    "type": "questdb",
    "uri": "http://10.0.2.20:9000"
  },
  {
    "type": "questdb",
    "uri": "http://10.0.2.21:9000"
  },
  {
    "type": "questdb",
    "uri": "http://10.0.2.22:9000"
  },
  {
    "type": "mongodb",
    "uri": "mongodb://10.0.2.23"
  }
]
```

Response

```json
[
    {
        "type": "questdb",
        "uri": "http://10.0.2.20:9000",
        "status": "done"
    },
    {
        "type": "questdb",
        "uri": "http://10.0.2.21:9000",
        "status": "done"
    },
    {
        "type": "questdb",
        "uri": "http://10.0.2.22:9000",
        "status": "done"
    },
    {
        "type": "mongodb",
        "uri": "mongodb://10.0.2.23",
        "status": "done"
    }
]
```

### POST /control/v1/setups/agents

Configure and initialize agents for the experiment. 
The questdb influx agents takes more time because it needs to set up the connection

Payload

```json
[
    {
        "location": "http://10.0.2.30:8080",
        "type": "questdb_pg",
        "datasource": "jdbc:postgresql://10.0.2.20:8812/qdb?user=admin&password=quest&sslmode=disable",
        "vehicles": 1
    },
    {
        "location": "http://10.0.2.31:8080",
        "type": "questdb_rest",
        "datasource": "http://10.0.2.21:9000",
        "vehicles": 20
    },
    {
        "location": "http://10.0.2.32:8080",
        "type": "questdb_influx",
        "datasource": "10.0.2.22:9009",
        "vehicles": 1
    },
    {
        "location": "http://10.0.2.33:8080",
        "type": "mongodb",
        "datasource": "mongodb://10.0.2.23",
        "vehicles": 200
    }
]
```

Response

```json
[
    {
        "location": "http://10.0.2.30:8080",
        "status": "done"
    },
    {
        "location": "http://10.0.2.31:8080",
        "status": "done"
    },
    {
        "location": "http://10.0.2.32:8080",
        "status": "done"
    },
    {
        "location": "http://10.0.2.33:8080",
        "status": "done"
    }
]
```

### PATCH /control/v1/setups/agents

Allow to start or stop the experiment for the configured agents

Payload to start

```json
{
    "action": "start"
}
```

Payload to stop

```json
{
    "action": "stop"
}
```

Response

```json
[
    {
        "location": "http://10.0.2.30:8080",
        "status": "done"
    },
    {
        "location": "http://10.0.2.31:8080",
        "status": "done"
    },
    {
        "location": "http://10.0.2.32:8080",
        "status": "done"
    },
    {
        "location": "http://10.0.2.33:8080",
        "status": "done"
    }
]
```

## Version automation

Each time that the command `./gradlew build` is executed,
the patch version is incremented. For example,:

- version before building `1.0.3`
- version after building `1.0.4`

This information is available in the GET /versions endpoint.
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