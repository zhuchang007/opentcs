#!/bin/sh
. ./0-set-server-ip.sh

curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD905" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"intendedVehicle\":\"Ve0001\",\"destinations\":[{\"locationName\":\"St01\",\"operation\":\"Unload cargo\"},{\"locationName\":\"Re01\",\"operation\":\"CHARGE\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD906" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"intendedVehicle\":\"Ve0002\",\"destinations\":[{\"locationName\":\"St02\",\"operation\":\"Unload cargo\"},{\"locationName\":\"Re02\",\"operation\":\"CHARGE\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD907" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"intendedVehicle\":\"Ve0003\",\"destinations\":[{\"locationName\":\"St01\",\"operation\":\"Unload cargo\"},{\"locationName\":\"Re03\",\"operation\":\"CHARGE\"}]}"

sleep 1s
curl -X POST "http://$server_ip:55200/v1/transportOrders/TOrder-04E8RBM7CAFREKRFRCEYGD908" -H "accept: */*" -H "Content-Type: application/json" -d "{\"deadline\":\"2019-09-19T16:42:40.396Z\",\"intendedVehicle\":\"Ve0004\",\"destinations\":[{\"locationName\":\"St02\",\"operation\":\"Unload cargo\"},{\"locationName\":\"Re04\",\"operation\":\"CHARGE\"}]}"

