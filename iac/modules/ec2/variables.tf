variable "instances" {
  description = "Create multiple instances, each one with last block of IPv4 and name"
  type = map(object({
    machine_number = number
    name           = string
    userdata_path  = string
    subgroups      = list(string)
    instance_type  = string
  }))
  nullable = false
}

variable "sut_instance_type" {
  type = string
  description = "Use to find the ssm parameter"
}

variable "region" {
  type = string
}

variable "instance_profile" {
  description = "Instance profile name to assume a role"
  type        = string
}

variable "key_name" {
  description = "Key Pair name"
  type        = string
  nullable    = false
  default     = "cbs_key"
}

variable "ami" {
  description = "AMI use to create ec2 machine"
  type        = string
  nullable    = false
  default     = "ami-0a261c0e5f51090b1"
}

variable "subnet" {
  description = "aws subnet id"
  type        = string
  nullable    = false
}
