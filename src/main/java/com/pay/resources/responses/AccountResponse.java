package com.pay.resources.responses;

import java.math.BigDecimal;

public class AccountResponse {
    public String idAccount;
    public Long idUser;
    public String nameUser;
    public BigDecimal balance;

    public AccountResponse() {
    }

    public AccountResponse(String idAccount, Long idUser, String nameUser, BigDecimal balance) {
        this.idAccount = idAccount;
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.balance = balance;
    }

    public String getIdAccount() {
        return idAccount;
    }

    public Long getIdUser() {
        return idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public BigDecimal getBalance() {
        return balance;
    }

}
