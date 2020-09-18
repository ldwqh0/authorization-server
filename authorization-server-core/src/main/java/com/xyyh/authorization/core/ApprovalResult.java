package com.xyyh.authorization.core;

import com.xyyh.authorization.collect.CollectionUtils;
import com.xyyh.authorization.collect.Sets;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * 表示授权结果
 *
 * @author LiDong
 */
public interface ApprovalResult extends Serializable {

    /**
     * 授权的clientId
     */
    String getClientId();

    /**
     * 授权结果
     */
    default boolean isApproved() {
        return CollectionUtils.isNotEmpty(getScopes());
    }

    /**
     * 授权完成后允许的范围
     */
    Set<String> getScopes();

    Set<String> getRedirectUris();

    public static ApprovalResult of(String clientId) {
        return of(clientId, Collections.emptySet(), Collections.emptySet());
    }

    public static ApprovalResult of(String clientId, Set<String> scopes) {
        return of(clientId, scopes, Collections.emptySet());
    }

    public static ApprovalResult of(String clientId, Set<String> scopes, Set<String> redirectUris) {
        return new DefaultApprovalResult(clientId, scopes, redirectUris);
    }

    public static ApprovalResult of(String clientId, Set<String> scopes, String... redirectUris) {
        return new DefaultApprovalResult(clientId, scopes, Sets.hashSet(redirectUris));
    }

}

class DefaultApprovalResult implements ApprovalResult {

    private static final long serialVersionUID = 8068718072536160467L;

    private final String clientId;

    private final Set<String> redirectUris;

    private final Set<String> scopes;

    public DefaultApprovalResult(String clientId, Set<String> scopes, Set<String> redirectUris) {
        this.clientId = clientId;
        this.redirectUris = redirectUris;
        this.scopes = scopes;
    }


    @Override
    public Set<String> getScopes() {
        return this.scopes;
    }


    @Override
    public String getClientId() {
        return this.clientId;
    }


    @Override
    public Set<String> getRedirectUris() {
        return redirectUris;
    }
}
