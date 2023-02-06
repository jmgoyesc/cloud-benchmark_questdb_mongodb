# IaC - Infrastructure as Code

This project creates the necessary infrastructure to run the benchmark.

Cloud provider: Amazon Web Services (AWS)
Region. eu-europe (Frankfurt)

## Workspaces

These are the available workspaces.

_1cpu_1ram (initial) t2.micro
_2cpu_1ram (more cpu) t4g.micro
_1cpu_2ram (more ram) t2.small

To use a workspace:
1. Exeucte `terraform init``
2. Create first workspace using `terraform workspace new _1cpu_1ram`
3. Create second workspace using `terraform workspace new _2cpu_1ram`
4. Create third workspace using `terraform workspace new _1cpu_2ram`
5. Select the desired workspace using `terraform workspace select _1cpu_1ram`

Note: Fixed Performance instance families (e.g. M6, C6, and R6) 

## Getting started

1. Configure an AWS account
2. Generate an Access Key for terraform with the following policies attached: AmazonEC2FullAccess, AmazonSSMFullAccess, AmazonVPCFullAccess, IAMFullAccess
3. Create a Key Pair in EC2 with this name "cbs_key"
4. Install the tools in the command line (terraform, awscli)
5. Prepare the workspaces and select one (previous section)
4. In the default workspace, execute `terraform apply'
5. Confirm the request in the command line. Type "yes"
6. After the experiment is finished, execute `terraform destroy`

## Requirements

Install the tools using homebrew in mac

```shell
brew install terraform
brew install awscli
```

terraform 1.3.7
aws provider 4.49.0
aws command line (aws-cli) 2.9.9
