variable "configs" {
  description = "multiple ssm parameters"
  type        = map(object({
    name = string
    config_location = string
  }))
  nullable    = false
}
