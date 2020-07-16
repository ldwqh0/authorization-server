package com.xyyh.authorization.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xyyh.authorization.core.OAuth2AccessTokenAuthentication;
import com.xyyh.authorization.core.OAuth2ApprovalAuthenticationToken;
import com.xyyh.authorization.provider.OAuth2AuthorizationCodeService;
import com.xyyh.authorization.provider.OAuth2AccessTokenGenerator;
import com.xyyh.authorization.provider.OAuth2AccessTokenService;

import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RequestMapping("/oauth/token")
@Controller
public class TokenEndpoint {
    @Autowired
    private OAuth2AuthorizationCodeService authorizationCodeService;
   
    @Autowired
    private OAuth2AccessTokenService accessTokenService;
   
    @Autowired
    private OAuth2AccessTokenGenerator accessTokenGenerator;

    @GetMapping
    public ResponseEntity<Map<String, ?>> getAccessToken(
            Principal principal,
            @RequestParam("grant_type") String grantType,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri) {
        return postAccessToken(principal, code, redirectUri);
    }

    /**
     * 密码模式的授权请求
     * 
     * @return
     */
    @PostMapping(params = { "grant_type=password" })
    public ResponseEntity<Map<String, ?>> postAccessToken(
            Principal principal, // client的信息
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("scope") String scope) {
        return null;
    }

    /**
     * 授权码模式的授权请求
     * 
     * @param principal
     * @param code
     * @param redirectUri
     * @return
     */
    @PostMapping(params = { "code", "grant_type=authorization_code" })
    public ResponseEntity<Map<String, ?>> postAccessToken(
            Principal principal,
            @RequestParam("code") String code,
            @RequestParam("redirect_uri") String redirectUri) {
        OAuth2ApprovalAuthenticationToken token = authorizationCodeService.get(code);
        // TODO 验证client是否匹配
        // TODO 验证RedirectUri
        if (Objects.isNull(token)) {
            throw new RuntimeException("指定授权码不正确");
        } else {
            OAuth2AccessTokenAuthentication authentication = accessTokenService
                    .save(accessTokenGenerator.generate(token));
            authorizationCodeService.delete(code);
            return ResponseEntity.ok(converToAccessTokenResponse(authentication));
        }
    }

    /**
     * 刷新token
     * 
     * @param refreshToken
     * @param scope
     * @return
     */
    @PostMapping(params = { "grant_type=refresh_token" })
    public ResponseEntity<Map<String, ?>> refreshToken(
            @RequestParam("refresh_token") String refreshToken,
            @RequestParam("scope") String scope) {
        return null;

    }

    /**
     * 转换accessToken到map
     * 
     * @param authentication
     * @return
     */
    private Map<String, ?> converToAccessTokenResponse(OAuth2AccessTokenAuthentication authentication) {
        OAuth2AccessToken accessToken = authentication.getAccessToken();
        long expiresIn = -1;
        Instant expiresAt = accessToken.getExpiresAt();
        if (expiresAt != null) {
            expiresIn = ChronoUnit.SECONDS.between(Instant.now(), expiresAt);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("access_token", accessToken.getTokenValue());
        result.put("token_type", accessToken.getTokenType().getValue());
        result.put("expires_in", expiresIn);
        result.put("refresh_token", "");
        return result;
    }

}
