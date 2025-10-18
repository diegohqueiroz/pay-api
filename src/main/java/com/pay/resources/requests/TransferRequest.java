package com.pay.resources.requests;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TransferRequest {
    
    @NotNull(message = "O valor da transferência é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal value;
    @NotNull(message = "A conta da origem é obrigatória.")
    private long payer;
    @NotNull(message = "A conta da destino é obrigatória.")
    private long payee;

    public TransferRequest() {
    }

    public TransferRequest(BigDecimal value, long payer, long payee) {
        this.value = value;
        this.payer = payer;
        this.payee = payee;
    }

    public BigDecimal getValue() {
        return value;
    }

    public long getPayer() {
        return payer;
    }

    public long getPayee() {
        return payee;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setPayer(long payer) {
        this.payer = payer;
    }

    public void setPayee(long payee) {
        this.payee = payee;
    }
}