package com.event.clientservice.web;

import com.event.clientservice.entities.Client;
import com.event.clientservice.repositories.ClientRepository;
import com.event.clientservice.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

public class ClientController {

    private ClientRepository clientRepository;

    @Autowired
    UserService userService;

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

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/clientss/{username}")
    public Client getClientByUsername(@PathVariable String username) {
        return clientRepository.findByUsername(username);
    }

    @GetMapping("/clients/me")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Client getAuthenticatedClient(Authentication authentication) {
        String username = authentication.getName(); // comes from JWT subject
        return clientRepository.findByUsername(username);
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


    @PostMapping("/register")
    public Client register(@RequestBody Client user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Client user) {
        return userService.verify(user);
    }

}
