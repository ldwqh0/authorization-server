package org.xyyh.authorization.provider;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.xyyh.authorization.core.PkceValidator;
import org.xyyh.authorization.exception.TokenRequestValidationException;

/**
 * @see <a target="blank_" href="https://tools.ietf.org/html/rfc7636#section-4>https://tools.ietf.org/html/rfc7636</a>
 */
public class S256PkceValidator implements PkceValidator {

    @Override
    public void validate(String codeChallenge, String codeVerifier, String codeChallengeMethod) throws TokenRequestValidationException {
        if (CODE_CHALLENGE_METHOD_S256.equals(codeChallengeMethod)) {
            if (!StringUtils.equals(codeChallenge, encodeCodeVerifier(codeVerifier))) {
                throw new TokenRequestValidationException("invalid_grant");
            }
        }
    }

    private String encodeCodeVerifier(String codeVerifier) {
        return Base64.encodeBase64URLSafeString(DigestUtils.sha256(codeVerifier));
    }
}
