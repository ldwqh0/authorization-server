package com.xyyh.authorization.provider;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import com.xyyh.authorization.core.ApprovalResult;

/**
 * 默认的授权处理器
 * 
 * @author LiDong
 *
 */
public class DefaultUserApprovalHandler implements UserApprovalHandler {

    private String scopePrefix = "scope.";

    @Override
    public ApprovalResult approval(OAuth2AuthorizationRequest request, Map<String, String> approvalParameters) {
        Set<String> requestScopes = request.getScopes();
        Set<String> approvedScopes = new HashSet<String>(); // 授权允许的scope
        for (String requestScope : requestScopes) {
            String approvalValue = approvalParameters.get(scopePrefix + requestScope);
            if ("true".equals(approvalValue)) {
                approvedScopes.add(requestScope);
            }
        }
        DefaultApprovalResult result = new DefaultApprovalResult();
        result.setClientId(request.getClientId());
        result.setRedirectUri(request.getRedirectUri());
        if (CollectionUtils.isNotEmpty(approvedScopes)) {
            result.setApprovaled(true);
            result.setScope(approvedScopes);
        }
        return result;
    }
}
