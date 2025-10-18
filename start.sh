#!/bin/bash
docker compose up -d
echo "Aplicação rodando em http://localhost:8080. Pressione Ctrl+C para parar o log."
docker compose logs -f app
docker compose down