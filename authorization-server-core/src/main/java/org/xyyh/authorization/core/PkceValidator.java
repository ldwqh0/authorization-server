package org.xyyh.authorization.core;

import org.xyyh.authorization.exception.TokenRequestValidationException;

public interface PkceValidator {
    public static final String CODE_CHALLENGE_METHOD_PLAIN = "plain";
    public static final String CODE_CHALLENGE_METHOD_S256 = "S256";

    void validate(String codeChallenge, String codeVerifier, String codeChallengeMethod) throws TokenRequestValidationException;
}

