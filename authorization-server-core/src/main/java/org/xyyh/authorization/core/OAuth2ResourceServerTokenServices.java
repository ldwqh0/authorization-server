package org.xyyh.authorization.core;

import java.util.Optional;

public interface OAuth2ResourceServerTokenServices {
    Optional<OAuth2Authentication> loadAuthentication(String accessToken);

    Optional<OAuth2ServerAccessToken> readAccessToken(String accessToken);
}
