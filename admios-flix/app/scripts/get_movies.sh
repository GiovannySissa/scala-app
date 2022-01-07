#!/bin/bash
echo "request to get movies!"

curl -i \
  "http://localhost:8080/v1/movie?genre=Action&genre=Adventure&pageSize=1&offset=2" \
  -H "accept: application/json" \
  -H "content-Type: application/json" \
  -H "Authorization: bearer ${USER_TOKEN}"



