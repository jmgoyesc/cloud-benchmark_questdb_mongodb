#!/bin/bash

# sudo yum update -y
sudo yum install -y jq


# install questdb
curl -LJ https://github.com/questdb/questdb/releases/download/6.6.1/questdb-6.6.1-rt-linux-amd64.tar.gz -o /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz
tar -xvf /home/ec2-user/questdb-6.6.1-rt-linux-amd64.tar.gz -C /home/ec2-user/


# create questdb.service
sudo cat >/lib/systemd/system/questdb.service <<EOL
[Unit]
Description=QuestDB Service
After=multi-user.target

[Service]
Type=forking
Restart=always
RestartSec=2
ExecStart=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh start -d home/ec2-user/.questdb
ExecStop=/home/ec2-user/questdb-6.6.1-rt-linux-amd64/bin/questdb.sh stop

[Install]
WantedBy=multi-user.target
EOL
sudo chmod 644 /lib/systemd/system/questdb.service


# configure questdb service
sudo systemctl daemon-reload
sudo systemctl start questdb.service
sudo systemctl enable questdb.service


# configure cloud watch logs
sudo yum install -y amazon-cloudwatch-agent

aws ssm get-parameter --name "sut_questdb_influx" --region eu-central-1 | jq -r ".Parameter.Value" > /home/ec2-user/amazon-cloudwatch-agent.json

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -s -c file:/home/ec2-user/amazon-cloudwatch-agent.json

sudo systemctl status amazon-cloudwatch-agent
sudo systemctl stop amazon-cloudwatch-agent
sudo systemctl start amazon-cloudwatch-agent

# done
echo "done" > /home/ec2-user/done.txt