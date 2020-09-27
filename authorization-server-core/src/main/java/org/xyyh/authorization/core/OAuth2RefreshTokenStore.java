package org.xyyh.authorization.core;

import java.util.Optional;

public interface OAuth2RefreshTokenStore {

    /**
     * 保存一个Token,在某种情况下，token value的值可能发生变化，比如使用jwt
     *
     * @param token          要保存的token
     * @param authentication 和token相关的权限信息
     * @return 保存之后的token, tokenValue值可能已经发生了变化，但其它属性不应该产生变化
     */
    OAuth2ServerRefreshToken save(OAuth2ServerRefreshToken token, String accessTokenValue, OAuth2Authentication authentication);

    /**
     * 删除一个现有的accessToken,需要同时删除该token相对应的其它信息，包括{@link OAuth2ServerRefreshToken}和{@link OAuth2Authentication}和access token
     *
     * @param tokenValue 要删除的token值
     */
    void delete(String tokenValue);

    Optional<OAuth2Authentication> getAuthentication(String tokenValue);

    Optional<OAuth2ServerRefreshToken> getToken(String tokenValue);

    Optional<String> getAccessToken(String tokenValue);

    Optional<OAuth2ServerRefreshToken> findByAccessToken(String accessToken);
}
