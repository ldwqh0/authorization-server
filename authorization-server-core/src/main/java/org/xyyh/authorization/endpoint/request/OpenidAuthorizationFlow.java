package org.xyyh.authorization.endpoint.request;

public enum OpenidAuthorizationFlow {
    /**
     * Authentication using the Authorization Code Flow
     *
     * @see <a href=
     * "https://openid.net/specs/openid-connect-core-1_0.html#CodeFlowAuth">https://openid.net/specs/openid-connect-core-1_0.html#CodeFlowAuth</a>
     */
    CODE,
    /**
     * Authentication using the Implicit Flow
     *
     * @see <a href=
     * "https://openid.net/specs/openid-connect-core-1_0.html#ImplicitFlowAuth">https://openid.net/specs/openid-connect-core-1_0.html#ImplicitFlowAuth</a>
     */
    IMPLICIT,
    /**
     * Authentication using the Hybrid Flow
     *
     * @see <a href=
     * "https://openid.net/specs/openid-connect-core-1_0.html#HybridFlowAuth">https://openid.net/specs/openid-connect-core-1_0.html#HybridFlowAuth</a>
     */
    HYBRID
}
