#!/bin/bash

# sudo yum update -y
sudo yum install -y jq


# install mongodb
sudo yum -y install https://repo.mongodb.org/yum/amazon/2/mongodb-org/6.0/x86_64/RPMS/mongodb-org-server-6.0.3-1.amzn2.x86_64.rpm
sudo yum -y install https://repo.mongodb.org/yum/amazon/2/mongodb-org/6.0/x86_64/RPMS/mongodb-mongosh-1.6.2.x86_64.rpm


# configure mongodb to be accesible from anywhere
sudo sed -i 's/^.*bindIp: 127.0.0.1/  bindIpAll: true/' /etc/mongod.conf


# configure mongod service
sudo systemctl daemon-reload
sudo systemctl start mongod
sudo systemctl status mongod
sudo systemctl enable mongod


# configure cloud watch logs
sudo yum install -y amazon-cloudwatch-agent
# TODO: find where are the logs of mongo

# done
echo "done" > /home/ec2-user/done.txt