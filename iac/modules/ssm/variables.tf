variable "configs" {
  description = "multiple ssm parameters"
  type = map(object({
    name              = string
    file_path         = string
    timestamp_format  = string
  }))
  nullable = false
}

variable "sut_instance_type" {
    type = string
    description = "Use for the log group path. Filter by different benchmarks"
}