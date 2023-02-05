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

module "cloudwatch" {
  source = "./modules/cloudwatch"
}

# ssm
module "ssm" {
  source = "./modules/ssm"
  configs = {
    sut_questdb_pg = {
      name            = "sut_questdb_pg"
      config_location = "${path.module}/resources/cloudwatch/sut_questdb_pg/amazon-cloudwatch-agent.json"
    }
    sut_questdb_rest = {
      name            = "sut_questdb_rest"
      config_location = "${path.module}/resources/cloudwatch/sut_questdb_rest/amazon-cloudwatch-agent.json"
    }
    sut_questdb_influx = {
      name            = "sut_questdb_influx"
      config_location = "${path.module}/resources/cloudwatch/sut_questdb_influx/amazon-cloudwatch-agent.json"
    }
    sut_mongodb = {
      name            = "sut_mongodb"
      config_location = "${path.module}/resources/cloudwatch/sut_mongodb/amazon-cloudwatch-agent.json"
    }
    agent_questdb_pg = {
      name            = "agent_questdb_pg"
      config_location = "${path.module}/resources/cloudwatch/agent_questdb_pg/amazon-cloudwatch-agent.json"
    }
    agent_questdb_rest = {
      name            = "agent_questdb_rest"
      config_location = "${path.module}/resources/cloudwatch/agent_questdb_rest/amazon-cloudwatch-agent.json"
    }
    agent_questdb_influx = {
      name            = "agent_questdb_influx"
      config_location = "${path.module}/resources/cloudwatch/agent_questdb_influx/amazon-cloudwatch-agent.json"
    }
    agent_mongodb = {
      name            = "agent_mongodb"
      config_location = "${path.module}/resources/cloudwatch/agent_mongodb/amazon-cloudwatch-agent.json"
    }
    control = {
      name            = "control"
      config_location = "${path.module}/resources/cloudwatch/control/amazon-cloudwatch-agent.json"
    }
  }
}

# ec2s
module "ec2s" {
  source = "./modules/ec2"
  instances = {
    sut_questdb_pg = {
      machine_number = 20
      name           = "sut_questdb_pg"
      userdata_path  = "${path.module}/resources/user-data/sut_questdb_pg.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
    }
    sut_questdb_rest = {
      machine_number = 21
      name           = "sut_questdb_rest"
      userdata_path  = "${path.module}/resources/user-data/sut_questdb_rest.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
    }
    sut_questdb_influx = {
      machine_number = 22
      name           = "sut_questdb_influx"
      userdata_path  = "${path.module}/resources/user-data/sut_questdb_influx.sh"
      subgroups = [
        module.security_groups.sg_questdb,
        module.security_groups.sg_external
      ]
    }
    sut_mongodb = {
      machine_number = 23
      name           = "sut_mongodb"
      userdata_path  = "${path.module}/resources/user-data/sut_mongodb.sh"
      subgroups = [
        module.security_groups.sg_mongodb,
        module.security_groups.sg_external
      ]
    }
    agent_questdb_pg = {
      machine_number = 30
      name           = "agent_questdb_pg"
      userdata_path  = "${path.module}/resources/user-data/agent_questdb_pg.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
    }
    agent_questdb_rest = {
      machine_number = 31
      name           = "agent_questdb_rest"
      userdata_path  = "${path.module}/resources/user-data/agent_questdb_rest.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
    }
    agent_questdb_influx = {
      machine_number = 32
      name           = "agent_questdb_influx"
      userdata_path  = "${path.module}/resources/user-data/agent_questdb_influx.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
    }
    agent_mongodb = {
      machine_number = 33
      name           = "agent_mongodb"
      userdata_path  = "${path.module}/resources/user-data/agent_mongodb.sh"
      subgroups = [
        module.security_groups.sg_agent,
        module.security_groups.sg_external
      ]
    }
    control = {
      machine_number = 10
      name           = "control"
      userdata_path  = "${path.module}/resources/user-data/control.sh"
      subgroups = [
        module.security_groups.sg_control,
        module.security_groups.sg_external
      ]
    }
  }
  subnet           = module.vpc.subnet_public_id
  instance_profile = module.cloudwatch.instance_profile_name
  depends_on       = [module.ssm]
}
