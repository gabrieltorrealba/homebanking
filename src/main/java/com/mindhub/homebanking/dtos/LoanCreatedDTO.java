package com.mindhub.homebanking.dtos;

import java.util.ArrayList;
import java.util.List;

public class LoanCreatedDTO {

    private String name;
    private Double maxAmount;
    private int percentage;
    private List<Integer> payments = new ArrayList<>();

    public LoanCreatedDTO() {
    }

    public LoanCreatedDTO(String name, Double maxAmount, int percentage, List<Integer> payments) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.percentage = percentage;
        this.payments = payments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }
}
