#!/bin/bash
echo "request to login an user!"

curl -i -X  POST \
  "http://localhost:8080/v1/auth/login" \
  -H "accept: application/json" \
  -H "content-Type: application/json" \
  -d @new_user.json

