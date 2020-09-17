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
     * @param request 要预检的请求
     * @param user    请求用户
     * @return 预检测结果
     */
    @Override
    public ApprovalResult preCheck(OpenidAuthorizationRequest request, Authentication user) {
        // 保存的scope大于请求的scope时，返回之前的授权信息
        final Set<String> requestScopes = request.getScopes();
        final String requestRedirectUri = request.getRedirectUri();
        return this.approvalStoreService.get(user.getName(), request.getClientId())
            .filter(preResult -> preResult.getScopes().containsAll(requestScopes))
            .filter(preResult -> preResult.getRedirectUris().contains(requestRedirectUri))
            .orElseGet(() -> ApprovalResult.of(request.getClientId()));
    }

    /**
     * 根据用户的请求参数对请求进行校验
     *
     * @param request            授权请求
     * @param user               授权用户
     * @param approvalParameters 用户请求参数
     * @return 授权结果
     */
    @Override
    public ApprovalResult approval(OpenidAuthorizationRequest request, Authentication user,
                                   Map<String, String> approvalParameters) {
        ApprovalResult result = super.approval(request, user, approvalParameters);
        approvalStoreService.save(user.getName(), request.getClientId(), result);
        return result;
    }

    /**
     * 更新授权请求
     *
     * @param result 授权结果
     * @param user   用户信息
     */
    @Override
    public void updateAfterApproval(ApprovalResult result, Authentication user) {
        this.approvalStoreService.save(user.getName(), result.getClientId(), result);
    }
}
