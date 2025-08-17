package com.event.clientservice.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserPrincipal implements UserDetails {


    private Client user;

    public UserPrincipal(Client user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Assuming user.getRoles() returns a List<String> of roles like ["ADMIN", "USER"]
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.trim())) // Add "ROLE_" prefix
                .toList(); // Convert to a list
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
