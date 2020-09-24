package org.xyyh.authorization.provider;

import org.apache.commons.lang3.StringUtils;
import org.xyyh.authorization.core.PkceValidator;
import org.xyyh.authorization.exception.TokenRequestValidationException;

/**
 * @see <a target="blank_" href="https://tools.ietf.org/html/rfc7636#section-4>https://tools.ietf.org/html/rfc7636</a>
 */
public class PlainPkceValidator implements PkceValidator {

    @Override
    public void validate(String codeChallenge, String codeVerifier, String codeChallengeMethod) throws TokenRequestValidationException {
        if (CODE_CHALLENGE_METHOD_PLAIN.equals(codeChallengeMethod)) {
            if (!StringUtils.equals(codeChallenge, codeVerifier)) {
                throw new TokenRequestValidationException("invalid_grant");
            }
        }
    }
}
