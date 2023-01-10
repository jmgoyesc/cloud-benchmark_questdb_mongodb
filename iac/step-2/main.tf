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

# Placement group (only for non burstable instances. sut: i3.large - agent: c6a.large)
# resource "aws_placement_group" "placement_group" {
#   name = "cluster_${terraform.workspace}"
#   strategy = "cluster"
#   tags = {
#     "project"   = "cloud benchmark"
#     "worskapce" = "default"
#   }
# }

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
    security_groups = [aws_security_group.agent.id]
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
    "Name"      = "sut - mongodb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ami mongo
data "aws_ami" "mongo" {
  most_recent      = true
  name_regex       = "^cbs_ami_mongodb"
  owners           = ["self"]
}

# ami questdb
data "aws_ami" "questdb" {
  most_recent      = true
  name_regex       = "^cbs_ami_questdb"
  owners           = ["self"]
}

# ec2 - questdb postgres
resource "aws_instance" "questdb_pg" {
  ami           = data.aws_ami.questdb.id
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private.id
  vpc_security_group_ids = [
    aws_security_group.questdb.id
  ]
  private_ip = "10.0.1.20"
  tags = {
    "Name"      = "20 - sut - questdb postgres"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - questdb web
resource "aws_instance" "questdb_web" {
  ami           = data.aws_ami.questdb.id
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private.id
  vpc_security_group_ids = [
    aws_security_group.questdb.id
  ]
  private_ip = "10.0.1.21"
  tags = {
    "Name"      = "21 - sut - questdb web"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - questdb ILP
resource "aws_instance" "questdb_ilp" {
  ami           = data.aws_ami.questdb.id
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private.id
  vpc_security_group_ids = [
    aws_security_group.questdb.id
  ]
  private_ip = "10.0.1.22"
  tags = {
    "Name"      = "22 - sut - questdb ilp"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - mongodb
resource "aws_instance" "mongodb" {
  ami           = data.aws_ami.mongo.id
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private.id
  vpc_security_group_ids = [
    aws_security_group.mongodb.id
  ]
  private_ip = "10.0.1.23"
  tags = {
    "Name"      = "23 - sut - mongodb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - agent questdb postgres
resource "aws_instance" "agent_questdb_pg" {
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private.id
  vpc_security_group_ids = [
    aws_security_group.agent.id
  ]
  private_ip = "10.0.1.30"
  tags = {
    "Name"      = "30 - agent - questdb - postgres"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - agent questdb web
resource "aws_instance" "agent_questdb_web" {
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private.id
  vpc_security_group_ids = [
    aws_security_group.agent.id
  ]
  private_ip = "10.0.1.31"
  tags = {
    "Name"      = "31 - agent - questdb - web"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - agent questdb ilp
resource "aws_instance" "agent_questdb_ilp" {
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private.id
  vpc_security_group_ids = [
    aws_security_group.agent.id
  ]
  private_ip = "10.0.1.32"
  tags = {
    "Name"      = "32 - agent - questdb - ilp"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - agent mongo
resource "aws_instance" "agent_mongo" {
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.private.id
  vpc_security_group_ids = [
    aws_security_group.agent.id
  ]
  private_ip = "10.0.1.33"
  tags = {
    "Name"      = "33 - agent - mongo"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - control
resource "aws_instance" "control" {
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.control.id
  ]
  private_ip = "10.0.2.10"
  tags = {
    "Name"      = "10 - control"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}
