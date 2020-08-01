package com.xyyh.authorization.core;

public interface OAuth2AuthorizationCodeService {

    /**
     * 保存一个code和一个授权的关系
     *
     * @param code
     * @return
     */
    public OAuth2AuthorizationCode save(OAuth2AuthorizationCode code, OAuth2Authentication authentication);


    /**
     * 消费指定授权码
     *
     * @param code
     */
    public OAuth2Authentication consume(String code);

}
