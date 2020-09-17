package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.core.UserApprovalHandler;
import com.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 默认的授权处理器
 *
 * @author LiDong
 */
public class DefaultUserApprovalHandler implements UserApprovalHandler {


    @Override
    public ApprovalResult preCheck(OpenidAuthorizationRequest request, Authentication authentication) {
        // 返回一个默认结果，默认结果为未授权
        return ApprovalResult.of(request.getClientId());
    }

    @Override
    public ApprovalResult approval(OpenidAuthorizationRequest request, Authentication user, Map<String, String> approvalParameters) {
        Set<String> requestScopes = request.getScopes();
        Set<String> approvedScopes = new HashSet<>(); // 授权允许的scope
        for (String requestScope : requestScopes) {
            String scopePrefix = "scope.";
            String approvalValue = approvalParameters.get(scopePrefix + requestScope);
            if (StringUtils.equalsIgnoreCase("true", approvalValue)) {
                approvedScopes.add(requestScope);
            }
        }
        return ApprovalResult.of(request.getClientId(), approvedScopes, request.getRedirectUri());
    }

    @Override
    public void updateAfterApproval(ApprovalResult result, Authentication user) {

    }
}
