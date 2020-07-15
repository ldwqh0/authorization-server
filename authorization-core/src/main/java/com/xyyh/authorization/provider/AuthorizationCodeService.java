package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.OAuth2Authentication;

public interface AuthorizationCodeService {

    /**
     * 根据信息创建授权码
     * 
     * @param code
     * @param result
     * @param principal
     * @return
     */
    String create(OAuth2Authentication auth);

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
    OAuth2Authentication get(String code);

}
