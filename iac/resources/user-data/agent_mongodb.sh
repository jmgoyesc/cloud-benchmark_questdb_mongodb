#!/bin/bash

# sudo yum update -y
sudo yum install -y jq

# install java
sudo rpm --import https://yum.corretto.aws/corretto.key 
sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
sudo yum install -y java-19-amazon-corretto-devel


# create agent.service
sudo cat >/lib/systemd/system/agent.service <<EOL
[Unit]
Description=CSB - Agent Service
After=multi-user.target

[Service]
Type=idle
ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/agent.jar --spring.profiles.active=ec2
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOL
sudo chmod 644 /lib/systemd/system/agent.service


# configure agent service
curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/agent/build/libs/agent.jar -o /home/ec2-user/agent.jar
sudo systemctl daemon-reload
sudo systemctl start agent.service
sudo systemctl enable agent.service


# configure cloud watch logs
sudo yum install -y amazon-cloudwatch-agent

aws ssm get-parameter --name "agent_mongodb" --region eu-central-1 | jq -r ".Parameter.Value" > /home/ec2-user/amazon-cloudwatch-agent.json

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:/home/ec2-user/amazon-cloudwatch-agent.json

sudo systemctl status amazon-cloudwatch-agent
sudo systemctl stop amazon-cloudwatch-agent
sudo systemctl start amazon-cloudwatch-agent


# TODO: find the way to measure the start up time (idea: take systemtime first instruction and same last instruction, print to the file done the stats)
# done
echo "done" > /home/ec2-user/done.txt