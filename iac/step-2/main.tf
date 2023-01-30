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

# ami mongo
data "aws_ami" "mongo" {
  most_recent = true
  name_regex  = "^cbs_ami_mongodb"
  owners      = ["self"]
}

# ami questdb
data "aws_ami" "questdb" {
  most_recent = true
  name_regex  = "^cbs_ami_questdb"
  owners      = ["self"]
}

# ami agent
data "aws_ami" "agent" {
  most_recent = true
  name_regex  = "^cbs_ami_agent"
  owners      = ["self"]
}

# ami control
data "aws_ami" "control" {
  most_recent = true
  name_regex  = "^cbs_ami_control"
  owners      = ["self"]
}

# ec2 - questdb postgres - 20
resource "aws_instance" "questdb_pg" {
  count         = 1
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.questdb.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.20"
  key_name   = "cbs_key"

  user_data = <<EOF
  #!/bin/bash
  curl -LJ https://github.com/questdb/questdb/releases/download/6.6.1/questdb-6.6.1-rt-linux-amd64.tar.gz -o /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz
  tar -xvf /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz -C /home/ec2-user/

  sudo echo "[Unit]" >> /lib/systemd/system/questdb.service
  sudo echo "Description=QuestDB Service" >> /lib/systemd/system/questdb.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/questdb.service
  sudo echo "" >> /lib/systemd/system/questdb.service
  sudo echo "[Service]" >> /lib/systemd/system/questdb.service
  sudo echo "Type=forking" >> /lib/systemd/system/questdb.service
  sudo echo "Restart=always" >> /lib/systemd/system/questdb.service
  sudo echo "RestartSec=2" >> /lib/systemd/system/questdb.service
  sudo echo "ExecStart=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh start -d home/ec2-user/.questdb" >> /lib/systemd/system/questdb.service
  sudo echo "ExecStop=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh stop" >> /lib/systemd/system/questdb.service
  sudo echo "" >> /lib/systemd/system/questdb.service
  sudo echo "[Install]" >> /lib/systemd/system/questdb.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/questdb.service

  sudo chmod 644 /lib/systemd/system/questdb.service

  sudo systemctl daemon-reload
  sudo systemctl start questdb.service
  sudo systemctl enable questdb.service

  EOF

  tags = {
    "Name"      = "20 - sut - questdb postgres"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - questdb web - 21
resource "aws_instance" "questdb_web" {
  count         = 1
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.questdb.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.21"
  key_name   = "cbs_key"

  user_data = <<EOF
  #!/bin/bash
  curl -LJ https://github.com/questdb/questdb/releases/download/6.6.1/questdb-6.6.1-rt-linux-amd64.tar.gz -o /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz
  tar -xvf /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz -C /home/ec2-user/

  sudo echo "[Unit]" >> /lib/systemd/system/questdb.service
  sudo echo "Description=QuestDB Service" >> /lib/systemd/system/questdb.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/questdb.service
  sudo echo "" >> /lib/systemd/system/questdb.service
  sudo echo "[Service]" >> /lib/systemd/system/questdb.service
  sudo echo "Type=forking" >> /lib/systemd/system/questdb.service
  sudo echo "Restart=always" >> /lib/systemd/system/questdb.service
  sudo echo "RestartSec=2" >> /lib/systemd/system/questdb.service
  sudo echo "ExecStart=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh start -d home/ec2-user/.questdb" >> /lib/systemd/system/questdb.service
  sudo echo "ExecStop=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh stop" >> /lib/systemd/system/questdb.service
  sudo echo "" >> /lib/systemd/system/questdb.service
  sudo echo "[Install]" >> /lib/systemd/system/questdb.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/questdb.service

  sudo chmod 644 /lib/systemd/system/questdb.service

  sudo systemctl daemon-reload
  sudo systemctl start questdb.service
  sudo systemctl enable questdb.service

  EOF

  tags = {
    "Name"      = "21 - sut - questdb web"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - questdb ILP
resource "aws_instance" "questdb_ilp" {
  count         = 1
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.questdb.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.22"
  key_name   = "cbs_key"

  user_data = <<EOF
  #!/bin/bash
  curl -LJ https://github.com/questdb/questdb/releases/download/6.6.1/questdb-6.6.1-rt-linux-amd64.tar.gz -o /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz
  tar -xvf /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz -C /home/ec2-user/

  sudo echo "[Unit]" >> /lib/systemd/system/questdb.service
  sudo echo "Description=QuestDB Service" >> /lib/systemd/system/questdb.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/questdb.service
  sudo echo "" >> /lib/systemd/system/questdb.service
  sudo echo "[Service]" >> /lib/systemd/system/questdb.service
  sudo echo "Type=forking" >> /lib/systemd/system/questdb.service
  sudo echo "Restart=always" >> /lib/systemd/system/questdb.service
  sudo echo "RestartSec=2" >> /lib/systemd/system/questdb.service
  sudo echo "ExecStart=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh start -d home/ec2-user/.questdb" >> /lib/systemd/system/questdb.service
  sudo echo "ExecStop=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh stop" >> /lib/systemd/system/questdb.service
  sudo echo "" >> /lib/systemd/system/questdb.service
  sudo echo "[Install]" >> /lib/systemd/system/questdb.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/questdb.service

  sudo chmod 644 /lib/systemd/system/questdb.service

  sudo systemctl daemon-reload
  sudo systemctl start questdb.service
  sudo systemctl enable questdb.service

  EOF

  tags = {
    "Name"      = "22 - sut - questdb ilp"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - mongodb
resource "aws_instance" "mongodb" {
  count         = 1
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.mongodb.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.23"
  key_name   = "cbs_key"
  user_data  = <<EOF
  #!/bin/bash
  sudo yum -y install https://repo.mongodb.org/yum/amazon/2/mongodb-org/6.0/x86_64/RPMS/mongodb-org-server-6.0.3-1.amzn2.x86_64.rpm
  sudo yum -y install https://repo.mongodb.org/yum/amazon/2/mongodb-org/6.0/x86_64/RPMS/mongodb-mongosh-1.6.2.x86_64.rpm

  sudo sed -i 's/^.*bindIp: 127.0.0.1/  bindIpAll: true/' /etc/mongod.conf

  sudo systemctl daemon-reload
  sudo systemctl start mongod
  sudo systemctl status mongod
  sudo systemctl enable mongod

  EOF

  tags = {
    "Name"      = "23 - sut - mongodb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - agent questdb postgres
resource "aws_instance" "agent_questdb_pg" {
  count         = 1
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.agent.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.30"
  key_name   = "cbs_key"

  user_data = <<EOF
  #!/bin/bash

  sudo rpm --import https://yum.corretto.aws/corretto.key 
  sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
  sudo yum install -y java-19-amazon-corretto-devel

  curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/agent-questdb-rest/build/libs/agent-1.0.0.jar -o /home/ec2-user/agent-1.0.0.jar

  sudo echo "[Unit]" >> /lib/systemd/system/agent.service
  sudo echo "Description=CSB - Agent Service" >> /lib/systemd/system/agent.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Service]" >> /lib/systemd/system/agent.service
  sudo echo "Type=idle" >> /lib/systemd/system/agent.service
  sudo echo "ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/agent-1.0.0.jar --spring.profiles.active=ec2" >> /lib/systemd/system/agent.service
  sudo echo "Restart=on-failure" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Install]" >> /lib/systemd/system/agent.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/agent.service

  sudo chmod 644 /lib/systemd/system/agent.service

  sudo systemctl daemon-reload
  sudo systemctl start agent.service
  sudo systemctl enable agent.service

  EOF

  tags = {
    "Name"      = "30 - agent - questdb - postgres"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - agent questdb web
resource "aws_instance" "agent_questdb_web" {
  count         = 0
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.agent.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.31"
  key_name   = "cbs_key"

  user_data = <<EOF
  #!/bin/bash

  sudo rpm --import https://yum.corretto.aws/corretto.key 
  sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
  sudo yum install -y java-19-amazon-corretto-devel

  curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/agent-questdb-rest/build/libs/agent-1.0.0.jar -o /home/ec2-user/agent-1.0.0.jar

  sudo echo "[Unit]" >> /lib/systemd/system/agent.service
  sudo echo "Description=CSB - Agent Service" >> /lib/systemd/system/agent.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Service]" >> /lib/systemd/system/agent.service
  sudo echo "Type=idle" >> /lib/systemd/system/agent.service
  sudo echo "ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/agent-1.0.0.jar --spring.profiles.active=ec2" >> /lib/systemd/system/agent.service
  sudo echo "Restart=on-failure" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Install]" >> /lib/systemd/system/agent.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/agent.service

  sudo chmod 644 /lib/systemd/system/agent.service

  sudo systemctl daemon-reload
  sudo systemctl start agent.service
  sudo systemctl enable agent.service

  EOF
  tags = {
    "Name"      = "31 - agent - questdb - web"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - agent questdb ilp
resource "aws_instance" "agent_questdb_ilp" {
  count         = 0
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.agent.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.32"
  key_name   = "cbs_key"

  user_data = <<EOF
  #!/bin/bash

  sudo rpm --import https://yum.corretto.aws/corretto.key 
  sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
  sudo yum install -y java-19-amazon-corretto-devel

  curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/agent-questdb-rest/build/libs/agent-1.0.0.jar -o /home/ec2-user/agent-1.0.0.jar

  sudo echo "[Unit]" >> /lib/systemd/system/agent.service
  sudo echo "Description=CSB - Agent Service" >> /lib/systemd/system/agent.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Service]" >> /lib/systemd/system/agent.service
  sudo echo "Type=idle" >> /lib/systemd/system/agent.service
  sudo echo "ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/agent-1.0.0.jar --spring.profiles.active=ec2" >> /lib/systemd/system/agent.service
  sudo echo "Restart=on-failure" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Install]" >> /lib/systemd/system/agent.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/agent.service

  sudo chmod 644 /lib/systemd/system/agent.service

  sudo systemctl daemon-reload
  sudo systemctl start agent.service
  sudo systemctl enable agent.service

  EOF
  tags = {
    "Name"      = "32 - agent - questdb - ilp"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - agent mongo
resource "aws_instance" "agent_mongo" {
  count         = 0
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.agent.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.33"
  key_name   = "cbs_key"

  user_data = <<EOF
  #!/bin/bash

  sudo rpm --import https://yum.corretto.aws/corretto.key 
  sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
  sudo yum install -y java-19-amazon-corretto-devel

  curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/agent-questdb-rest/build/libs/agent-1.0.0.jar -o /home/ec2-user/agent-1.0.0.jar

  sudo echo "[Unit]" >> /lib/systemd/system/agent.service
  sudo echo "Description=CSB - Agent Service" >> /lib/systemd/system/agent.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Service]" >> /lib/systemd/system/agent.service
  sudo echo "Type=idle" >> /lib/systemd/system/agent.service
  sudo echo "ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/agent-1.0.0.jar --spring.profiles.active=ec2" >> /lib/systemd/system/agent.service
  sudo echo "Restart=on-failure" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Install]" >> /lib/systemd/system/agent.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/agent.service

  sudo chmod 644 /lib/systemd/system/agent.service

  sudo systemctl daemon-reload
  sudo systemctl start agent.service
  sudo systemctl enable agent.service

  EOF
  tags = {
    "Name"      = "33 - agent - mongo"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - control
resource "aws_instance" "control" {
  count         = 1
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  subnet_id     = aws_subnet.public.id
  vpc_security_group_ids = [
    aws_security_group.control.id,
    aws_security_group.external.id
  ]
  private_ip = "10.0.2.10"
  key_name   = "cbs_key"

  user_data = <<EOF
  #!/bin/bash

  sudo rpm --import https://yum.corretto.aws/corretto.key 
  sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
  sudo yum install -y java-19-amazon-corretto-devel

  curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/control/build/libs/control-1.0.0.jar -o /home/ec2-user/control-1.0.0.jar

  sudo echo "[Unit]" >> /lib/systemd/system/control.service
  sudo echo "Description=CSB - Control Service" >> /lib/systemd/system/control.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/control.service
  sudo echo "" >> /lib/systemd/system/control.service
  sudo echo "[Service]" >> /lib/systemd/system/control.service
  sudo echo "Type=idle" >> /lib/systemd/system/control.service
  sudo echo "ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/control-1.0.0.jar --spring.profiles.active=ec2" >> /lib/systemd/system/control.service
  sudo echo "Restart=on-failure" >> /lib/systemd/system/control.service
  sudo echo "" >> /lib/systemd/system/control.service
  sudo echo "[Install]" >> /lib/systemd/system/control.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/control.service

  sudo chmod 644 /lib/systemd/system/control.service

  sudo systemctl daemon-reload
  sudo systemctl start control.service
  sudo systemctl enable control.service

  EOF
  tags = {
    "Name"      = "10 - control"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}
