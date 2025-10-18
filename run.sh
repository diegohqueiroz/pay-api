#!/bin/bash

echo "--- 1. Limpando e Empacotando o projeto Quarkus (JVM) ---"
./mvnw package -DskipTests

# Verifica se o build do Maven foi bem-sucedido
if [ $? -ne 0 ]; then
    echo "Erro: O build do Maven falhou. Abortando."
    exit 1
fi

echo "--- 2. Construindo a Imagem Docker da Aplicação (app) ---"
docker compose build app

echo "--- 3. Iniciando os Contêineres (app e db) ---"
docker compose up -d

echo "--- 4. Acompanhando os Logs da Aplicação (app) ---"
echo "Aplicação Quarkus rodando em http://localhost:8080"
echo "Pressione Ctrl+C para parar e remover os contêineres."

docker compose logs -f app

echo "--- 5. Parando e Removendo os Contêineres ---"
docker compose down
