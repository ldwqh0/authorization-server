package org.xyyh.authorization.core;

import java.util.Optional;

/**
 * 授权结果存储服务，用于保存用户的授权结果信息
 */
public interface ApprovalResultStore {
    /**
     * 保存一条授权信息
     *
     * @param userid   用户id
     * @param clientId 连接id
     * @param result   授权结果
     */
    void save(String userid, String clientId, ApprovalResult result);

    /**
     * 获取授权信息
     *
     * @param userid   userId
     * @param clientId clientId
     * @return 用户的授权结果信息
     */
    Optional<ApprovalResult> get(String userid, String clientId);

    /**
     * 删除授权信息
     *
     * @param userid   userId
     * @param clientId clientId
     */
    void delete(String userid, String clientId);
}
