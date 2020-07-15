package com.xyyh.authorization.core;

import java.io.Serializable;
import java.util.Set;

/**
 * 表示授权结果
 * 
 * @author LiDong
 *
 */
public interface ApprovalResult extends Serializable {

    /**
     * 授权的clientId
     * 
     * @return
     */
    public String getClientId();

    /**
     * 授权结果
     * 
     * @return
     */
    public boolean isApprovaled();

    /**
     * 授权完成后允许的范围
     * 
     * @return
     */
    public Set<String> getScope();

}
