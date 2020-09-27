package org.xyyh.authorization.core;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Optional;

public interface OAuth2ResourceServerTokenServices {
    Optional<OAuth2Authentication> loadAuthentication(String accessToken);

    Optional<OAuth2AccessToken> readAccessToken(String accessToken);
}
