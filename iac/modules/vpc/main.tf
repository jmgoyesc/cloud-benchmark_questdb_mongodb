# Vpc
resource "aws_vpc" "vpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    "Name"      = "${var.sut_instance_type}_vpc_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# Subnet - private
resource "aws_subnet" "private" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "${var.region}a"
  tags = {
    "Name"      = "${var.sut_instance_type}_private"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# Subnet - public
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "${var.region}a"
  map_public_ip_on_launch = true
  tags = {
    "Name"      = "${var.sut_instance_type}_public"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# Inernet Gateway
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    "Name"      = "${var.sut_instance_type}_igw cbs"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# Route table private
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    "Name"      = "${var.sut_instance_type}_private route_table_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# Route table public
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.vpc.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }
  tags = {
    "Name"      = "${var.sut_instance_type}_public route_table_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = var.sut_instance_type
  }
}

# Association between route table and private subnet
resource "aws_route_table_association" "private_association" {
  subnet_id      = aws_subnet.private.id
  route_table_id = aws_route_table.private.id
}

# Association between route table and public subnet
resource "aws_route_table_association" "public_association" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}
