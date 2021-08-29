package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private Long transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int accountFromId;
    private int accountToId;
    private BigDecimal amount;

    //replaces optionals with the 2
    public Transfer(int accountFromId, int accountToId, BigDecimal amount) {
        this.transferTypeId = 2;
        this.transferStatusId = 2;
        this.accountFromId = accountFromId;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    public Transfer () {}

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
