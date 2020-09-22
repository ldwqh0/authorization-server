package org.xyyh.authorization.core;

public interface PkceValidator {
    public static final String CODE_CHALLENGE_METHOD_PLAIN = "plain";
    public static final String CODE_CHALLENGE_METHOD_S256 = "S256";

    void validate(String codeChallenge, String codeVerifier, String codeChallengeMethod);
}

