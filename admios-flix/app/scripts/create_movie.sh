#!/bin/bash
echo "request to create  a new movie!"

curl -i -X POST\
  "http://localhost:8080/v1/movie" \
  -H "accept: application/json" \
  -H "content-Type: application/json" \
  -H "Authorization: bearer ${USER_TOKEN}" \
  -d @movie.json




