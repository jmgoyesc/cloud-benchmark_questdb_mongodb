# IaC - Infrastructure as Code

This project creates the necessary infrastructure to run the benchmark.

Cloud provider: Amazon Web Services (AWS)
Region. eu-europe (Frankfurt)

## Getting started

Below there are the steps to create the infrastructure for the benchmark.
It is divided into 2 steps in order to simplify the archicture and restrict the permissions.
The SUT system does not need access to internet, but the instalation of the SUT needs.
Therefore, create the AMI and use it later, avoid the necessity to configure either NAT instance, 
NAT gateway or Egress only Internet Gateway (only for IPv6).

### Step 1 - AMI creation

Initiate an EC2 from amazon linux, run the user data scripts and take a snapshot.
Exeucte these steps for MongoDB and QuestDB

1. Create EC2 instance in default VPC in public subnet with public IP address and ssh access
1. Take snapshop of EBS volume
1. Create AMI
1. Destroy EC2 instance

Run the following scripts to create and start EC2 instances

```shell
cd step-1
terraform init
terraform plan
terraform apply
```

To destroy the infrastructure, execute this (assuming inside of subfolder /step-1)

```shell
terraform destroy
```

### Step 2 - Benchmark infrastructure

Create the infrastructure for the experiment using AMIs from step 1

Execute the following commands to create the infrastructure for the benchmark

```shell
cd step-2
terraform init
terraform plan
terraform apply
```

After the benchmark is completed, then destroy the infrastructure with the following command

```shell
terraform destroy
```

## AWS cost analysis

Table for service

EC2 instance type per hour
Internet Gateway
NAT gateway $ 0.045 per hour

## Tool versions

Install the tools using homebrew in mac

```shell
brew install terraform
brew install awscli
```

terraform 1.3.7
aws provider 4.49.0
aws command line (aws-cli) 2.9.9

