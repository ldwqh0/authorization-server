package com.xyyh.authorization.client;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xyyh.authorization.exception.NoSuchClientException;

public class InMemoryClientDetailsService implements ClientDetailsService {

    private final Map<String, ClientDetails> clients = new ConcurrentHashMap<>();

    @Override
    public ClientDetails loadClientByClientId(String clientId) {
        ClientDetails details = clients.get(clientId);
        if (details == null) {
            throw new NoSuchClientException("No client with requested id: " + clientId);
        }
        return details;
    }

    public void addClients(Collection<ClientDetails> clients) {
        for (ClientDetails client : clients) {
            addClient(client);
        }
    }

    public void addClient(ClientDetails client) {
        this.clients.put(client.getClientId(), client);
    }
}
