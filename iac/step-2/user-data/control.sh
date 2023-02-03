#!/bin/bash

# sudo yum update -y
sudo yum install -y jq


# install java
sudo rpm --import https://yum.corretto.aws/corretto.key 
sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
sudo yum install -y java-19-amazon-corretto-devel


# create control.service
sudo cat >/lib/systemd/system/control.service <<EOL
[Unit]
Description=CSB - Control Service
After=multi-user.target

[Service]
Type=idle
ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/control.jar --spring.profiles.active=ec2
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOL
sudo chmod 644 /lib/systemd/system/control.service


# configure control service
curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/control/build/libs/control.jar -o /home/ec2-user/control.jar
sudo systemctl daemon-reload
sudo systemctl start control.service
sudo systemctl enable control.service


# configure cloud watch logs
sudo yum install -y amazon-cloudwatch-agent

aws ssm get-parameter --name "control-cloudwatch-config" --region eu-central-1 | jq -r ".Parameter.Value" > /home/ec2-user/amazon-cloudwatch-agent.json

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:/home/ec2-user/amazon-cloudwatch-agent.json

sudo systemctl status amazon-cloudwatch-agent
sudo systemctl stop amazon-cloudwatch-agent
sudo systemctl start amazon-cloudwatch-agent


# done
echo "done" > /home/ec2-user/done.txt