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


# ec2 - questdb
resource "aws_instance" "questdb" {
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  tags = {
    "Name"      = "ami - questdb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# ec2 - mongodb
resource "aws_instance" "mongodb" {
  ami           = "ami-0a261c0e5f51090b1"
  instance_type = "t2.micro"
  tags = {
    "Name"      = "ami - mongodb"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}