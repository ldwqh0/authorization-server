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
     * 应用的蜜月
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
    
    
    
}
