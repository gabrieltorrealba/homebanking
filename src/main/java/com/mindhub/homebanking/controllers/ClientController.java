package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.AccountType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

   @GetMapping("/clients")
   public ResponseEntity <?> getAllClients(){
       try {
           return new ResponseEntity<>(Util.makeDTO("clients", this.clientService.getAll().stream().map(ClientDTO::new).collect(Collectors.toList())),
                   HttpStatus.OK);
       }catch (Exception e){
           return new ResponseEntity<>("Error" + e.getMessage(),HttpStatus.BAD_REQUEST);
       }
   }

    @GetMapping("/clients/current")
    public ResponseEntity<?> getClientDTO(Authentication authentication){
       try {
           Client client = this.clientService.getByEmail(authentication.getName());
           ClientDTO clientDTO = new ClientDTO(client);
           return new ResponseEntity<>(Util.makeDTO("current",clientDTO),HttpStatus.OK);
       } catch (Exception e){
           return new ResponseEntity<>("Error",HttpStatus.BAD_REQUEST);
       }
    }

    @PostMapping("/clients")
    public ResponseEntity<Object> register(@RequestParam String firstName, @RequestParam String lastName,
                                           @RequestParam String email, @RequestParam String password, @RequestParam AccountType type) {
        if (firstName.isEmpty()|| lastName.isEmpty() || email.isEmpty() || password.isEmpty() || type.toString().isEmpty()) {
            return new ResponseEntity<>("No debe haber campo vacío", HttpStatus.FORBIDDEN);
        }
        if (this.clientService.getByEmail(email) != null) {
            return new ResponseEntity<>("Email ya existe", HttpStatus.FORBIDDEN);
        }
        Client newClient =new Client(firstName, lastName, email, passwordEncoder.encode(password));
        this.clientService.save(newClient);
        List<String> accountByNumber = this.accountService.getAll().stream().map(Account::getNumber).collect(Collectors.toList());
        String newAccountNumber= Util.accountNumber();
        while (accountByNumber.contains(newAccountNumber)){
            newAccountNumber = Util.accountNumber();
        }
        Account newAccount = new Account(newAccountNumber, LocalDateTime.now(), 0, newClient, type);
        this.accountService.save(newAccount);
        return new ResponseEntity<>("Usuario creado",HttpStatus.CREATED);
    }


}


