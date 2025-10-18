package com.pay.resources.responses;

public class TransferResponse {
    public String message;
    public long fromAccountId;
    public long toAccountId;
    public double amount;
    public String transactionId;

    public TransferResponse(String message, long fromAccountId, long toAccountId, double amount, String transactionId) {
        this.message = message;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.transactionId = transactionId;
    }

}