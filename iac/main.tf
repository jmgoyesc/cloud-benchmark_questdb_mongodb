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
  region = var.region
}

# vpc
module "vpc" {
  source            = "./modules/vpc"
  sut_instance_type = var.sut_instance_type
  region            = var.region
}

# security groups
module "security_groups" {
  source            = "./modules/security_groups"
  sut_instance_type = var.sut_instance_type
  vpc_id            = module.vpc.vpc_id
}

module "cloudwatch" {
  source            = "./modules/cloudwatch"
  sut_instance_type = var.sut_instance_type
}

# ssm
module "ssm" {
  source            = "./modules/ssm"
  sut_instance_type = var.sut_instance_type
  configs = {
    sut_questdb_pg = {
      name             = "sut_questdb_pg"
      file_path        = "/home/ec2-user/.questdb/log/**"
      timestamp_format = "%Y-%m-%dT%H:%M:%S.%fZ"
    }
    sut_questdb_rest = {
      name             = "sut_questdb_rest"
      file_path        = "/home/ec2-user/.questdb/log/**"
      timestamp_format = "%Y-%m-%dT%H:%M:%S.%fZ"
    }
    sut_questdb_influx = {
      name             = "sut_questdb_influx"
      file_path        = "/home/ec2-user/.questdb/log/**"
      timestamp_format = "%Y-%m-%dT%H:%M:%S.%fZ"
    }
    sut_mongodb = {
      name             = "sut_mongodb"
      file_path        = "/var/log/mongodb/mongod.log"
      timestamp_format = "no-timestamp"
    }
    agent_questdb_pg = {
      name             = "agent_questdb_pg"
      file_path        = "/home/ec2-user/logs/spring.log"
      timestamp_format = "%Y-%m-%d %H:%M:%S"
    }
    agent_questdb_rest = {
      name             = "agent_questdb_rest"
      file_path        = "/home/ec2-user/logs/spring.log"
      timestamp_format = "%Y-%m-%d %H:%M:%S"
    }
    agent_questdb_influx = {
      name             = "agent_questdb_influx"
      file_path        = "/home/ec2-user/logs/spring.log"
      timestamp_format = "%Y-%m-%d %H:%M:%S"
    }
    agent_mongodb = {
      name             = "agent_mongodb"
      file_path        = "/home/ec2-user/logs/spring.log"
      timestamp_format = "%Y-%m-%d %H:%M:%S"
    }
    control = {
      name             = "control"
      file_path        = "/home/ec2-user/logs/spring.log"
      timestamp_format = "%Y-%m-%d %H:%M:%S"
    }
  }
}

# ec2s
module "ec2s" {
  source            = "./modules/ec2"
  sut_instance_type = var.sut_instance_type
  region            = var.region
  instances = {
    sut_questdb_pg = {
      machine_number = 20
      name           = "sut_questdb_pg"
      userdata_path  = "${path.module}/resources/user-data/sut_questdb_pg.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
      instance_type = var.sut_instance_type
    }
    sut_questdb_rest = {
      machine_number = 21
      name           = "sut_questdb_rest"
      userdata_path  = "${path.module}/resources/user-data/sut_questdb_rest.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
      instance_type = var.sut_instance_type
    }
    sut_questdb_influx = {
      machine_number = 22
      name           = "sut_questdb_influx"
      userdata_path  = "${path.module}/resources/user-data/sut_questdb_influx.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
      instance_type = var.sut_instance_type
    }
    sut_mongodb = {
      machine_number = 23
      name           = "sut_mongodb"
      userdata_path  = "${path.module}/resources/user-data/sut_mongodb.sh"
      subgroups = [
        module.security_groups.sg_mongodb,
        module.security_groups.sg_external
      ]
      instance_type = var.sut_instance_type
    }
    agent_questdb_pg = {
      machine_number = 30
      name           = "agent_questdb_pg"
      userdata_path  = "${path.module}/resources/user-data/agent_questdb_pg.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
      instance_type = var.agent_instance_type
    }
    agent_questdb_rest = {
      machine_number = 31
      name           = "agent_questdb_rest"
      userdata_path  = "${path.module}/resources/user-data/agent_questdb_rest.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
      instance_type = var.agent_instance_type
    }
    agent_questdb_influx = {
      machine_number = 32
      name           = "agent_questdb_influx"
      userdata_path  = "${path.module}/resources/user-data/agent_questdb_influx.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
      instance_type = var.agent_instance_type
    }
    agent_mongodb = {
      machine_number = 33
      name           = "agent_mongodb"
      userdata_path  = "${path.module}/resources/user-data/agent_mongodb.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
      instance_type = var.agent_instance_type
    }
    control = {
      machine_number = 10
      name           = "control"
      userdata_path  = "${path.module}/resources/user-data/control.sh"
      subgroups = [
        module.security_groups.sg_control,
        module.security_groups.sg_external
      ]
      instance_type = "t2.micro"
    }
  }
  subnet           = module.vpc.subnet_public_id
  instance_profile = module.cloudwatch.instance_profile_name
  depends_on       = [module.ssm]
}
