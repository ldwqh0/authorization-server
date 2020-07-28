package com.xyyh.authorization.client;

import java.io.Serializable;
import java.util.Set;

public interface ClientDetails extends Serializable {

    /**
     * 应用的ID
     *
     * @return
     */
    public String getClientId();

    /**
     * 应用的密钥
     *
     * @return
     */
    public String getClientSecret();

    /**
     * 应用的scope
     *
     * @return
     */
    public Set<String> getScope();

    public Set<String> getAuthorizedGrantTypes();

    public Set<String> getRegisteredRedirectUris();

}
