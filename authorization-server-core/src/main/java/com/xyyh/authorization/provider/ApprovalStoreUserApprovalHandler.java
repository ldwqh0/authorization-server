package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.core.ApprovalStoreService;
import com.xyyh.authorization.core.UserApprovalHandler;
import com.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Set;

/**
 * 可以保存用户授权接口的用户授权处理器
 */
public class ApprovalStoreUserApprovalHandler extends DefaultUserApprovalHandler implements UserApprovalHandler {

    private ApprovalStoreService approvalStoreService;

    public void setApprovalStoreService(ApprovalStoreService approvalStoreService) {
        this.approvalStoreService = approvalStoreService;
    }

    /**
     * 预检请求
     *
     * @param request
     * @param user
     * @return
     */
    @Override
    public ApprovalResult preCheck(OpenidAuthorizationRequest request, Authentication user) {
        /**
         * 保存的scope大于请求的scope时，返回之前的授权信息
         */
        Set<String> requestScopes = request.getScopes();
        return this.approvalStoreService.get(user.getName(), request.getClientId())
            .filter(preResult -> preResult.getScopes().containsAll(requestScopes))
            .orElseGet(DefaultApprovalResult::new);
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
