package com.xyyh.authorization.core;

import com.xyyh.authorization.endpoint.request.OpenidAuthorizationRequest;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * 用户授权处理器，用户处理用户的手动授权信息
 */
public interface UserApprovalHandler {

    /**
     * 对请求进行预检
     *
     * @param request
     * @param user
     * @return
     */
    public ApprovalResult preCheck(OpenidAuthorizationRequest request, Authentication user);

    /**
     * 进行请求验证
     *
     * @param request
     * @param approvalParameters
     * @return
     */
    public ApprovalResult approval(OpenidAuthorizationRequest request, Authentication user, Map<String, String> approvalParameters);

    /**
     * 更新授权结果
     *
     * @param result
     * @param user
     */
    public void updateAfterApproval(ApprovalResult result, Authentication user);
}
