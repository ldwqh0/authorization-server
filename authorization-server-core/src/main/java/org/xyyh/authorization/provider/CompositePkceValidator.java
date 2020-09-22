package org.xyyh.authorization.provider;

import org.xyyh.authorization.core.PkceValidator;

import java.util.*;

import static org.xyyh.authorization.collect.Sets.hashSet;

public class CompositePkceValidator implements PkceValidator {
    private final List<PkceValidator> validators;

    public CompositePkceValidator(PkceValidator... validators) {
        this.validators = Collections.unmodifiableList(Arrays.asList(validators));
    }

    @Override
    public void validate(String codeChallenge, String codeVerifier, String codeChallengeMethod) {
        for (PkceValidator validator : validators) {
            validator.validate(codeChallenge, codeVerifier, codeChallengeMethod);
        }
    }
}
