package com.xyyh.authorization.provider;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.xyyh.authorization.client.ClientDetails;
import com.xyyh.authorization.client.ClientDetailsService;

public class ClientDetailsUserDetailsService implements UserDetailsService {

    private ClientDetailsService clientDetailsService;

    public ClientDetailsUserDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new ClientUserDetails(clientDetailsService.loadClientByClientId(username));
    }

}

class ClientUserDetails implements UserDetails {

    private static final long serialVersionUID = -4968552547785149722L;
    private final ClientDetails clientDetails;

    public ClientUserDetails(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return clientDetails.getClientSecret();
    }

    @Override
    public String getUsername() {
        return clientDetails.getClientId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
