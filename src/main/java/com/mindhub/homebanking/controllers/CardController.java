package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private CardService cardService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/clients/current/cards")
    public ResponseEntity<?> registerCard(Authentication authentication, @RequestParam String cardType,
                                          @RequestParam String cardColor, @RequestParam String accountNumber){
        Client newClient = clientService.getByEmail(authentication.getName());
        Account account = accountService.getByNumber(accountNumber);

        if(cardType.isEmpty() || cardColor.isEmpty()){
            return new ResponseEntity<>("No puede haber campo vacio", HttpStatus.FORBIDDEN);
        }
        CardType newType= CardType.valueOf(cardType);
        CardColor newColor= CardColor.valueOf(cardColor);

        if (newClient.getCards().stream().filter(card -> card.getType().equals(newType)).count() > 2){
            return new ResponseEntity<>("Maximo tarjeta por cliente 3", HttpStatus.FORBIDDEN);
        }

        if (!newClient.getAccounts().contains(account)){
            return new ResponseEntity<>("Cuenta no pertenece a cliente", HttpStatus.FORBIDDEN);
        }
        if (account == null){
            return new ResponseEntity<>("Cuenta no existe", HttpStatus.FORBIDDEN);
        }

        List <String> getCardNumber = cardService.getAll().stream().map(Card::getNumber).collect(Collectors.toList());
       String cardNumber = Util.cardNumber();
        while (getCardNumber.contains(cardNumber)){
            cardNumber = Util.cardNumber();
        }
        Card newCard = new Card(newType, newColor, cardNumber,(int)(Math.random()*(999-100)+100), LocalDateTime.now(),LocalDateTime.now().plusYears(5),newClient, account);
        cardService.save(newCard);
        return new  ResponseEntity<>("Tarjeta creada",HttpStatus.CREATED);
    }

    @DeleteMapping("/clients/current/cards/{id}")
    public ResponseEntity<?> deleteCard(Authentication authentication, @PathVariable("id") Long id){
        Client newClient = clientService.getByEmail(authentication.getName());
        if (!newClient.getCards().stream().map(Card::getId).collect(Collectors.toList()).contains(id)){
            return new ResponseEntity<>("Tarjeta invalida", HttpStatus.FORBIDDEN);
        }
        cardService.deleteCard(id);
        return new ResponseEntity<>("Tarjeta eliminada", HttpStatus.OK);
    }
}
