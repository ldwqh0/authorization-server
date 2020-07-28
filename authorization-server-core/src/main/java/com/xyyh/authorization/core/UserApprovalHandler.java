package com.xyyh.authorization.core;

import java.util.Map;

import com.xyyh.authorization.core.endpoint.OpenidAuthorizationRequest;

public interface UserApprovalHandler {

    ApprovalResult approval(OpenidAuthorizationRequest request, Map<String, String> approvalParameters);

}
