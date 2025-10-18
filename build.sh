#!/bin/bash
./mvnw package -DskipTests
docker compose build app