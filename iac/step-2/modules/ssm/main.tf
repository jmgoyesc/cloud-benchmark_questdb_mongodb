# ssm parameter cloudwatch config
resource "aws_ssm_parameter" "configs" {
  for_each = var.configs

  name        = each.value.name
  description = "CloudWatch config to send metrics and logs from ec2 ${each.value.name}"
  type        = "String"
  value = file(each.value.config_location)
}
