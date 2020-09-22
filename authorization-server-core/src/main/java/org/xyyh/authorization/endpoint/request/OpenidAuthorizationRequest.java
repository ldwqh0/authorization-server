package org.xyyh.authorization.endpoint.request;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

import static org.xyyh.authorization.collect.Maps.hashMap;
import static org.xyyh.authorization.collect.Sets.hashSet;

/**
 * 一个可以包含openid请求参数的请求封装<br>
 * openid 和oauth2 请求的区别在于，openid请求的 response_type 参数是以空格分割的多个参数
 * 也就是是 openid 的 Hybrid Flow
 *
 * @see <a target="_blank" href="https://openid.net/specs/openid-connect-core-1_0.html#HybridFlowAuth">Authentication using the Hybrid Flow</a>
 * @see org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
 */
public class OpenidAuthorizationRequest implements Serializable {

    private static final long serialVersionUID = 144721905123198109L;

    public static final String RESPONSE_TYPE_CODE = "code";
    public static final String RESPONSE_TYPE_ID_TOKEN = "id_token";
    public static final String RESPONSE_TYPE_TOKEN = "token";

    private final Set<String> responseTypes;
    //    private final String authorizationUri;
    private final AuthorizationGrantType authorizationGrantType;
    private final String clientId;
    private final String redirectUri;
    private final Set<String> scopes;
    private final String state;
    private final Map<String, String> additionalParameters;
    private final String authorizationRequestUri;
    private final Map<String, Object> attributes;

    private OpenidAuthorizationRequest(Set<String> responseTypes,
                                       AuthorizationGrantType authorizationGrantType,
                                       String clientId,
                                       String redirectUri,
                                       Set<String> scopes,
                                       String state,
                                       Map<String, String> additionalParameters,
                                       String authorizationRequestUri,
                                       Map<String, Object> attributes) {
        this.responseTypes = hashSet(responseTypes);
//        this.authorizationUri = authorizationUri;
        this.authorizationGrantType = authorizationGrantType;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.scopes = hashSet(scopes);
        this.state = state;
        this.additionalParameters = hashMap(additionalParameters);
        this.authorizationRequestUri = authorizationRequestUri;
        this.attributes = hashMap(attributes);
    }

    public Set<String> getResponseTypes() {
        return Collections.unmodifiableSet(responseTypes);
    }

//    public String getAuthorizationUri() {
//        return authorizationUri;
//    }

    public AuthorizationGrantType getAuthorizationGrantType() {
        return authorizationGrantType;
    }

    /**
     * 获取授权流
     *
     * @return 授权流程
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
        return Collections.unmodifiableSet(scopes);
    }

    public String getState() {
        return state;
    }

    public Map<String, String> getAdditionalParameters() {
        return Collections.unmodifiableMap(additionalParameters);
    }

    public String getAuthorizationRequestUri() {
        return authorizationRequestUri;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final String SPACE_REGEX = "[\\s+]";
        private String redirectUri;
        private String state;
        private String authorizationRequestUri;
        private Map<String, String> additionalParameters;
        //        private String authorizationUri;
        private AuthorizationGrantType authorizationGrantType;
        private String clientId;
        private Set<String> scopes;
        private Map<String, Object> attributes;
        private Set<String> responseTypes;

        private Builder() {
        }

        public Builder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public Builder scopes(Collection<String> scopes) {
            for (String scope : scopes) {
                scopes(scope);
            }
            return this;
        }

        public Builder scopes(String scopes) {
            if (StringUtils.isNotBlank(scopes)) {
                getScopes().addAll(Arrays.asList(scopes.split(SPACE_REGEX)));
            }
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder additionalParameters(Map<String, String> additionalParameters) {
            getAdditionalParameters().putAll(additionalParameters);
            return this;
        }

        public Builder additionalParameter(String key, String value) {
            getAdditionalParameters().put(key, value);
            return this;
        }

        public Builder authorizationRequestUri(String authorizationRequestUri) {
            this.authorizationRequestUri = authorizationRequestUri;
            return this;
        }

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
                String[] responseTypeArray = responseType.split(SPACE_REGEX);
                responseTypes.addAll(Arrays.asList(responseTypeArray));
            }
            return this;
        }

//        public Builder authorizationUri(String authorizationUri) {
//            this.authorizationUri = authorizationUri;
//            return this;
//        }

        public Builder authorizationGrantType(AuthorizationGrantType authorizationGrantType) {
            this.authorizationGrantType = authorizationGrantType;
            return this;
        }

        public Builder authorizationGrantType(String authorizationGrantType) {
            this.authorizationGrantType = new AuthorizationGrantType(authorizationGrantType);
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        private Map<String, String> getAdditionalParameters() {
            if (this.additionalParameters == null) {
                this.additionalParameters = new HashMap<>();
            }
            return this.additionalParameters;
        }

        private Map<String, Object> getAttributes() {
            if (this.attributes == null) {
                this.attributes = new HashMap<>();
            }
            return this.attributes;
        }

        private Set<String> getScopes() {
            if (this.scopes == null) {
                this.scopes = new HashSet<>();
            }
            return this.scopes;
        }

        private Set<String> getResponseTypes() {
            if (this.responseTypes == null) {
                this.responseTypes = new HashSet<>();
            }
            return this.responseTypes;
        }

        public OpenidAuthorizationRequest build() {
            Set<String> responseTypes = this.responseTypes;
            // 判断responseType,并判断flow
            Assert.notEmpty(responseTypes, "The response type can not be empty!");
            return new OpenidAuthorizationRequest(
                this.responseTypes,
                this.authorizationGrantType,
                this.clientId,
                this.redirectUri,
                this.scopes,
                this.state,
                this.additionalParameters,
                this.authorizationRequestUri,
                this.attributes
            );
        }
    }
}