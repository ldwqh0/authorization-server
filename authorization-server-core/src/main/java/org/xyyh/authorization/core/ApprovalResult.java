package org.xyyh.authorization.core;

import org.xyyh.authorization.collect.CollectionUtils;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Set;

import static org.xyyh.authorization.collect.Sets.hashSet;

/**
 * 用户授权结果
 *
 * @author LiDong
 */
public interface ApprovalResult extends Serializable {

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

    ZonedDateTime getExpireAt();

    static ApprovalResult empty() {
        return of(Collections.emptySet(), Collections.emptySet());
    }

    static ApprovalResult of(Set<String> scopes) {
        return of(scopes, Collections.emptySet());
    }

    static ApprovalResult of(Set<String> scopes, Set<String> redirectUris) {
        return new DefaultApprovalResult(scopes, redirectUris);
    }

    static ApprovalResult of(Set<String> scopes, String... redirectUris) {
        return new DefaultApprovalResult(scopes, hashSet(redirectUris));
    }

    static ApprovalResult of(Set<String> scopes, Set<String> redirectUris, ZonedDateTime expireAt) {
        return new DefaultApprovalResult(scopes, redirectUris, expireAt);
    }

}

class DefaultApprovalResult implements ApprovalResult {

    private static final long serialVersionUID = 8068718072536160467L;

    private final Set<String> redirectUris;

    private final Set<String> scopes;

    private final ZonedDateTime expireAt;

    DefaultApprovalResult(Set<String> scopes, Set<String> redirectUris) {
        this(scopes, redirectUris, ZonedDateTime.now().plusDays(30));
    }

    DefaultApprovalResult(Set<String> scopes, Set<String> redirectUris, ZonedDateTime expireAt) {
        this.redirectUris = redirectUris;
        this.scopes = scopes;
        this.expireAt = expireAt;
    }

    @Override
    public Set<String> getScopes() {
        return this.scopes;
    }

    @Override
    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    @Override
    public ZonedDateTime getExpireAt() {
        return expireAt;
    }
}
