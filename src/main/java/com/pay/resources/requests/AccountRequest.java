package com.pay.resources.requests;

public class AccountRequest {
    public long account;

    public AccountRequest() {
    }

    public AccountRequest(long account) {
        this.account = account;
    }

    public long getAccount() {
        return account;
    }

}
