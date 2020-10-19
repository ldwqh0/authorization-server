package org.xyyh.authorization.core;

import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.exception.RefreshTokenValidationException;
import org.xyyh.authorization.exception.TokenRequestValidationException;

import java.util.Collection;

public interface OAuth2AuthorizationServerTokenService {

    OAuth2ServerAccessToken createAccessToken(OAuth2Authentication authentication);

    /**
     * 使用refresh token重新创建一个access token
     *
     * @param refreshToken //     * @param authentication
     * @return
     */
    OAuth2ServerAccessToken refreshAccessToken(String refreshToken, ClientDetails client, Collection<String> requestScopes) throws RefreshTokenValidationException, TokenRequestValidationException;
}
