#!/bin/sh
. ./0-set-server-ip.sh

curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD901" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"intendedVehicle\":\"Ve0001\",\"destinations\":[{\"locationName\":\"GiN01\",\"operation\":\"Load cargo\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD902" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"intendedVehicle\":\"Ve0002\",\"destinations\":[{\"locationName\":\"GiN02\",\"operation\":\"Load cargo\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD903" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"intendedVehicle\":\"Ve0003\",\"destinations\":[{\"locationName\":\"GiS01\",\"operation\":\"Load cargo\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD904" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"intendedVehicle\":\"Ve0004\",\"destinations\":[{\"locationName\":\"St01\",\"operation\":\"Load cargo\"}]}"

