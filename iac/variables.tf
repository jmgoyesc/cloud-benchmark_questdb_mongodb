variable "agent_instance_type" {
  description = "Agent instance type to be created"
  type        = string
  nullable    = false
}

variable "sut_instance_type" {
  description = "SUT instance type to be created"
  type        = string
  nullable    = false
}

variable "region" {
  type = string
  default = "eu-central-1"
}