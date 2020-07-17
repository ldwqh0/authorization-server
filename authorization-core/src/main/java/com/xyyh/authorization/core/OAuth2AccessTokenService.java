package com.xyyh.authorization.core;

import org.springframework.security.oauth2.core.OAuth2AccessToken;

public interface OAuth2AccessTokenService {

    public OAuth2AccessToken create(OAuth2Authentication authentication);

    public void delete(String accessToken);

    public OAuth2Authentication getAuthentication(String accessToken);

    public OAuth2AccessToken getAccessToken(String accessToken);
}
