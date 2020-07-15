package com.xyyh.authorization.web;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

import com.xyyh.authorization.exception.RequestValidationException;

public class AuthorizationRequest implements Serializable {
    private static final long serialVersionUID = 9006110161000995191L;
    private String responseType;
    private String clientId;
    private String state;
    private String redirectUri;
    private Set<String> scope;

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public static AuthorizationRequest createRequest(MultiValueMap<String, String> params) {
        String clientId = params.getFirst("client_id");
        String redirectUri = params.getFirst("redirect_uri");
        String responseType = params.getFirst("response_type");
        String state = params.getFirst("state");
        List<String> scopes = params.get("scope");
        assetNotEmpty(clientId, "the client_id can not be null");
        assetNotEmpty(responseType, "the response_type can not be null");
        assetNotEmpty(scopes, "the scope can not be null");
        AuthorizationRequest request = new AuthorizationRequest();
        request.setClientId(clientId);
        request.setRedirectUri(redirectUri);
        request.setResponseType(responseType);
        request.setScope(new HashSet<>(scopes));
        request.setState(state);
        return request;
    }

    private static void assetNotEmpty(String str, String message) {
        if (StringUtils.isEmpty(str)) {
            throw new RequestValidationException(message);
        }
    }

    private static void assetNotEmpty(List<String> strList, String message) {
        if (CollectionUtils.isEmpty(strList)) {
            throw new RequestValidationException(message);
        } else {
            for (String str : strList) {
                assetNotEmpty(str, message);
            }
        }
    }

}
