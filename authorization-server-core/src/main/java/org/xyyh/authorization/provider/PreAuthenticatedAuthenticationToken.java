package org.xyyh.authorization.provider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PreAuthenticatedAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;

    public PreAuthenticatedAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
    }

    @Override
    public Object getCredentials() {
        return "N/A";
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
