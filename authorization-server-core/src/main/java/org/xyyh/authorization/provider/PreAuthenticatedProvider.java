package org.xyyh.authorization.provider;

import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;

public class PreAuthenticatedProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final UserDetailsChecker userChecker = new AccountStatusUserDetailsChecker();

    public PreAuthenticatedProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }
        String username = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        userChecker.check(userDetails);
        PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(username, userDetails.getAuthorities());
        result.setAuthenticated(true);
        result.setDetails(userDetails);
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
