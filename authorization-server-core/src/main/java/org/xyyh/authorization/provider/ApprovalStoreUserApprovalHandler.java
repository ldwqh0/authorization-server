package org.xyyh.authorization.provider;

import org.springframework.security.core.Authentication;
import org.xyyh.authorization.core.ApprovalResult;
import org.xyyh.authorization.core.ApprovalResultStore;
import org.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * 可以保存用户授权接口的用户授权处理器
 */
public class ApprovalStoreUserApprovalHandler extends DefaultUserApprovalHandler {

    private final ApprovalResultStore approvalStoreService;

    public ApprovalStoreUserApprovalHandler(ApprovalResultStore approvalStoreService) {
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
        final String username = user.getName();
        final String clientId = request.getClientId();
        Optional<ApprovalResult> savedResult = this.approvalStoreService.get(username, clientId);
        if (savedResult.isPresent()) {
            ApprovalResult preResult = savedResult.get();
            if (preResult.getExpireAt().isAfter(ZonedDateTime.now())) {
                if (preResult.getScopes().containsAll(requestScopes) && preResult.getRedirectUris().contains(requestRedirectUri)) {
                    return ApprovalResult.of(requestScopes, requestRedirectUri);
                }
            } else {
                this.approvalStoreService.delete(username, clientId);
            }
        }
        return ApprovalResult.empty();
    }

    /**
     * 更新授权请求
     *
     * @param result 授权结果
     * @param user   用户信息
     */
    @Override
    public void updateAfterApproval(OpenidAuthorizationRequest request, Authentication user, ApprovalResult result) {
        this.approvalStoreService.save(user.getName(), request.getClientId(), result);
    }
}
