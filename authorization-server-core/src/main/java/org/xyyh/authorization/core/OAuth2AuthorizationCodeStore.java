package org.xyyh.authorization.core;

public interface OAuth2AuthorizationCodeStore {

    /**
     * 保存一个code和一个授权的关系
     *
     * @param code           要保存的授权码信息
     * @param authentication 权限信息
     * @return 包存的授权信息
     */
    OAuth2AuthorizationCode save(OAuth2AuthorizationCode code, OAuth2Authentication authentication);


    /**
     * 消费指定授权码,返回授权信息
     *
     * @param code 授权码
     */
    OAuth2Authentication consume(String code);

}
