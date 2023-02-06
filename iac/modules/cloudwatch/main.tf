# iam policy
resource "aws_iam_policy" "logs_put_retention" {
  name = "${var.sut_instance_type}_CloudWatchPutRetentionPolicy"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = [
          "logs:PutRetentionPolicy",
        ]
        Effect   = "Allow"
        Resource = "*"
      },
    ]
  })

  tags = {
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# iam role
resource "aws_iam_role" "CloudWatchAgentServerRole" {
  name = "${var.sut_instance_type}_CloudWatchAgentServerRole"
  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "sts:AssumeRole"
        ],
        "Principal" : {
          "Service" : [
            "ec2.amazonaws.com"
          ]
        }
      }
    ]
  })
  managed_policy_arns = [
    "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy",
    "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore",
    aws_iam_policy.logs_put_retention.arn
  ]

  tags = {
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# instance profile to help to assume role
resource "aws_iam_instance_profile" "ec2_cloudwatch_profile" {
  name = "${var.sut_instance_type}_ec2_cloudwatch_profile"
  role = aws_iam_role.CloudWatchAgentServerRole.name
}