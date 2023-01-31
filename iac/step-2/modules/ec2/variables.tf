variable "instances" {
  description = "Create multiple instances, each one with last block of IPv4 and name"
  type = map(object({
    machine_number = number
    name           = string
  }))
  nullable = false
}

variable "userdata_path" {
  description = "Path to user-data.sh file"
  type        = string
  nullable    = false
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

variable "instance_type" {
  description = "Instance type to be created"
  type        = string
  nullable    = false
  default     = "t2.micro"
}

variable "subnet" {
  description = "aws subnet id"
  type        = string
  nullable    = false
}

variable "subgroups" {
  description = "aws subgroup ids"
  type        = list(string)
  nullable    = false
}

