package com.techelevator.tenmo.model;

public class BuildingAccount {

    private int accountId;
    private String accountName;

    public BuildingAccount(int accountId, String accountName) {
        this.accountId = accountId;
        this.accountName = accountName;
    }

    public BuildingAccount() {

    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}
