package com.pay.resources.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public class TransferRequest extends BaseMovimentRequest{
    @NotNull(message = "A conta da origem é obrigatória.")
    private long payer;
    @NotNull(message = "A conta da destino é obrigatória.")
    private long payee;

    public TransferRequest() {
        super();
    }

    public TransferRequest(BigDecimal value, long payer, long payee) {
        super(value);
        this.payer = payer;
        this.payee = payee;
    }

    public long getPayer() {
        return payer;
    }

    public long getPayee() {
        return payee;
    }

    public void setPayer(long payer) {
        this.payer = payer;
    }

    public void setPayee(long payee) {
        this.payee = payee;
    }
}