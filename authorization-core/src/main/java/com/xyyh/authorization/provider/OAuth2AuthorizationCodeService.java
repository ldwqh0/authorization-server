package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2ApprovalAuthenticationToken;

public interface OAuth2AuthorizationCodeService {

    /**
     * 根据信息创建授权码
     * 
     * @param code
     * @param result
     * @param principal
     * @return
     */
    String create(OAuth2ApprovalAuthenticationToken auth);

    /**
     * 删除指定授权码
     * 
     * @param code
     */
    void delete(String code);

    /**
     * 获取指定授权码
     * 
     * @param code
     */
    OAuth2ApprovalAuthenticationToken get(String code);

}
