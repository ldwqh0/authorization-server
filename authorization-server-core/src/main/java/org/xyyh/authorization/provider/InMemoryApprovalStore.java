package org.xyyh.authorization.provider;

import org.xyyh.authorization.core.ApprovalResult;
import org.xyyh.authorization.core.ApprovalResultStore;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.xyyh.authorization.collect.Sets.hashSet;
import static org.xyyh.authorization.collect.Sets.merge;

public class InMemoryApprovalStore implements ApprovalResultStore {
    private final Map<UnionKey, ApprovalResultModel> storage = new ConcurrentHashMap<>();

    static String[] mergeToArray(Set<String> origin, String... add) {
        Set<String> result = merge(origin, add);
        return result.toArray(new String[0]);
    }


    @Override
    public void save(String userid, String clientId, ApprovalResult result) {
        UnionKey key = new UnionKey(userid, clientId);
        ApprovalResultModel exit = storage.get(key);
        if (exit == null) {
            storage.put(key, new ApprovalResultModel(result));
        } else {
            exit.merge(result);
        }
    }

    @Override
    public Optional<ApprovalResult> get(String userid, String clientId) {
        return Optional.ofNullable(storage.get(new UnionKey(userid, clientId))).map(ApprovalResultModel::toApprovalResult);
    }

    @Override
    public void delete(String userid, String clientId) {
        this.storage.remove(new UnionKey(userid, clientId));
    }

    static class UnionKey implements Serializable {
        private static final long serialVersionUID = -4656419319790202947L;
        private final String userid;
        private final String clientId;

        public UnionKey(String userid, String clientId) {
            this.userid = userid;
            this.clientId = clientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnionKey key = (UnionKey) o;
            return Objects.equals(userid, key.userid) &&
                    Objects.equals(clientId, key.clientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userid, clientId);
        }
    }

    static class ApprovalResultModel implements Serializable {
        private static final long serialVersionUID = -794253127779866641L;
        private String[] redirectUris;
        private String[] scopes;
        private ZonedDateTime expireAt;

        public ApprovalResultModel(ApprovalResult result) {
            this.scopes = mergeToArray(result.getScopes());
            this.redirectUris = mergeToArray(result.getRedirectUris());
            this.expireAt = result.getExpireAt();
        }

        /**
         * 将一个新的请求合并到现有请求中
         *
         * @param result 要合并的请求
         */
        public void merge(ApprovalResult result) {
            this.scopes = mergeToArray(result.getScopes(), this.scopes);
            this.redirectUris = mergeToArray(result.getRedirectUris(), this.redirectUris);
            this.expireAt = result.getExpireAt();
        }

        public ApprovalResult toApprovalResult() {
            return ApprovalResult.of(hashSet(scopes), hashSet(redirectUris));
        }
    }
}


