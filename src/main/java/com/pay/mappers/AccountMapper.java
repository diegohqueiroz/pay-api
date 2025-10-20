package com.pay.mappers;

import com.pay.models.AccountEntity;
import com.pay.resources.requests.AccountRequest;
import com.pay.resources.responses.AccountResponse;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AccountMapper implements Mapper<AccountEntity, AccountRequest, AccountResponse> {

    @Override
    public AccountResponse toDTO(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new AccountResponse(
            entity.getId(),
            entity.getUser().getId(),
            entity.getUser().getName(),
            entity.getBalance()
        );
    }

    @Override
    public AccountEntity toEntity(AccountRequest dto) {
        throw new UnsupportedOperationException("Unimplemented method 'toEntity'");
    }
}