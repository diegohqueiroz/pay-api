package com.pay.models.enums;

public enum TransactionType {
    TRANSFER("Transferência", 1),
    CREDIT("Deposito", 2),
    DEBIT("Saque", 3);

    private String description;
    private Integer code;

    private TransactionType(String description, Integer code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }
}
