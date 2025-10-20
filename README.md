# simple-pay

### Requisitos

- [Atendido] Para ambos tipos de usuário, precisamos do `Nome Completo`, `CPF`, `e-mail` e `Senha`. CPF/CNPJ e e-mails devem ser
  únicos no sistema. Sendo assim, seu sistema deve permitir apenas um cadastro com o mesmo CPF ou endereço de e-mail;

- [Atendido] Usuários podem enviar dinheiro (efetuar transferência) para lojistas e entre usuários;

- [Atendido] Lojistas **só recebem** transferências, não enviam dinheiro para ninguém;

- [Atendido] Validar se o usuário tem saldo antes da transferência;

- [Atendido] Antes de finalizar a transferência, deve-se consultar um serviço autorizador externo, use este mock
  [https://util.devi.tools/api/v2/authorize](https://util.devi.tools/api/v2/authorize) para simular o serviço
  utilizando o verbo `GET`;

- [Atendido] A operação de transferência deve ser uma transação (ou seja, revertida em qualquer caso de inconsistência) e o
  dinheiro deve voltar para a carteira do usuário que envia;

- [Atendido] No recebimento de pagamento, o usuário ou lojista precisa receber notificação (envio de email, sms) enviada por um
  serviço de terceiro e eventualmente este serviço pode estar indisponível/instável. Use este mock
  [https://util.devi.tools/api/v1/notify)](https://util.devi.tools/api/v1/notify)) para simular o envio da notificação
  utilizando o verbo `POST`;

- [Atendido] Este serviço deve ser RESTFul.

> Tente ser o mais aderente possível ao que foi pedido, mas não se preocupe se não conseguir atender a todos os
> requisitos. Durante a entrevista vamos conversar sobre o que você conseguiu fazer e o que não conseguiu.

### Endpoints

Habilitei o swagger ui para melhor visualização da API
http://localhost:8080/q/dev-ui/quarkus-smallrye-openapi/swagger-ui

Também é possivel importar para o POSTMAN ou visualizar o resumo via arquivo:
[simple-api-swagger](simple-api-swagger)

### Endpoints de transferência

Para padronizar com o restante da API o recurso principal ficou no caminho;
```http request
POST /transfer
Content-Type: application/json

{
  "value": 100.0,
  "payer": 4,
  "payee": 15
}
```

# Apresentação da solução

A soluções foi implementado com linguagem Java versão 21 e framework quarkus, contudo também impementei com os mesmo fluxos
de defidas adaptações para o framework Spring Boot [https://github.com/diegohqueiroz/pay-api-spring](https://github.com/diegohqueiroz/pay-api-spring),
resolvi implementar com ambos o framework para servir de um comparativo de compatibilidade e diferenteças real entre os este dois frameworks utilizo com Java.

Implementei a solução com camadas:
- Resources (Provê os endpoints)
- Services (Possui a regra de negócio e algumas utilidades)
- Models (Entidades e consultas ao banco de dados)
- Mappers (Conversões de dados entre objetos de banco e objetos recebidos e enviados para o cliente da api)
- Exceptions (Excessõe personalizadas)
- Consumers (Processamento assincrono)

Pontos adicionais:
- Adicionei smallrye-health para ter um end point que retorne a saude do serviço.
- Adicionei uma tabela de auditoria referente as modificações da conta e conseguentemente do saldo do cliente.
- Gere usuarios e contas de exemplo para teste [src\main\resources\import.sql](src\main\resources\import.sql) test untários [src\test\resources\import.sql](src\test\resources\import.sql)
- O cobertura de testes ficou proxima de 68 %, e pode ser verificado executando o comando abaixo e posteriormente acessando [target\jacoco-report\index.html](target\jacoco-report\index.html):
```shell script
./mvnw verify
```


## Modelagem de dados

Utilizei o banco posgres, contudo pode ser facilmente modificado via configurações do projeto
```sql
CREATE TABLE public.accounts (
	balance numeric(38, 2) NULL,
	user_id int8 NULL,
	id varchar(255) NOT NULL,
	CONSTRAINT accounts_pkey null,
	CONSTRAINT accounts_user_id_key null,
	CONSTRAINT fknjuop33mo69pd79ctplkck40n FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE public.accounts_aud (
	rev int4 NOT NULL,
	revtype int2 NULL,
	balance numeric(38, 2) NULL,
	user_id int8 NULL,
	id varchar(255) NOT NULL,
	CONSTRAINT accounts_aud_pkey null,
	CONSTRAINT fk4fc2osoc9ougcyxdyxohsgc1m FOREIGN KEY (rev) REFERENCES public.revinfo(rev)
);

CREATE TABLE public.notifications (
	send bool NULL,
	createdat timestamp(6) NULL,
	updatedat timestamp(6) NULL,
	userid int8 NULL,
	message varchar(500) NULL,
	email varchar(255) NULL,
	id varchar(255) NOT NULL,
	title varchar(255) NULL,
	CONSTRAINT notifications_pkey null
);

CREATE TABLE public.transactions (
	"type" int4 NULL,
	value numeric(38, 2) NULL,
	createdat timestamp(6) NULL,
	account_destination_id varchar(255) NULL,
	account_source_id varchar(255) NULL,
	id varchar(255) NOT NULL,
	CONSTRAINT transactions_pkey null,
	CONSTRAINT fkco5n47y1cko7u5b3wl7ht5nsd FOREIGN KEY (account_destination_id) REFERENCES public.accounts(id),
	CONSTRAINT fklkeuosnoypod4grufe4xvuxgh FOREIGN KEY (account_source_id) REFERENCES public.accounts(id)
);

CREATE TABLE public.users (
	"type" int4 NULL,
	id int8 NOT NULL,
	"document" varchar(255) NULL,
	email varchar(255) NULL,
	"name" varchar(255) NULL,
	"password" varchar(255) NULL,
	CONSTRAINT users_document_key null,
	CONSTRAINT users_email_key null,
	CONSTRAINT users_pkey null
);
```

## Processamento principal

[src\main\java\com\pay\services\TransactionService.java](src\main\java\com\pay\services\TransactionService.java)
```java
    public String transfer(TransferRequest request){ //METODO CHAMADO PELO ENDPOINT
        String id = transferPersistence(request);// TRANSACAO DO BANCO DE DADOS ISOLADA
        LOG.debug("[transfer] id: " + id);
        bus.publish(TransferConsumer.TRANSFER_PROCESSOR_ADDRESS, id);//NOTIFICACAO ASSINCRONA PARA EVITAR ATRASO NA RESPOSTA E CONSULTANDO DADOS JÀ PERSSITIDOS NO BANCO
        return id;
    }

    @Transactional
    protected String transferPersistence(@Valid TransferRequest request){
        Optional<AccountEntity> accountEntitySource = AccountEntity.find("WHERE user.id = ?1", request.getPayer()).firstResultOptional();
        Optional<AccountEntity> accountEntityDestination = AccountEntity.find("WHERE user.id = ?1", request.getPayee()).firstResultOptional();
        validate(request, accountEntitySource, accountEntityDestination);//VALICOES DE NEGOCIO ANTES DE CRIAR OBJETOSm ENTRE ELAS O SALDO
        autorization(request.getPayer());//CONSULTA PARA OBTER AUTORIZACAO
        TransactionEntity transactionEntity = generateTransaction(accountEntitySource.get(), accountEntityDestination.get(), request.getValue(), TransactionType.TRANSFER);//GERACAO TRANSCAO
        transactionEntity.persistAndFlush();//PERSISTENCIA

        accountEntitySource.get().setBalance(debitAccount(request.getValue(), accountEntitySource.get().getBalance()));//REALIZACAO DO DEBIDO NA CONTA DO PAGADOR, 
        accountEntityDestination.get().setBalance(creditAccount(request.getValue(), accountEntityDestination.get().getBalance()));//REALIZADO O CREDITO NA CONTA DO BENEFICIARIO

        accountEntitySource.get().persistAndFlush();//PERSISTENCIA
        accountEntityDestination.get().persistAndFlush();//PERSISTENCIA
        return transactionEntity.getId();
    }
```

Comparando com o Spring Boot com outro projeto
https://github.com/diegohqueiroz/pay-api-spring/blob/99cbe7ac4677d9ecbf73bf2cb93766fe77a0a88a/src/main/java/com/pay/services/TransactionService.java#L82

```java
    @Transactional
    protected String transferPersistence(@Valid TransferRequest request){
        Optional<AccountEntity> accountEntitySource = accountRepository.findByUserId(request.getPayer());
        Optional<AccountEntity> accountEntityDestination = accountRepository.findByUserId(request.getPayee());
        validate(request, accountEntitySource, accountEntityDestination);
        autorization(request.getPayer());
        TransactionEntity transactionEntity = generateTransaction(accountEntitySource.get(), accountEntityDestination.get(), request.getValue(), TransactionType.TRANSFER);
        accountEntitySource.get().setBalance(debitAccount(request.getValue(), accountEntitySource.get().getBalance()));
        accountEntityDestination.get().setBalance(creditAccount(request.getValue(), accountEntityDestination.get().getBalance()));
        
        transactionRepository.save(transactionEntity);
        accountRepository.save(accountEntitySource.get());
        accountRepository.save(accountEntityDestination.get());

        return transactionEntity.getId();
    }
```

## Evoluções
- A noficicações foram implementadas dentro no mesmo serviço, porém em um uso mais "real" poderia ser separada em outro microserviço e enviada via uma fila de mensageria.
- Adicionar novas tentativas para o caso de falha no envio notificação dentro.
-  

# Execução do projeto

## Requisitos
Variaveis de ambiente arquivo .env

```
QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://host:5432/NOME_BANCO
DB_USER=NOME_USUARIO
DB_PASS=SENHA
#SEMPRE RECRIAR O SCHEMA E OS DADOS DE TESTE AO EXECUTAR
QUARKUS_DATABASE_GENERATION=drop-and-create
#MANTER OS DADOS E SCHEMA
QUARKUS_DATABASE_GENERATION=none
```

## Execução local (nessita de um banco de dados para conectar)

```shell script
./mvnw quarkus:dev
```

## Execução via docket
Vide [docker-compose.yml](docker-compose.yml)

Build da imagem

```shell script
./build.sh
```
Executar
```shell script
./start.sh
```
