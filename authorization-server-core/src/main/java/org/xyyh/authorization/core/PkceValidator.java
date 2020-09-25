package org.xyyh.authorization.core;

import org.xyyh.authorization.exception.TokenRequestValidationException;

/**
 * pkce 验证器
 *
 * @see <a target="_blank" href="https://tools.ietf.org/html/rfc7636">Proof Key for Code Exchange by OAuth Public Clients</a>
 */
@FunctionalInterface
public interface PkceValidator {
    String CODE_CHALLENGE_METHOD_PLAIN = "plain";
    String CODE_CHALLENGE_METHOD_S256 = "S256";

    /**
     * 验证access token请求的参数是否正确
     *
     * @param codeChallenge       <a href="https://tools.ietf.org/html/rfc7636#section-4.2">Client Creates the Code Challenge</a>
     * @param codeVerifier        <a href="https://tools.ietf.org/html/rfc7636#section-4.1">Client Creates a Code Verifier</a>
     * @param codeChallengeMethod value must be plain S256
     * @throws TokenRequestValidationException 参数请求验证出错
     */
    void validate(String codeChallenge, String codeVerifier, String codeChallengeMethod) throws TokenRequestValidationException;
}

