resource "aws_instance" "cbs_vm" {
  for_each = var.instances

  ami                    = var.ami
  instance_type          = var.instance_type
  subnet_id              = var.subnet
  vpc_security_group_ids = each.value.subgroups
  private_ip             = "10.0.2.${each.value.machine_number}"
  key_name               = var.key_name

  user_data = file("${each.value.userdata_path}")
  iam_instance_profile = var.instance_profile

  tags = {
    "Name"      = "${each.value.machine_number} - ${each.value.name}"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}
