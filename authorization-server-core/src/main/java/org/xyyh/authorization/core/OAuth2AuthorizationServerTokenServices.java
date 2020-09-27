package org.xyyh.authorization.core;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.xyyh.authorization.client.ClientDetails;
import org.xyyh.authorization.exception.RefreshTokenValidationException;
import org.xyyh.authorization.exception.TokenRequestValidationException;

import java.util.Optional;

public interface OAuth2AuthorizationServerTokenServices {

    OAuth2AccessToken createAccessToken(OAuth2Authentication authentication);

    OAuth2RefreshToken createRefreshToken(String accessToken, ClientDetails client);

    /**
     * 使用refresh token重新创建一个access token
     *
     * @param refreshToken
     * @param authentication
     * @return
     */
    OAuth2AccessToken refreshAccessToken(String refreshToken, OAuth2Authentication authentication);

    /**
     * 进行refresh token的预检测
     *
     * @param refreshToken refresh token
     * @throws TokenRequestValidationException 校验异常
     */
    void preCheckRefreshToken(String refreshToken) throws RefreshTokenValidationException;

    Optional<OAuth2Authentication> loadAuthenticationByRefreshToken(String refreshToken);

    void deleteRefreshToken(String refreshToken);
}
