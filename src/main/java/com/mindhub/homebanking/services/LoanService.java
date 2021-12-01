package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    public Loan getByName(String name);
    public Optional<Loan>getById(Long id);
    public List<Loan> getAll();
    public ClientLoan save(ClientLoan clientLoan);
    public Loan save(Loan loan);
}
