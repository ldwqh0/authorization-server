package com.xyyh.authorization.core;

import java.util.Map;

import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

public interface UserApprovalHandler {

    ApprovalResult approval(OAuth2AuthorizationRequest request, Map<String, String> approvalParameters);

}
