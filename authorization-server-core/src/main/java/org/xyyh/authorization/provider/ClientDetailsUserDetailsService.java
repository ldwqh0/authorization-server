package org.xyyh.authorization.provider;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.client.ClientDetailsService;
import org.xyyh.authorization.exception.NoSuchClientException;

import java.util.Collection;
import java.util.Collections;

public class ClientDetailsUserDetailsService implements UserDetailsService {

    private final ClientDetailsService clientDetailsService;

    public ClientDetailsUserDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new ClientUserDetails(clientDetailsService.loadClientByClientId(username));
        } catch (NoSuchClientException e) {
            throw new UsernameNotFoundException("the client can not be find with id" + username);
        }
    }

}

class ClientUserDetails implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = -4968552547785149722L;
    private final ClientDetails clientDetails;

    public ClientUserDetails(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }

    public ClientDetails getClientDetails() {
        return clientDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO　这里带扩展，后期不应该有空的Authorities
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

    @Override
    public void eraseCredentials() {
        // TODO 对于client的密码是否需要擦除，暂时不处理
    }
}
