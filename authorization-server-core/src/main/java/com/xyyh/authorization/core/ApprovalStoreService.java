package com.xyyh.authorization.core;

public interface ApprovalStoreService {
    /**
     * 保存一条授权信息
     *
     * @param userid
     * @param clientId
     * @param result
     */
    public void save(String userid, String clientId, ApprovalResult result);

    /**
     * 获取授权信息
     *
     * @param userid
     * @param clientId
     * @return
     */
    public ApprovalResult get(String userid, String clientId);

    /**
     * 删除授权信息
     *
     * @param userid
     * @param clientId
     */
    public void delete(String userid, String clientId);
}
