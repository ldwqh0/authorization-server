package org.xyyh.authorization.client;

import org.xyyh.authorization.exception.NoSuchClientException;

public interface ClientDetailsService {

    ClientDetails loadClientByClientId(String clientId) throws NoSuchClientException;
}
