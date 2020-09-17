package com.xyyh.authorization.client;

public interface ClientDetailsService {

    ClientDetails loadClientByClientId(String clientId);
}
