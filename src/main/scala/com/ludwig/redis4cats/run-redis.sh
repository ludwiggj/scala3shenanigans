#!/bin/sh

if [[ "$1" == "down" ]]; then
  docker compose down
elif [[ "$1" == "stop" ]]; then
  docker compose stop
elif [[ "$1" == "logs" ]] || [[ "$1" == "up" ]]; then
  docker compose up
else
  docker compose up -d
fi
