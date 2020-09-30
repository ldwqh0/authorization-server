package org.xyyh.authorization.endpoint.converter;

import org.xyyh.authorization.core.OAuth2Authentication;
import org.xyyh.authorization.core.OAuth2ServerAccessToken;

import java.util.Map;

public interface AccessTokenConverter {

    Map<String, Object> toAccessTokenResponse(OAuth2ServerAccessToken token);

    Map<String, Object> toAccessTokenIntrospectionResponse(OAuth2ServerAccessToken token, OAuth2Authentication authentication);
}
