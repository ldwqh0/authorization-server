package org.xyyh.authorization.core;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

public interface OAuth2AccessTokenService {

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

    OAuth2Authentication getAuthentication(String accessToken);

    OAuth2AccessToken getAccessToken(String accessToken);

    // TODO 可能定义不太准确
    OAuth2RefreshToken createRefreshToken(OAuth2AccessToken accessToken);
}
