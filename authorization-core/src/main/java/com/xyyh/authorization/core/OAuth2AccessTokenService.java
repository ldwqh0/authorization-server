package com.xyyh.authorization.core;

public interface OAuth2AccessTokenService {

    public OAuth2AccessTokenAuthentication save(OAuth2AccessTokenAuthentication token);

    public void delete(String token);

    public OAuth2AccessTokenAuthentication get(String accessToken);
}
