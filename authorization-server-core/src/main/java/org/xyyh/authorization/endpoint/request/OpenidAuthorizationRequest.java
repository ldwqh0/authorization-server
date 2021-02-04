package org.xyyh.authorization.endpoint.request;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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

    private static final String SPACE_REGEX = "[\\s+]";

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

    /**
     * 根据传入参数，创建授权请求信息
     *
     * @param uri        请求 URI
     * @param parameters 其它请求参数
     * @return 授权请求
     */
    public static OpenidAuthorizationRequest of(String uri, MultiValueMap<String, String> parameters) {
        Map<String, String> additionalParameters = new HashMap<>();
        parameters.entrySet().stream()
                .filter(e -> !e.getKey().equals(OAuth2ParameterNames.RESPONSE_TYPE) &&
                        !e.getKey().equals(OAuth2ParameterNames.CLIENT_ID) &&
                        !e.getKey().equals(OAuth2ParameterNames.REDIRECT_URI) &&
                        !e.getKey().equals(OAuth2ParameterNames.SCOPE) &&
                        !e.getKey().equals(OAuth2ParameterNames.STATE))
                .forEach(e -> additionalParameters.put(e.getKey(), e.getValue().get(0)));
        Set<String> scopes = parameters.get(OAuth2ParameterNames.SCOPE)
                .stream().flatMap(v -> Arrays.stream(v.split(SPACE_REGEX)))
                .collect(Collectors.toSet());
        Set<String> responseTypes = parameters.get(OAuth2ParameterNames.RESPONSE_TYPE)
                .stream().flatMap(v -> Arrays.stream(v.split(SPACE_REGEX)))
                .collect(Collectors.toSet());
        return new OpenidAuthorizationRequest(
                responseTypes,
                null,
                parameters.getFirst(OAuth2ParameterNames.CLIENT_ID),
                parameters.getFirst(OAuth2ParameterNames.REDIRECT_URI),
                scopes,
                parameters.getFirst(OAuth2ParameterNames.STATE),
                additionalParameters,
                uri,
                Collections.emptyMap()
        );
    }
}
