# IaC - Infrastructure as Code

This project creates the necessary infrastructure to run the benchmark.

Cloud provider: Amazon Web Services (AWS)
Region. eu-europe (Frankfurt)

## Getting started

1. Configure an AWS account
2. Generate an Access Key for terraform with the following policies attached: AmazonEC2FullAccess, AmazonSSMFullAccess, AmazonVPCFullAccess, IAMFullAccess
3. Create a Key Pair in EC2 with this name "cbs_key"
4. In the default workspace, execute `terraform init && terraform apply'
5. Confirm the request in the command line. Type "yes"
6. After the experiment is finished, execute `terraform destroy`

## Tool versions

Install the tools using homebrew in mac

```shell
brew install terraform
brew install awscli
```

terraform 1.3.7
aws provider 4.49.0
aws command line (aws-cli) 2.9.9
