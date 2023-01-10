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

resource "aws_default_vpc" "default" {
  tags = {
    Name = "Default VPC"
  }
}

# secutiry group mongo_quest
resource "aws_security_group" "mongo_quest" {
  name        = "mongo_quest"
  description = "mongo quest"
  vpc_id      = aws_default_vpc.default.id

  ingress {
    description = "questdb postgres"
    from_port   = 8812
    to_port     = 8812
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "questdb web/rest api"
    from_port   = 9000
    to_port     = 9000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    description = "questdb influx line protocol"
    from_port   = 9009
    to_port     = 9009
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "MongoDB"
    from_port   = 27017
    to_port     = 27017
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
    "Name"      = "mongo quest"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - questdb
resource "aws_instance" "questdb" {
  ami                    = "ami-0a261c0e5f51090b1"
  instance_type          = "t2.micro"
  vpc_security_group_ids = [aws_security_group.mongo_quest.id]

  user_data = <<EOF
#!/bin/bash
curl -LJO https://github.com/questdb/questdb/releases/download/6.6.1/questdb-6.6.1-rt-linux-amd64.tar.gz
tar -xvf questdb-6.6.1-rt-linux-amd64.tar.gz
cd questdb-6.6.1-rt-linux-amd64/bin
./questdb.sh start
EOF

  tags = {
    "Name"      = "ami - questdb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - mongodb
resource "aws_instance" "mongodb" {
  ami                    = "ami-0a261c0e5f51090b1"
  instance_type          = "t2.micro"
  vpc_security_group_ids = [aws_security_group.mongo_quest.id]

  user_data = <<EOF
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
    "Name"      = "ami - mongodb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}
