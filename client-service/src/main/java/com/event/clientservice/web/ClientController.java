package com.event.clientservice.web;

import com.event.clientservice.entities.Client;
import com.event.clientservice.repositories.ClientRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class ClientController {

    private ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/clients")
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    @GetMapping("/clients/{id}")
    public Client getClient(@PathVariable Long id) {
        return clientRepository.findById(id).get();
    }

    @PostMapping("/clients")
    public void addClient(@RequestBody Client client) {
        clientRepository.save(client);
    }

    @PutMapping("/clients/{id}")
    public void updateClient(@PathVariable Long id, @RequestBody Client client) {
        Client client1 = clientRepository.findById(id).orElseThrow();
        BeanUtils.copyProperties(client, client1);
        clientRepository.save(client1);
    }

    @DeleteMapping("/clients/{id}")
    public void deleteClient(@PathVariable Long id) {
        clientRepository.deleteById(id);
    }

}
