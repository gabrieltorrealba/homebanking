package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;

import java.util.List;

public interface CardService {
    public List<Card> getAll();
    public Card findByNumber(String number);
    public Card save(Card card);
    public void deleteCard(Long id);
}
