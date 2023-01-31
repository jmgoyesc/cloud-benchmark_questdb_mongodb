#!/bin/bash

sudo yum update -y


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
ExecStart=/usr/bin/java --enable-preview -jar /home/ec2-user/control-1.0.0.jar --spring.profiles.active=ec2
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOL
sudo chmod 644 /lib/systemd/system/control.service


# configure control service
curl -LJ https://github.com/jmgoyesc/cloud-benchmark_questdb_mongodb/raw/main/control/build/libs/control-1.0.0.jar -o /home/ec2-user/control-1.0.0.jar
sudo systemctl daemon-reload
sudo systemctl start control.service
sudo systemctl enable control.service


# configure cloud watch logs
sudo yum install amazon-cloudwatch-agent