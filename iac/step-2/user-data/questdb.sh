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
# TODO: find where are the logs for questdb

# done
echo "done" > /home/ec2-user/done.txt