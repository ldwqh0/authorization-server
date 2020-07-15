package com.xyyh.authorization.provider;

import java.util.Map;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.xyyh.authorization.core.ApprovalResult;

public interface UserApprovalHandler {

    ApprovalResult approval(OAuth2AuthorizationRequest request, Map<String, String> approvalParameters);

}
