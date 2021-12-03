package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanCreatedDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.LoanService;
import com.mindhub.homebanking.services.TransactionService;
import com.mindhub.homebanking.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LoanController {
    @Autowired
    ClientService clientService;

    @Autowired
    AccountService accountService;

    @Autowired
    LoanService loanService;

    @Autowired
    TransactionService transactionService;

    @GetMapping("/loans")
    public ResponseEntity<?> getAllLoans(Authentication authentication){
        Client client= clientService.getByEmail(authentication.getName());
        try {
            return new ResponseEntity<>(Util.makeDTO("loans", loanService.getAll().stream().map(LoanDTO::new).collect(Collectors.toList())), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>("Error en loans",HttpStatus.BAD_REQUEST);
        }
    }
    @Transactional
    @PostMapping("/loans")
    public ResponseEntity<?> registerLoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO){
    Client client= clientService.getByEmail(authentication.getName());
    Account newAccount = accountService.getByNumber(loanApplicationDTO.getAccount());
    if (loanApplicationDTO.getId().isEmpty() || loanApplicationDTO.getAmount().isEmpty() || loanApplicationDTO.getPayment().isEmpty() || loanApplicationDTO.getAccount().isEmpty()){
        return new ResponseEntity<>("No puede haber campo vacio",HttpStatus.FORBIDDEN);
    }
    double newAmount= Double.parseDouble(loanApplicationDTO.getAmount());
    int newPayment= Integer.parseInt(loanApplicationDTO.getPayment());
    Long id = Long.parseLong(loanApplicationDTO.getId());
    Loan newLoan = loanService.getById(id).orElse(null);

    if (newAccount == null){
        return new ResponseEntity<>("Cuenta no existe", HttpStatus.FORBIDDEN);
    }

    if (!client.getAccounts().stream().map(Account::getNumber).collect(Collectors.toList()).contains(newAccount.getNumber())) {
        return new ResponseEntity<>("Cuenta destino no pertenece al cliente", HttpStatus.FORBIDDEN);
    }

   if (newLoan == null){
       return new ResponseEntity<>("Prestamo no existe", HttpStatus.FORBIDDEN);
    }

    if (newAmount > newLoan.getMaxAmount()){
        return new ResponseEntity<>("Monto excede al maximo disponible para el prestamo " +newLoan.getName() +" de "+"$" + newLoan.getMaxAmount(), HttpStatus.FORBIDDEN);
    }

    if (newAmount < newLoan.getMaxAmount()/10){
        return new ResponseEntity<>("Monto mínimo para solicitar prestamo "+newLoan.getName()+" es de "+"$" + newLoan.getMaxAmount()/10, HttpStatus.FORBIDDEN);
    }

    if (!newLoan.getPayments().contains(newPayment)){
        return new ResponseEntity<>("Cantidad de cuotas no disponible", HttpStatus.FORBIDDEN);
    }

        ClientLoan newClientLoan = new ClientLoan(newAmount,newPayment, client, newLoan);
        loanService.save(newClientLoan);
        Transaction transactionDestination = new Transaction(TransactionType.CREDIT, newAmount, "Prestamo "+ newLoan.getName()+ " aprovado", LocalDateTime.now(), newAccount.getBalance() + newAmount);
        transactionService.save(transactionDestination);
        newAccount.addTransactions(transactionDestination);
        double balanceDestination = newAccount.getBalance() + newAmount;
        newAccount.setBalance(balanceDestination);
        accountService.save(newAccount);

        return new ResponseEntity<>("Prestamo creado", HttpStatus.CREATED);
    }

    @PostMapping("/loans/created")
    public ResponseEntity <?> createdLoans(Authentication authentication, @RequestBody LoanCreatedDTO loan){
        Client client = clientService.getByEmail(authentication.getName());
        if (!client.getEmail().contains("@admin.com")){
            return new ResponseEntity<>("No tiene autoridad para crear prestamos",HttpStatus.FORBIDDEN);
        }
        String newPercentege = String.valueOf(loan.getPercentage());
        if (loan.getName().isEmpty() || loan.getMaxAmount().toString().isEmpty() || loan.getPayments().isEmpty() || newPercentege.isEmpty()){
            return new ResponseEntity<>("No puede haber campo vacio",HttpStatus.FORBIDDEN);
        }
        if (loan.getMaxAmount() <= 0){
            return new ResponseEntity<>("Monto debe ser mayor a 0",HttpStatus.FORBIDDEN);
        }
        if (loan.getPercentage() <= 0){
            return new ResponseEntity<>("Porcentaje debe ser mayor a 0",HttpStatus.FORBIDDEN);
        }
        if (loan.getPayments().contains(0)){
            return new ResponseEntity<>("No puede ser 0",HttpStatus.FORBIDDEN);
        }
        int suma = 0;
        for (int i = 0; i < loan.getPayments().size(); i++){
            if (loan.getPayments().get(i) < 0){
                suma++;
                i = loan.getPayments().size();
            }
        }
        if (suma == 1){
            return new ResponseEntity<>("No puede haber numeros negativos",HttpStatus.FORBIDDEN);
        }
        Loan newLoan = new Loan(loan.getName(), loan.getMaxAmount(), loan.getPayments(), loan.getPercentage());
        loanService.save(newLoan);
        return new ResponseEntity<>("Prestamo creado",HttpStatus.CREATED);
    }
}
