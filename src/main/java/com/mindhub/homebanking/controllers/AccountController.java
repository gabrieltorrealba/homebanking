package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.util.Util;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/accounts")
    public ResponseEntity<?> getAllAccounts(){
        try {
            return new ResponseEntity<>(Util.makeDTO("accounts", this.accountService.getAll().stream().map(AccountDTO::new).collect(Collectors.toList())), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Error de cuentas",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/clients/current/accounts/{id}")
    public ResponseEntity<?> getAccount(Authentication authentication, @PathVariable Long id){
        Client client = this.clientService.getByEmail(authentication.getName());
        Account account= accountService.getById(id).orElse(null);
        if (!client.getAccounts().contains(account)){
            return new ResponseEntity<>("cuenta no pertenece a cliente",HttpStatus.UNAUTHORIZED);
        }
            return new ResponseEntity<>(Util.makeDTO("account", this.accountService.getById(id).map(AccountDTO::new).orElse(null)),HttpStatus.OK);
    }

    @PostMapping("/clients/current/accounts")
    public ResponseEntity<?> registerAccount(Authentication authentication, @RequestParam AccountType type){
        Client newClient = this.clientService.getByEmail(authentication.getName());
        if (newClient.getAccounts().size() > 2){
            return new ResponseEntity<>("Exceeds maximum allowed", HttpStatus.FORBIDDEN);
        }
        List <String> accountByNumber = this.accountService.getAll().stream().map(Account::getNumber).collect(Collectors.toList());
        String newAccountNumber= Util.accountNumber();
        while (accountByNumber.contains(newAccountNumber)){
            newAccountNumber = Util.accountNumber();
        }
        Account newAccount = new Account(newAccountNumber, LocalDateTime.now(), 0, newClient, type);
        this.accountService.save(newAccount);
        return new ResponseEntity<>("Cuenta creada", HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/current/accounts/{id}")
    public ResponseEntity<?> deleteAccount(Authentication authentication, @PathVariable Long id){
        Client newClient = clientService.getByEmail(authentication.getName());
        Account account= accountService.getById(id).orElse(null);
        if (!newClient.getAccounts().contains(account)){
            return new ResponseEntity<>("Cuenta no pertenece a cliente", HttpStatus.FORBIDDEN);
        }
        assert account != null;
        if (account.getBalance() > 0){
            return new ResponseEntity<>("Balance debe ser 0 para poder dar de baja", HttpStatus.FORBIDDEN);
        }
        Set<Transaction> transactions = account.getTransactions();
        transactionService.delete(transactions);
        accountService.deleteAccount(id);
        return new ResponseEntity<>("Cuenta eliminada", HttpStatus.OK);
    }
}
