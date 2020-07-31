package com.xyyh.authorization.endpoint;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xyyh.authorization.core.OAuth2AccessTokenService;
import com.xyyh.authorization.core.OAuth2Authentication;
import com.xyyh.authorization.utils.OAuth2AccessTokenUtils;

/**
 * Introspection endpoint<br>
 *
 * <b>资源服务器</b>用于审查token的正确性
 *
 * @author LiDong
 * @see <a href=
 * "https://tools.ietf.org/html/rfc7662">https://tools.ietf.org/html/rfc7662</a>
 */
@RequestMapping("/oauth2/token/introspection")
public class TokenIntrospectionEndpoint {


    @Autowired
    private OAuth2AccessTokenService accessTokenService;

    /**
     * 这个接口仅对资源服务器开放，不应该被外部服务器，或者client访问到
     *
     * @param token
     * @return
     */
    @PostMapping
    @ResponseBody
    public Map<String, ?> introspection(
        @RequestParam("token") String token) {
        OAuth2AccessToken accessToken = accessTokenService.getAccessToken(token);
        OAuth2Authentication authentication = accessTokenService.getAuthentication(token);
        return OAuth2AccessTokenUtils.converterToken2IntrospectionResponse(accessToken, authentication);
    }

}
