# Cloud benchmark between QuestDB and MongoDB Time Series

Contains the information to allow the comprarison between QuestDB and MongoDB.
QuestDB allows to insert data with 3 different strategies:

- Inlfux Lline Protocol (ILP)
- Postgres connector
- Rest

The purpose of the project is to compare the insertation rate among MongoDB Time Series and the 3 strategies offered
by QuestDB

## Research questions

This projects plans to address the following research questions.

RQ1. What is the maximum write throughput between QuestDB and MongoDB Time Series?
RQ2. How does write throughput vary depending on RAM and vCPU?

## Structure

The project is compose by 3 subprojects:

1. [iac - Infrastructure as Code](./iac/README.md)
2. [control - Expriment orchestrator](./control/README.md)
3. [agent - Load generator](./agent/README.md)

## How to run the experiment

It needs to follow these short steps:

1. Follow the ["Getting started"](./iac/README.md#getting-started) section from iac.
2. In AWS console, find the EC2 machine 10 - Control, and get the public IP address.
3. Install postman collections and environment for the control projec. Located in ./control/postman
4. Using postman, select AWS environment.
5. Add the public IP addres to the variable "aws.ip" in "aws" environment in postman.
6. Execute the endpoint to configure the tables (postman / control / aws / tables / aws:create )
7. Execute the endpoint to configure the agents (postman / control / aws / agents / aws:create )
8. Check the version of the control and agent using the endpoint (postman / control / versions )
8. Start the experiment by executing the endpoint (postman / control / start )
9. At the end, end the experiment by executing the endpoint (postman / control / stop )
10. Check a summary of the results by executing the endpoint (postman / control / experiments)
11. Get the logs and metrics from CloudWatch
