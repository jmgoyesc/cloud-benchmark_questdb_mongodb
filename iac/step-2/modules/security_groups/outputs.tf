output "sg_external" {
  value = aws_security_group.external.id
}

output "sg_control" {
  value = aws_security_group.control.id
}

output "sg_agent" {
  value = aws_security_group.agent.id
}

output "sg_questdb" {
  value = aws_security_group.questdb.id
}

output "sg_mongodb" {
  value = aws_security_group.mongodb.id
}