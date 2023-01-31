# Vpc
resource "aws_vpc" "vpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    "Name"      = "vpc_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Subnet - private
resource "aws_subnet" "private" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = "10.0.1.0/24"
  availability_zone = "eu-central-1a"
  tags = {
    "Name"      = "private"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Subnet - public
resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "eu-central-1a"
  map_public_ip_on_launch = true
  tags = {
    "Name"      = "public"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Inernet Gateway
resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    "Name"      = "igw cbs"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
  }
}

# Route table private
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.vpc.id
  tags = {
    "Name"      = "private route_table_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
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
    "Name"      = "public route_table_cbs"
    "project"   = "cloud benchmark"
    "worskapce" = "default"
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