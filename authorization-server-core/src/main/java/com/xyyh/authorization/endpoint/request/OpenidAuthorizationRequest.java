package com.xyyh.authorization.endpoint.request;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

public class OpenidAuthorizationRequest implements Serializable {

    private static final long serialVersionUID = 144721905123198109L;

    public static final String RESPONSE_TYPE_CODE = "code";
    public static final String RESPONSE_TYPE_ID_TOKEN = "id_token";
    public static final String RESPONSE_TYPE_TOKEN = "token";

    private Set<String> responseTypes;
    private String authorizationUri;
    private AuthorizationGrantType authorizationGrantType;
    private String clientId;
    private String redirectUri;
    private Set<String> scopes;
    private String state;
    private Map<String, Object> additionalParameters;
    private String authorizationRequestUri;
    private Map<String, Object> attributes;

    private OpenidAuthorizationRequest() {
    }

    ;

    public Set<String> getResponseTypes() {
        return responseTypes;
    }

    public String getAuthorizationUri() {
        return authorizationUri;
    }

    public AuthorizationGrantType getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    /**
     * 获取授权流
     *
     * @return
     */
    public OpenidAuthorizationFlow getFlow() {
        if (responseTypes.contains("code")) {
            if (responseTypes.size() == 1) {
                return OpenidAuthorizationFlow.CODE;
            } else {
                return OpenidAuthorizationFlow.HYBRID;
            }
        } else {
            return OpenidAuthorizationFlow.IMPLICIT;
        }
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public String getState() {
        return state;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    public String getAuthorizationRequestUri() {
        return authorizationRequestUri;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OpenidAuthorizationRequest value = new OpenidAuthorizationRequest();
        private static final String[] RESPONSE_TYPES = new String[]{RESPONSE_TYPE_CODE, RESPONSE_TYPE_ID_TOKEN, RESPONSE_TYPE_TOKEN};

        private static final String SPACE = " ";

        private Builder() {
        }

        public Builder redirectUri(String redirectUri) {
            value.redirectUri = redirectUri;
            return this;
        }

        ;

        public Builder scopes(Collection<String> scopes) {
            for (String scope : scopes) {
                scopes(scope);
            }
            return this;
        }

        public Builder scopes(String scopes) {
            getScopes().addAll(Arrays.asList(StringUtils.split(scopes, SPACE)));
            return this;
        }

        public Builder state(String state) {
            value.state = state;
            return this;
        }

        public Builder additionalParameters(Map<String, Object> additionalParameters) {
            getAdditionalParameters().putAll(additionalParameters);
            return this;
        }

        public Builder additionalParameter(String key, Object value) {
            getAdditionalParameters().put(key, value);
            return this;
        }

        public Builder authorizationRequestUri(String authorizationRequestUri) {
            value.authorizationRequestUri = authorizationRequestUri;
            return this;
        }

        ;

        public Builder attributes(Map<String, Object> attributes) {
            getAttributes().putAll(attributes);
            return this;
        }

        public Builder attribute(String key, String value) {
            getAttributes().put(key, value);
            return this;
        }

        public Builder responseType(Collection<String> responseTypes) {
            for (String type : responseTypes) {
                responseType(type);
            }
            return this;
        }

        public Builder responseType(String responseType) {
            if (StringUtils.isNotBlank(responseType)) {
                Set<String> responseTypes = getResponseTypes();
                String[] responseTypeArray = StringUtils.split(responseType, " ");
                for (String type : responseTypeArray) {
                    responseTypes.add(type);
                }
            }
            return this;
        }

        public Builder authorizationUri(String authorizationUri) {
            value.authorizationRequestUri = authorizationUri;
            return this;
        }

        public Builder authorizationGrantType(AuthorizationGrantType authorizationGrantType) {
            value.authorizationGrantType = authorizationGrantType;
            return this;
        }

        public Builder authorizationGrantType(String authorizationGrantType) {
            value.authorizationGrantType = new AuthorizationGrantType(authorizationGrantType);
            return this;
        }

        public Builder clientId(String clientId) {
            value.clientId = clientId;
            return this;
        }

        private Map<String, Object> getAdditionalParameters() {
            if (value.additionalParameters == null) {
                value.additionalParameters = new HashMap<>();
            }
            return value.additionalParameters;
        }

        private Map<String, Object> getAttributes() {
            if (value.attributes == null) {
                value.attributes = new HashMap<String, Object>();
            }
            return value.attributes;
        }

        private Set<String> getScopes() {
            if (value.scopes == null) {
                value.scopes = new HashSet<>();
            }
            return value.scopes;
        }

        private Set<String> getResponseTypes() {
            if (value.responseTypes == null) {
                value.responseTypes = new HashSet<String>();
            }
            return value.responseTypes;
        }

        public OpenidAuthorizationRequest build() {
            Set<String> responseTypes = value.responseTypes;
            // 判断responseType,并判断flow
            Assert.notEmpty(responseTypes, "The response type can not be empty!");
            return value;
        }
    }
}
