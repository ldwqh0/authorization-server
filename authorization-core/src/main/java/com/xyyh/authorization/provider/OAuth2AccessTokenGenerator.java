package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2AccessTokenAuthentication;
import com.xyyh.authorization.core.OAuth2ApprovalAuthenticationToken;

public interface OAuth2AccessTokenGenerator {
    public OAuth2AccessTokenAuthentication generate(OAuth2ApprovalAuthenticationToken approvalAuthenticationToken);
}
