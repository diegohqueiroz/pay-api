package com.pay.services;

import com.pay.mappers.AccountMapper;
import com.pay.models.AccountEntity;
import com.pay.resources.responses.AccountResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AccountService {
    @Inject
    AccountMapper mapper;

    public AccountResponse getByUserId(Long idUser){
        return mapper.toDTO(AccountEntity.find("WHERE user.id = ?1", idUser).firstResult());
    }
    
}
