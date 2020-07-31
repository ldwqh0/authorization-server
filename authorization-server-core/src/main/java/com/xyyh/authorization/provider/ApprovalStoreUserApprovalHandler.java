package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.core.ApprovalStoreService;
import com.xyyh.authorization.core.UserApprovalHandler;
import com.xyyh.authorization.core.endpoint.OpenidAuthorizationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import java.util.Map;

public class ApprovalStoreUserApprovalHandler extends DefaultUserApprovalHandler implements UserApprovalHandler {

    @Autowired
    private ApprovalStoreService approvalStoreService;

    /**
     * 预检请求
     *
     * @param request
     * @param user
     * @return
     */
    @Override
    public ApprovalResult preCheck(OpenidAuthorizationRequest request, Authentication user) {
        return this.approvalStoreService.get(user.getName(), request.getClientId());
    }

    /**
     * 授权请求
     *
     * @param request
     * @param user
     * @param approvalParameters
     * @return
     */
    @Override
    public ApprovalResult approval(OpenidAuthorizationRequest request, Authentication user, Map<String, String> approvalParameters) {
        ApprovalResult result = super.approval(request, user, approvalParameters);
        approvalStoreService.save(user.getName(), request.getClientId(), result);
        return result;
    }

    /**
     * 更新授权请求
     *
     * @param result
     * @param user
     */
    @Override
    public void updateAfterApproval(ApprovalResult result, Authentication user) {
        this.approvalStoreService.save(user.getName(), result.getClientId(), result);
    }
}
