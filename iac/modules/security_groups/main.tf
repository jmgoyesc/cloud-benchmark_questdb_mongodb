
# security group external
resource "aws_security_group" "external" {
  name        = "${var.sut_instance_type}_external"
  description = "external access"
  vpc_id      = var.vpc_id

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
    "Name"      = "${var.sut_instance_type}_external access"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# security group control
resource "aws_security_group" "control" {
  name        = "${var.sut_instance_type}_control"
  description = "control - Experiment controller"
  vpc_id      = var.vpc_id

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
    "Name"      = "${var.sut_instance_type}_control"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# security group agent
resource "aws_security_group" "agent" {
  name        = "${var.sut_instance_type}_agent"
  description = "Agent - Load Generator"
  vpc_id      = var.vpc_id

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
    "Name"      = "${var.sut_instance_type}_agent"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# secutiry group questdb
resource "aws_security_group" "questdb" {
  name        = "${var.sut_instance_type}_questdb"
  description = "System Under Test - QuestDB"
  vpc_id      = var.vpc_id

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
    "Name"      = "${var.sut_instance_type}_sut - questdb"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# security group mongodb
resource "aws_security_group" "mongodb" {
  name        = "${var.sut_instance_type}_mongodb"
  description = "System Under Test - Mongo DB"
  vpc_id      = var.vpc_id

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
    "Name"      = "${var.sut_instance_type}_sut - mongodb"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}