package com.xyyh.authorization.client;

public interface ClientDetailsService {

    public ClientDetails loadClientByClientId(String clientId);
}
