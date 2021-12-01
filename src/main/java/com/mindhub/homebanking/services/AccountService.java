package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    public List<Account> getAll();
    public Optional<Account> getById(Long id);
    public Account save(Account account);
    public Account getByNumber(String number);
    public void deleteAccount(Long id);
}
