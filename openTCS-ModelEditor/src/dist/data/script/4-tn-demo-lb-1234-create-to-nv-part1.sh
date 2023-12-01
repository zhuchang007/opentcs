#!/bin/sh
. ./0-set-server-ip.sh

curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD911" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"destinations\":[{\"locationName\":\"GiN01\",\"operation\":\"Load cargo\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD912" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"destinations\":[{\"locationName\":\"GiN02\",\"operation\":\"Load cargo\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD913" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"destinations\":[{\"locationName\":\"GiS01\",\"operation\":\"Load cargo\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD914" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"destinations\":[{\"locationName\":\"St01\",\"operation\":\"Load cargo\"}]}"

