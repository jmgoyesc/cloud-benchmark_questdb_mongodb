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
    description = "ssh"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
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
  count                  = 1
  ami                    = "ami-0a261c0e5f51090b1"
  instance_type          = "t2.micro"
  vpc_security_group_ids = [aws_security_group.mongo_quest.id]
  key_name               = "benchamr_key"

  user_data = <<EOF
  #!/bin/bash
  curl -LJ https://github.com/questdb/questdb/releases/download/6.6.1/questdb-6.6.1-rt-linux-amd64.tar.gz -o /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz
  tar -xvf /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz -C /home/ec2-user/

  sudo echo "[Unit]" >> /lib/systemd/system/questdb.service
  sudo echo "Description=QuestDB Service" >> /lib/systemd/system/questdb.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/questdb.service
  sudo echo "" >> /lib/systemd/system/questdb.service
  sudo echo "[Service]" >> /lib/systemd/system/questdb.service
  sudo echo "Type=idle" >> /lib/systemd/system/questdb.service
  sudo echo "ExecStart=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh start" >> /lib/systemd/system/questdb.service
  sudo echo "Restart=on-failure" >> /lib/systemd/system/questdb.service
  sudo echo "" >> /lib/systemd/system/questdb.service
  sudo echo "[Install]" >> /lib/systemd/system/questdb.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/questdb.service

  sudo chmod 644 /lib/systemd/system/questdb.service

  sudo systemctl daemon-reload
  sudo systemctl start questdb.service
  sudo systemctl enable questdb.service

  EOF

  tags = {
    "Name"      = "ami - questdb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - mongodb
resource "aws_instance" "mongodb" {
  count                  = 1
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

# ec2 - agent
resource "aws_instance" "agent" {
  count                  = 1
  ami                    = "ami-0a261c0e5f51090b1"
  instance_type          = "t2.micro"
  vpc_security_group_ids = [aws_security_group.mongo_quest.id]
  key_name               = "benchamr_key"

  user_data = <<EOF
  #!/bin/bash

  sudo rpm --import https://yum.corretto.aws/corretto.key 
  sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
  sudo yum install -y java-19-amazon-corretto-devel

  sudo echo "[Unit]" >> /lib/systemd/system/agent.service
  sudo echo "Description=CSB - Agent Service" >> /lib/systemd/system/agent.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Service]" >> /lib/systemd/system/agent.service
  sudo echo "Type=idle" >> /lib/systemd/system/agent.service
  sudo echo "ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/agent-questdb-rest-1.0.0.jar" >> /lib/systemd/system/agent.service
  sudo echo "Restart=on-failure" >> /lib/systemd/system/agent.service
  sudo echo "" >> /lib/systemd/system/agent.service
  sudo echo "[Install]" >> /lib/systemd/system/agent.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/agent.service

  sudo chmod 644 /lib/systemd/system/agent.service

  # sudo systemctl daemon-reload
  # sudo systemctl start agent.service
  # sudo systemctl enable agent.service

  EOF

  tags = {
    "Name"      = "ami - agent"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - control
resource "aws_instance" "control" {
  count                  = 1
  ami                    = "ami-0a261c0e5f51090b1"
  instance_type          = "t2.micro"
  vpc_security_group_ids = [aws_security_group.mongo_quest.id]
  key_name               = "benchamr_key"

  user_data = <<EOF
  #!/bin/bash

  sudo rpm --import https://yum.corretto.aws/corretto.key 
  sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
  sudo yum install -y java-19-amazon-corretto-devel

  sudo echo "[Unit]" >> /lib/systemd/system/control.service
  sudo echo "Description=CSB - Control Service" >> /lib/systemd/system/control.service
  sudo echo "After=multi-user.target" >> /lib/systemd/system/control.service
  sudo echo "" >> /lib/systemd/system/control.service
  sudo echo "[Service]" >> /lib/systemd/system/control.service
  sudo echo "Type=idle" >> /lib/systemd/system/control.service
  sudo echo "ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/control-1.0.0.jar" >> /lib/systemd/system/control.service
  sudo echo "Restart=on-failure" >> /lib/systemd/system/control.service
  sudo echo "" >> /lib/systemd/system/control.service
  sudo echo "[Install]" >> /lib/systemd/system/control.service
  sudo echo "WantedBy=multi-user.target" >> /lib/systemd/system/control.service

  sudo chmod 644 /lib/systemd/system/control.service

  # sudo systemctl daemon-reload
  # sudo systemctl start control.service
  # sudo systemctl enable control.service

  EOF

  tags = {
    "Name"      = "ami - control"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}
