package org.xyyh.authorization.core;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.xyyh.authorization.client.ClientDetails;

/**
 * token生成器 <br>
 * 可以用它来生成 access token,authorization code 和 refresh token
 */
public interface TokenGenerator {
    /**
     * 创建一个新的access token
     *
     * @param client               连接信息
     * @param oAuth2Authentication oauth2 授权信息
     * @return 一个新的access token
     * @see OAuth2AccessToken
     * @see ClientDetails
     * @see OAuth2Authentication
     */
    OAuth2AccessToken generateAccessToken(ClientDetails client, OAuth2Authentication oAuth2Authentication);

    /**
     * 创建一个新的 authorization code
     *
     * @return 新的 OAuth2AuthorizationCode
     */
    OAuth2AuthorizationCode generateAuthorizationCode();

    /**
     * 创建一个新的refresh token
     *
     * @param client 应用信息
     * @return 新的 refresh token
     */
    OAuth2RefreshToken generateRefreshToken(ClientDetails client);
}
