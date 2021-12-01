package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


public interface TransactionService {
    public Transaction save(Transaction transaction);
    public void delete(Set<Transaction> transactions);
    public Set<Transaction> getAllByDateBetween(LocalDateTime start, LocalDateTime end);
}
