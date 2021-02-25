package org.xyyh.authorization.provider;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

// principal用于用户信息
// details用户存储连接信息
// spring security默认就是这么处理的
public class PreAuthenticatedAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -5103231704382259801L;
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
