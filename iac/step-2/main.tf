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

# vpc
module "vpc" {
  source = "./modules/vpc"
}

# security groups
module "security_groups" {
  source = "./modules/security_groups"
  vpc_id = module.vpc.vpc_id
}

# ec2s
module "ec2s" {
  source = "./modules/ec2"
  instances = {
    sut_questdb_pg = {
      machine_number = 20
      name           = "sut - questdb postgres"
      userdata_path  = "${path.module}/user-data/questdb.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
    }
    sut_questdb_web = {
      machine_number = 21
      name           = "sut - questdb web"
      userdata_path  = "${path.module}/user-data/questdb.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
    }
    sut_questdb_ilp = {
      machine_number = 22
      name           = "sut - questdb ilp"
      userdata_path  = "${path.module}/user-data/questdb.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
    }
    sut_mongodb = {
      machine_number = 23
      name           = "sut - mongodb"
      userdata_path  = "${path.module}/user-data/mongodb.sh"
      subgroups = [
        module.security_groups.sg_mongodb,
        module.security_groups.sg_external
      ]
    }
    agent_questdb_pg = {
      machine_number = 30
      name           = "agent - questdb - postgres"
      userdata_path  = "${path.module}/user-data/agent.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
    }
    agent_questdb_web = {
      machine_number = 31
      name           = "agent - questdb - web"
      userdata_path  = "${path.module}/user-data/agent.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
    }
    agent_questdb_ilp = {
      machine_number = 32
      name           = "agent - questdb - ilp"
      userdata_path  = "${path.module}/user-data/agent.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
    }
    agent_mongodb = {
      machine_number = 33
      name           = "agent - mongo"
      userdata_path  = "${path.module}/user-data/agent.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
    }
    control = {
      machine_number = 10
      name           = "control"
      userdata_path  = "${path.module}/user-data/control.sh"
      subgroups = [
        module.security_groups.sg_control,
        module.security_groups.sg_external
      ]
    }
  }
  subnet = module.vpc.subnet_public_id
}
