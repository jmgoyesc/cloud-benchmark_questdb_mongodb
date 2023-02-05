# ssm parameter cloudwatch config - agent
resource "aws_ssm_parameter" "agent" {
  name        = "agent-cloudwatch-config"
  description = "CloudWatch config to send metrics and logs from ec2 agent"
  type        = "String"
  value = file("${path.module}/cloudwatch/agent/amazon-cloudwatch-agent.json")
}

# ssm parameter cloudwatch config - control
resource "aws_ssm_parameter" "control" {
  name        = "control-cloudwatch-config"
  description = "CloudWatch config to send metrics and logs from ec2 control"
  type        = "String"
  value = file("${path.module}/cloudwatch/control/amazon-cloudwatch-agent.json")
}

# ssm parameter cloudwatch config - mongodb
resource "aws_ssm_parameter" "mongodb" {
  name        = "mongodb-cloudwatch-config"
  description = "CloudWatch config to send metrics and logs from ec2 mongodb"
  type        = "String"
  value = file("${path.module}/cloudwatch/mongodb/amazon-cloudwatch-agent.json")
}

# ssm parameter cloudwatch config - questdb
resource "aws_ssm_parameter" "questdb" {
  name        = "questdb-cloudwatch-config"
  description = "CloudWatch config to send metrics and logs from ec2 questdb"
  type        = "String"
  value = file("${path.module}/cloudwatch/questdb/amazon-cloudwatch-agent.json")
}
