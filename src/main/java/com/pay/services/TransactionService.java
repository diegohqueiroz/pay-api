package com.pay.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.jboss.logging.Logger;

import com.pay.comsumers.TransferConsumer;
import com.pay.exceptions.TransactionException;
import com.pay.models.AccountEntity;
import com.pay.models.TransactionEntity;
import com.pay.models.enums.TransactionType;
import com.pay.models.enums.UserType;
import com.pay.resources.TransactionResource;
import com.pay.resources.clients.AutorizationClient;
import com.pay.resources.requests.AutorizationRequest;
import com.pay.resources.requests.MovimentRequest;
import com.pay.resources.requests.TransferRequest;

import io.vertx.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.WebApplicationException;

@ApplicationScoped
public class TransactionService {
    private static final Logger LOG = Logger.getLogger(TransactionResource.class);

    @Inject
    EventBus bus;

    @Inject
    AutorizationClient autorizationClient;

    @Transactional
    public String debit(MovimentRequest request){
        final TransactionType transactionType = TransactionType.DEBIT;
        Optional<AccountEntity> accountEntity = AccountEntity.find("WHERE user.id = ?1", request.getAccount()).firstResultOptional();
        validateMoviment(request, accountEntity, transactionType);
        autorization(request.getAccount());

        TransactionEntity transactionEntity = generateTransaction(null, accountEntity.get(), request.getValue(), TransactionType.DEBIT);
        accountEntity.get().setBalance(debitAccount(request.getValue(), accountEntity.get().getBalance()));
        
        transactionEntity.persistAndFlush();
        accountEntity.get().persistAndFlush();
        return transactionEntity.getId();
    }

    @Transactional
    public String credit(MovimentRequest request){
        final TransactionType transactionType = TransactionType.CREDIT;
        Optional<AccountEntity> accountEntity = AccountEntity.find("WHERE user.id = ?1", request.getAccount()).firstResultOptional();
        validateMoviment(request, accountEntity, transactionType);
        autorization(request.getAccount());

        TransactionEntity transactionEntity = generateTransaction(accountEntity.get(),null, request.getValue(), TransactionType.CREDIT);
        accountEntity.get().setBalance(creditAccount(request.getValue(), accountEntity.get().getBalance()));
        
        transactionEntity.persistAndFlush();
        accountEntity.get().persistAndFlush();
        return transactionEntity.getId();
    }

    public String transfer(TransferRequest request){
        String id = transferPersistence(request);
        LOG.debug("[transfer] id: " + id);
        bus.publish(TransferConsumer.TRANSFER_PROCESSOR_ADDRESS, id);
        return id;
    }

    @Transactional
    protected String transferPersistence(@Valid TransferRequest request){
        Optional<AccountEntity> accountEntitySource = AccountEntity.find("WHERE user.id = ?1", request.getPayer()).firstResultOptional();
        Optional<AccountEntity> accountEntityDestination = AccountEntity.find("WHERE user.id = ?1", request.getPayee()).firstResultOptional();
        validate(request, accountEntitySource, accountEntityDestination);
        autorization(request.getPayer());
        TransactionEntity transactionEntity = generateTransaction(accountEntitySource.get(), accountEntityDestination.get(), request.getValue(), TransactionType.TRANSFER);
        transactionEntity.persistAndFlush();

        accountEntitySource.get().setBalance(debitAccount(request.getValue(), accountEntitySource.get().getBalance()));
        accountEntityDestination.get().setBalance(creditAccount(request.getValue(), accountEntityDestination.get().getBalance()));

        accountEntitySource.get().persistAndFlush();
        accountEntityDestination.get().persistAndFlush();
        return transactionEntity.getId();
    }

    protected void validateMoviment(MovimentRequest request, Optional<AccountEntity> accountEntitySource, TransactionType transactionType){
        if (accountEntitySource.isPresent() == false) {
            throw new TransactionException("Conta de origem não encontrada");
        }
        if (TransactionType.DEBIT.equals(transactionType) && accountEntitySource.get().getBalance().compareTo(request.getValue()) < 0) {
            throw new TransactionException("Saldo insuficiente");
        }
    }

    protected void validate(TransferRequest request, Optional<AccountEntity> accountEntitySource, Optional<AccountEntity> accountEntityDestination) {
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

    protected BigDecimal debitAccount(BigDecimal valueDebit, BigDecimal balance) {
        return balance.subtract(valueDebit);
    }

    protected BigDecimal creditAccount(BigDecimal valueCredit, BigDecimal balance) {
        return balance.add(valueCredit);
    }

    protected TransactionEntity generateTransaction(AccountEntity accountEntitySource,
            AccountEntity accountEntityDestination, BigDecimal value, TransactionType transactionType) {
        TransactionEntity entity = new TransactionEntity();
        entity.setAccountSource(accountEntitySource);
        entity.setAccountDestination(accountEntityDestination);
        entity.setValue(value);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setType(transactionType.getCode());
        return entity;
    }

    protected void autorization(Long idAccount){
        try{
            autorizationClient.authorize(new AutorizationRequest(idAccount));
        }catch(WebApplicationException e){
            throw new TransactionException("Transferencia não autorizada");
        }
    }

}
