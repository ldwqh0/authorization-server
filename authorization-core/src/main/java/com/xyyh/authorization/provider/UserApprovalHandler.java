package com.xyyh.authorization.provider;

import java.util.Map;

import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.web.AuthorizationRequest;

public interface UserApprovalHandler {

    ApprovalResult approval(AuthorizationRequest request, Map<String, String> approvalParameters);

}
