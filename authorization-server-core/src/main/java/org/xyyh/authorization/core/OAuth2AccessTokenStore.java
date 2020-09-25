package org.xyyh.authorization.core;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Optional;

/**
 * access token 存储服务
 */
public interface OAuth2AccessTokenStore {

    /**
     * 保存一个Token,在某种情况下，token value的值可能发生变化，比如使用jwt
     *
     * @param token          要保存的token
     * @param authentication 和token相关的权限信息
     * @return 保存之后的token, tokenValue值可能已经发生了变化，但其它属性不应该产生变化
     */
    OAuth2AccessToken save(OAuth2AccessToken token, OAuth2Authentication authentication);

    /**
     * 删除一个现有的accessToken
     *
     * @param accessToken 要删除的token值
     */
    void delete(String accessToken);

    /**
     * 根据access token值获取 {@link OAuth2Authentication}
     *
     * @param accessToken access token值
     */
    Optional<OAuth2Authentication> getAuthentication(String accessToken);

    /**
     * 根据access token值获取 {@link OAuth2AccessToken}
     *
     * @param accessToken access token值
     */
    Optional<OAuth2AccessToken> getAccessToken(String accessToken);

    /**
     * 根据{@link OAuth2Authentication}获取{@link OAuth2AccessToken}<br>
     * 根据name,clientId,scope来确认authentication的唯一性
     *
     * @param authentication 用户的{@link OAuth2Authentication}
     */
    Optional<OAuth2AccessToken> getAccessToken(OAuth2Authentication authentication);

}
