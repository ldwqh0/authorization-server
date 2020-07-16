package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2AccessTokenAuthentication;

public interface OAuth2AccessTokenService {

    public OAuth2AccessTokenAuthentication save(OAuth2AccessTokenAuthentication token);

    public void delete(String token);

    public OAuth2AccessTokenAuthentication get(String accessToken);
}
