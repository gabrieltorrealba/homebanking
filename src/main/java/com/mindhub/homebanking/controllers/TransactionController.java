package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionPDFService;
import com.mindhub.homebanking.services.TransactionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@RestController
@RequestMapping("/api")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @Autowired
    ClientService clientService;

    @Autowired
    AccountService accountService;

    @Autowired
    TransactionPDFService transactionPDFService;

    @PostMapping("/transactions")
    public ResponseEntity<?> registerTransaction(Authentication authentication, @RequestParam  String amount,
                                                 @RequestParam String description, @RequestParam String originAccount,
                                                 @RequestParam String destinationAccount) {

        Client client = clientService.getByEmail(authentication.getName());
        Account accountOrigin = accountService.getByNumber(originAccount);
        Account accountDestination = accountService.getByNumber(destinationAccount);

        if (amount.isEmpty() || description.isEmpty()) {
            return new ResponseEntity<>("Monto o descripción vacia", HttpStatus.FORBIDDEN);
        }
        double amountTransfer =  Double.parseDouble(amount);

        if (amountTransfer < 100) {
            return new ResponseEntity<>("Monto mínimo a transferir $100", HttpStatus.FORBIDDEN);
        }

        if (amountTransfer > 100000){
            return new ResponseEntity<>("Monto máximo a transferir $100.000", HttpStatus.FORBIDDEN);
        }

        if (originAccount.isEmpty() || destinationAccount.isEmpty()) {
            return new ResponseEntity<>("Algún campo de cuenta está vacio", HttpStatus.FORBIDDEN);
        }

        if (accountOrigin == null) {
            return new ResponseEntity<>("Cuenta origen no existe", HttpStatus.FORBIDDEN);
        }

        if (accountDestination == null) {
            return new ResponseEntity<>("Cuenta destino no existe", HttpStatus.FORBIDDEN);
        }

        if (!client.getAccounts().stream().map(Account::getNumber).collect(Collectors.toList()).contains(originAccount)) {
            return new ResponseEntity<>("cuenta origen invalida", HttpStatus.FORBIDDEN);
        }

        if (accountOrigin.getBalance() < amountTransfer) {
            return new ResponseEntity<>("Monto execede saldo en cuenta", HttpStatus.FORBIDDEN);
        }

        if (accountOrigin.equals(accountDestination)) {
            return new ResponseEntity<>("Cuenta destino debe ser diferente a la de origen", HttpStatus.FORBIDDEN);
        }

        Transaction transactionOrigin = new Transaction(TransactionType.DEBIT, -amountTransfer,description+" "+ accountDestination.getNumber(), LocalDateTime.now(), accountOrigin.getBalance() - amountTransfer);
        transactionService.save(transactionOrigin);
        accountOrigin.addTransactions(transactionOrigin);

        double balanceOrigin = accountOrigin.getBalance() - amountTransfer;
        accountOrigin.setBalance(balanceOrigin);
        accountService.save(accountOrigin);

        Transaction transactionDestination = new Transaction(TransactionType.CREDIT, amountTransfer, description+" "+accountOrigin.getNumber(),LocalDateTime.now(), accountDestination.getBalance() + amountTransfer);
        transactionService.save(transactionDestination);
        accountDestination.addTransactions(transactionDestination);

        double balanceDestination = accountDestination.getBalance() + amountTransfer;
        accountDestination.setBalance(balanceDestination);
        accountService.save(accountDestination);

        return new ResponseEntity<>("transacción exitosa", HttpStatus.CREATED);
    }

    @PostMapping("/transactions/pdf")
    public ResponseEntity<?> generatePDF(HttpServletResponse httpServletResponse, Authentication authentication,
                            @RequestParam String accountOrigin, @RequestParam String accountDestination,
                            @RequestParam String amount, @RequestParam String description) throws IOException {
        httpServletResponse.setContentType("application/pdf");
        Client client1 = clientService.getByEmail(authentication.getName());
        Client client2 = accountService.getByNumber(accountDestination).getClient();
        Account accountDestiny = accountService.getByNumber(accountDestination);
        DateFormat dateFormat= new SimpleDateFormat("dd-MM-yyyy_hh:mm:ss");
        String currentDatTime= dateFormat.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attacment; filename=Transferencia_a_"+accountDestiny.getNumber()+"_"+currentDatTime+".pdf";
        httpServletResponse.setHeader(headerKey,headerValue);

        this.transactionPDFService.export(httpServletResponse, client1, client2, accountOrigin, accountDestination, amount, description);
        return new ResponseEntity<>("Pdf creado", HttpStatus.OK);
    }

    @PostMapping("/transactions/created/pdf")
    public ResponseEntity<?> generatePDFTransaction(HttpServletResponse httpServletResponse, Authentication authentication,
                                                    @RequestParam String from, @RequestParam String to,
                                                    @RequestParam Long id) throws IOException {

        Client client = clientService.getByEmail(authentication.getName());
        Account account = accountService.getById(id).orElse(null);
        if (from.isEmpty() || to.isEmpty() || id == null){
            return new ResponseEntity<>("Algún dato está mal", HttpStatus.FORBIDDEN);
        }
        if (!client.getAccounts().contains(account)){
            return new ResponseEntity<>("Cuenta no pertenece al cliente", HttpStatus.FORBIDDEN);
        }

        httpServletResponse.setContentType("application/pdf");
        DateFormat dateFormat= new SimpleDateFormat("dd-MM-yyyy_hh:mm:ss");
        String currentDatTime= dateFormat.format(new Date());

        LocalDateTime dateTimeFrom = LocalDateTime.parse(from);
        LocalDateTime dateTimeTo = LocalDateTime.parse(to);
        Set<Transaction> transactionSet = transactionService.getAllByDateBetween(dateTimeFrom,dateTimeTo).stream().filter(transaction -> transaction.getAccount().getId().equals(id)).collect(Collectors.toSet()).stream().sorted(Comparator.comparing(Transaction::getId).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));

        String headerKey = "Content-Disposition";
        assert account != null;
        String headerValue = "attacment; filename=Estado_de_cuenta_"+account.getNumber()+"_"+currentDatTime+".pdf";
        httpServletResponse.setHeader(headerKey,headerValue);


        transactionPDFService.exportTransaction(httpServletResponse,client,account, transactionSet);

        return new ResponseEntity<>("Pdf creado", HttpStatus.OK);
    }
}
