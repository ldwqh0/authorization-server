package com.xyyh.authorization.endpoint.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Set;

/**
 * 客户端动态注册请求
 *
 * @see <a href=
 *      "https://openid.net/specs/openid-connect-registration-1_0.html#ClientMetadata">https://openid.net/specs/openid-connect-registration-1_0.html#ClientMetadata</a>
 */
public class ClientRegistrationRequest implements Serializable {

    private static final long serialVersionUID = -5463064530105926713L;

    @JsonProperty("redirect_uris")
    private Set<String> redirectUris;

    @JsonProperty("response_types")
    private Set<String> responseTypes;

}
