package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Client;

import java.util.List;
import java.util.Optional;


public interface ClientService {
    public List<Client> getAll();
    public Optional<Client> getById(Long id);
    public Client getByEmail(String email);
    public Client save(Client client);
}
