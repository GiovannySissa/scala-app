#!/bin/bash
echo "request to create  a new user!"

curl -i -X  POST \
  "http://localhost:8080/v1/auth/users" \
  -H "accept: application/json" \
  -H "content-Type: application/json" \
  -d @new_user.json

