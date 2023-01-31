terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.49.0"
    }
  }
}

# Configure the AWS Provider
provider "aws" {
  region = "eu-central-1"
}

##################################################################
## VPC

# Vpc
resource "aws_vpc" "vpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    "Name"      = "vpc_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Subnet - private
resource "aws_subnet" "private" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "eu-central-1a"
  tags = {
    "Name"      = "private"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Subnet - public
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "eu-central-1a"
  map_public_ip_on_launch = true
  tags = {
    "Name"      = "public"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Inernet Gateway
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    "Name"      = "igw cbs"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Route table private
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    "Name"      = "private route_table_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Route table public
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = {
    "Name"      = "public route_table_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Association between route table and private subnet
resource "aws_route_table_association" "private_association" {
  subnet_id      = aws_subnet.private.id
  route_table_id = aws_route_table.private.id
}

# Association between route table and public subnet
resource "aws_route_table_association" "public_association" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

##################################################################
## EC2

# security group external
resource "aws_security_group" "external" {
  name        = "external"
  description = "external access"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    description = "ssh"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "http-8080"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "mongo"
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "questdb"
    from_port   = 9000
    to_port     = 9000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    "Name"      = "external access"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}


# security group control
resource "aws_security_group" "control" {
  name        = "control"
  description = "control - Experiment controller"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    description = "rest"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    "Name"      = "control"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# security group agent
resource "aws_security_group" "agent" {
  name        = "agent"
  description = "Agent - Load Generator"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    description     = "rest"
    from_port       = 8080
    to_port         = 8080
    protocol        = "tcp"
    security_groups = [aws_security_group.control.id]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }

  tags = {
    "Name"      = "agent"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# secutiry group questdb
resource "aws_security_group" "questdb" {
  name        = "questdb"
  description = "System Under Test - QuestDB"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    description     = "postgres"
    from_port       = 8812
    to_port         = 8812
    protocol        = "tcp"
    security_groups = [aws_security_group.agent.id]
  }
  ingress {
    description     = "web/rest api"
    from_port       = 9000
    to_port         = 9000
    protocol        = "tcp"
    security_groups = [aws_security_group.agent.id, aws_security_group.control.id]
  }
  ingress {
    description     = "influx line protocol"
    from_port       = 9009
    to_port         = 9009
    protocol        = "tcp"
    security_groups = [aws_security_group.agent.id]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
  tags = {
    "Name"      = "sut - questdb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# security group mongodb
resource "aws_security_group" "mongodb" {
  name        = "mongodb"
  description = "System Under Test - Mongo DB"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    description     = "mongo"
    from_port       = 27017
    to_port         = 27017
    protocol        = "tcp"
    security_groups = [aws_security_group.agent.id, aws_security_group.control.id]
  }

  egress {
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
    ipv6_cidr_blocks = ["::/0"]
  }
  tags = {
    "Name"      = "sut - mongodb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - questdb
module "ec2_questdb" {
  source = "./modules/ec2"
  instances = {
    a = {
      machine_number = 20
      name = "sut - questdb postgres"
    }
    b = {
      machine_number = 21
      name = "sut - questdb web"
    }
    c = {
      machine_number = 22
      name = "sut - questdb ilp"
    }
  }
  userdata_path = "${path.module}/user-data/questdb.sh"
  subnet = aws_subnet.public.id
  subgroups = [
    aws_security_group.questdb.id,
    aws_security_group.external.id
  ]
}

# ec2 - mongodb
module "ec2_mongodb" {
  source = "./modules/ec2"
  instances = {
    a = {
      machine_number = 23
      name = "sut - mongodb"
    }
  }
  userdata_path = "${path.module}/user-data/mongodb.sh"
  subnet = aws_subnet.public.id
  subgroups = [
    aws_security_group.mongodb.id,
    aws_security_group.external.id
  ]
}

# ec2 - agents
module "ec2_agents" {
  source = "./modules/ec2"
  instances = {
    a = {
      machine_number = 30
      name         = "agent - questdb - postgres"
    }
    b = {
      machine_number = 31
      name         = "agent - questdb - web"
    }
    c = {
      machine_number = 32
      name         = "agent - questdb - ilp"
    }
    d = {
      machine_number = 33
      name         = "agent - mongo"
    }
  }
  userdata_path = "${path.module}/user-data/agent.sh"
  subnet = aws_subnet.public.id
  subgroups = [
    aws_security_group.agent.id,
    aws_security_group.external.id
  ]
}

# ec2 - control
module "ec2_control" {
  source = "./modules/ec2"
  instances = {
    a = {
      machine_number = 10
      name = "control"
    }
  }
  userdata_path = "${path.module}/user-data/control.sh"
  subnet = aws_subnet.public.id
  subgroups = [
    aws_security_group.control.id,
    aws_security_group.external.id
  ]
}
