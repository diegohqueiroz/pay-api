package com.pay.services;

import java.math.BigDecimal;
import java.util.Optional;

import com.pay.comsumers.TransferConsumer;
import com.pay.exceptions.TransactionException;
import com.pay.models.AccountEntity;
import com.pay.models.TransactionEntity;
import com.pay.models.enums.TransactionType;
import com.pay.models.enums.UserType;
import com.pay.resources.AutorizationClient;
import com.pay.resources.requests.AutorizationRequest;
import com.pay.resources.requests.TransferRequest;

import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class TransactionService {

    @Inject
    EventBus bus;

    @Inject
    AutorizationClient autorizationClient;

    public String transfer(TransferRequest request){
        String id = transferPersistence(request);
        System.out.println("transfer id: " + id);
        bus.publish(TransferConsumer.TRANSFER_PROCESSOR_ADDRESS, id);
        return id;
    }

    @Transactional
    protected String transferPersistence(@Valid TransferRequest request){
        Optional<AccountEntity> accountEntitySource = AccountEntity.find("WHERE user.id = ?1", request.getPayer()).firstResultOptional();
        Optional<AccountEntity> accountEntityDestination = AccountEntity.find("WHERE user.id = ?1", request.getPayee()).firstResultOptional();
        validate(request, accountEntitySource, accountEntityDestination);
        autorization(request);
        TransactionEntity entity = generateTransaction(request, accountEntitySource.get(), accountEntityDestination.get());
        entity.persistAndFlush();

        accountEntitySource.get().setBalance(debitAccount(request.getValue(), accountEntityDestination.get().getBalance()));
        accountEntityDestination.get().setBalance(creditAccount(request.getValue(), accountEntityDestination.get().getBalance()));

        accountEntitySource.get().persistAndFlush();
        accountEntityDestination.get().persistAndFlush();
        return entity.getId();
    }

    private void validate(TransferRequest request, Optional<AccountEntity> accountEntitySource, Optional<AccountEntity> accountEntityDestination) {
        if (request.getPayee() == request.getPayer()) {
            throw new TransactionException("Não é possivel transferir para mesma conta");
        }
        if (accountEntitySource.isPresent() == false) {
            throw new TransactionException("Conta de origem não encontrada");
        }
        if (accountEntitySource.get().getUser().getType() == UserType.COMPANY.getCode()){
            throw new TransactionException("Conta não pode realizar transferencia");
        }
        if (accountEntitySource.get().getBalance().compareTo(request.getValue()) < 0) {
            throw new TransactionException("Saldo insuficiente");
        }
        if (accountEntityDestination == null) {
            throw new TransactionException("Conta de destino não encontrada");
        }
    }

    private BigDecimal debitAccount(BigDecimal valueDebit, BigDecimal balance) {
        return balance.subtract(valueDebit);
    }

    private BigDecimal creditAccount(BigDecimal valueCredit, BigDecimal balance) {
        return balance.add(valueCredit);
    }

    private TransactionEntity generateTransaction(TransferRequest request, AccountEntity accountEntitySource,
            AccountEntity accountEntityDestination) {
        TransactionEntity entity = new TransactionEntity();
        entity.setAccountSource(accountEntitySource);
        entity.setAccountDestination(accountEntityDestination);
        entity.setValue(request.getValue());
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setType(TransactionType.TRANSFER.getCode());
        return entity;
    }

    private void autorization(TransferRequest request){
        try{
            autorizationClient.authorize(new AutorizationRequest());
        }catch(WebApplicationException e){
            throw new TransactionException("Transferencia não autorizada");
        }
    }

}
