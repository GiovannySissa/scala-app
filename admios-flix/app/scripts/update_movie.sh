#!/bin/bash
echo "request to update a movie!"

curl -i -X PUT\
  "http://localhost:8080/v1/movie" \
  -H "accept: application/json" \
  -H "content-Type: application/json" \
  -H "Authorization: bearer ${USER_TOKEN}" \
  -d @update_movie.json




