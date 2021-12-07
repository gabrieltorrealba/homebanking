package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.PaymentsDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Transactional
@RestController
public class PaymentsController {

    @Autowired
    AccountService accountService;

    @Autowired
    CardService cardService;

    @Autowired
    TransactionService transactionService;

    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/api/payments")
    public ResponseEntity<?> registerPayments(@RequestBody PaymentsDTO paymentsDTO){
        String newCardNumber = paymentsDTO.getNumber().substring(0,4)+"-"+paymentsDTO.getNumber().substring(4,8)+"-"+paymentsDTO.getNumber().substring(8,12)+"-"+paymentsDTO.getNumber().substring(12,16);
        Card newCard = cardService.findByNumber(newCardNumber);
        Account accountTo = accountService.getByNumber(paymentsDTO.getAccountNumber());
        LocalDateTime newDay = LocalDateTime.now();
        if (newCard == null){
            return new ResponseEntity<>("Tarjeta no existe", HttpStatus.FORBIDDEN);
        }
        Account newAccount = accountService.getByNumber(newCard.getAccount().getNumber());
        System.out.println(newCard.getThruDate());
        if (accountTo == null){
            return new ResponseEntity<>("Cuenta destino no existe", HttpStatus.FORBIDDEN);
        }
        if (newCard.getCvv() != paymentsDTO.getCvv()){
            return new ResponseEntity<>("Codigo de seguridad incorrecto", HttpStatus.FORBIDDEN);
        }
        if (newCard.getThruDate().isBefore(newDay)){
            return new ResponseEntity<>("Tarjeta vencida", HttpStatus.FORBIDDEN);
        }
        if (newCard.getAccount().getBalance() < paymentsDTO.getAmount()){
            return new ResponseEntity<>("Saldo insuficiente", HttpStatus.FORBIDDEN);
        }
        String cardNumber = newCard.getNumber().substring(newCard.getNumber().length() - 4);

        Transaction transaction = new Transaction(TransactionType.DEBIT, -paymentsDTO.getAmount(),"Pago "+ paymentsDTO.getDescription()+" "+"tarjeta No."+cardNumber, LocalDateTime.now(), newAccount.getBalance() - paymentsDTO.getAmount());
        transactionService.save(transaction);
        newAccount.addTransactions(transaction);
        newAccount.setBalance(newAccount.getBalance() - paymentsDTO.getAmount());
        accountService.save(newAccount);

        Transaction transactionTo = new Transaction(TransactionType.CREDIT, paymentsDTO.getAmount(),"Pago "+ paymentsDTO.getDescription()+" "+"tarjeta No."+cardNumber, LocalDateTime.now(), accountTo.getBalance() + paymentsDTO.getAmount());
        transactionService.save(transactionTo);
        accountTo.addTransactions(transactionTo);
        accountTo.setBalance(accountTo.getBalance() + paymentsDTO.getAmount());
        accountService.save(accountTo);

        return new ResponseEntity<>("Pago exitoso", HttpStatus.CREATED);
    }
}
