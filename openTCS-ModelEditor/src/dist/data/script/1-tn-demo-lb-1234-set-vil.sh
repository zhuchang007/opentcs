#!/bin/sh
. ./0-set-server-ip.sh

curl -X PUT "http://$server_ip:55200/v1/vehicles/Ve0001/integrationLevel?newValue=TO_BE_UTILIZED" -H "accept: */*"

curl -X PUT "http://$server_ip:55200/v1/vehicles/Ve0002/integrationLevel?newValue=TO_BE_UTILIZED" -H "accept: */*"

curl -X PUT "http://$server_ip:55200/v1/vehicles/Ve0003/integrationLevel?newValue=TO_BE_UTILIZED" -H "accept: */*"

curl -X PUT "http://$server_ip:55200/v1/vehicles/Ve0004/integrationLevel?newValue=TO_BE_UTILIZED" -H "accept: */*"

