package com.pay.resources.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class MovimentRequest extends BaseMovimentRequest{
    @NotNull(message = "A conta da movimentação é obrigatória.")
    private long account;

    public MovimentRequest(BigDecimal value, long account) {
        super(value);
        this.account = account;
    }

    public long getAccount() {
        return account;
    }

}