# ssm parameter cloudwatch config
resource "aws_ssm_parameter" "configs" {
  for_each = var.configs

  name        = "/${var.sut_instance_type}/${each.value.name}"
  description = "CloudWatch config to send metrics and logs from ec2 ${each.value.name}"
  type        = "String"
  value = each.value.timestamp_format != "no-timestamp" ? templatefile("${path.module}/config.json.tftpl", {
    name              = each.value.name
    file_path         = each.value.file_path
    sut_instance_type = var.sut_instance_type
    timestamp_format  = each.value.timestamp_format
    }) : templatefile("${path.module}/config_no_timestamp.json.tftpl", {
    name              = each.value.name
    file_path         = each.value.file_path
    sut_instance_type = var.sut_instance_type
  })
}
