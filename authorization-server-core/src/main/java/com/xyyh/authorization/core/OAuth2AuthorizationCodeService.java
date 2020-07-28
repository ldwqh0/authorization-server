package com.xyyh.authorization.core;

public interface OAuth2AuthorizationCodeService {

    /**
     * 根据信息创建授权码
     *
     * @param authorization 验证后的授权信息
     * @return
     */
    String create(OAuth2Authentication authorization);

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
