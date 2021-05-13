package org.xyyh.authorization.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务发现端点，可以自动发现服务
 */
@RequestMapping("/.wellknow")
public class ServerDiscoveryEndpoint {

    /**
     * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfig">openid-connect-discovery</a>
     * @see <a href="http://tools.ietf.org/html/rfc5785">rfc5785</a>
     */
    @GetMapping("openid-configuration")
    public Map<String, Object> getOpenidConfiguration() {
        Map<String, Object> result = new HashMap<>();

        /**
         * 以下参考google的实现
         */
        result.put("issuer", "");
        result.put("authorization_endpoint", "");
        result.put("device_authorization_endpoint", "");
        result.put("token_endpoint", "");
        result.put("userinfo_endpoint", "");
        result.put("revocation_endpoint", "");
        result.put("jwks_uri", "");
        result.put("response_types_supported", "");
        result.put("subject_types_supported", "");
        result.put("id_token_signing_alg_values_supported", "");
        result.put("scopes_supported", "");
        result.put("token_endpoint_auth_methods_supported", "");
        result.put("claims_supported", "");
        result.put("code_challenge_methods_supported", "");
        result.put("grant_types_supported", "");
        result.put("registration_endpoint", "");

        //TODO 待实现
        return result;
    }

    /**
     * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-discovery-10“>https://tools.ietf.org/html/draft-ietf-oauth-discovery-10</a>
     */
    @GetMapping("oauth-authorization-server")
    public String getOs() {
        //TODO 待实现
        return "";
    }
}
