package org.xyyh.authorization.core;

import java.util.Optional;

public interface OAuth2ResourceServerTokenService {
    Optional<OAuth2Authentication> loadAuthentication(String accessToken);

    Optional<OAuth2ServerAccessToken> readAccessToken(String accessToken);
}
