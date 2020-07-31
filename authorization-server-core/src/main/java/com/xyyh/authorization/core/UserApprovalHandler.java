package com.xyyh.authorization.core;

import com.xyyh.authorization.core.endpoint.OpenidAuthorizationRequest;
import org.springframework.security.core.Authentication;

import java.util.Map;

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
     * 更新结果
     *
     * @param result
     * @param user
     */
    public void updateAfterApproval(ApprovalResult result, Authentication user);
}
