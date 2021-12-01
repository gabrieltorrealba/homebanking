package com.mindhub.homebanking.dtos;


public class LoanApplicationDTO {
    private String id;
    private String amount;
    private String payment;
    private String account;

    public LoanApplicationDTO(String id, String amount, String payment, String account) {
        this.id = id;
        this.amount = amount;
        this.payment = payment;
        this.account = account;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
