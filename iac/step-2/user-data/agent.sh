#!/bin/bash

sudo yum update -y


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
ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/agent-1.0.0.jar --spring.profiles.active=ec2
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOL
sudo chmod 644 /lib/systemd/system/agent.service


# configure agent service
curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/agent/build/libs/agent-1.0.0.jar -o /home/ec2-user/agent-1.0.0.jar
sudo systemctl daemon-reload
sudo systemctl start agent.service
sudo systemctl enable agent.service


# configure cloud watch logs
sudo yum install amazon-cloudwatch-agent