#!/bin/bash
echo "request delete a movie!"

curl -i -X DELETE \
  "http://localhost:8080/v1/movie/49e72634-1e0e-4d05-b828-9517a791bf60" \
  -H "accept: application/json" \
  -H "content-Type: application/json" \
  -H "Authorization: bearer ${USER_TOKEN}"



