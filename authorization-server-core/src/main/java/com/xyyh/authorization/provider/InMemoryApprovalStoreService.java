package com.xyyh.authorization.provider;

import com.xyyh.authorization.core.ApprovalResult;
import com.xyyh.authorization.core.ApprovalStoreService;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryApprovalStoreService implements ApprovalStoreService {
    private Map<UnionKey, ApprovalResult> storage = new ConcurrentHashMap<>();

    @Override
    public void save(String userid, String clientId, ApprovalResult result) {
        this.storage.put(new UnionKey(userid, clientId), result);
    }

    @Override
    public ApprovalResult get(String userid, String clientId) {
        return this.storage.get(new UnionKey(userid, clientId));
    }

    @Override
    public void delete(String userid, String clientId) {
        this.storage.remove(new UnionKey(userid, clientId));
    }

    static class UnionKey {
        private String userid;
        private String clientId;

        public UnionKey(String userid, String clientId) {
            this.userid = userid;
            this.clientId = clientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnionKey unionKey = (UnionKey) o;
            return Objects.equals(userid, unionKey.userid) &&
                Objects.equals(clientId, unionKey.clientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userid, clientId);
        }
    }
}
