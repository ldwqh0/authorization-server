package com.xyyh.authorization.core;

public interface OAuth2AccessTokenGenerator {
    public OAuth2AccessTokenAuthentication generate(OAuth2ApprovalAuthenticationToken approvalAuthenticationToken);
}
