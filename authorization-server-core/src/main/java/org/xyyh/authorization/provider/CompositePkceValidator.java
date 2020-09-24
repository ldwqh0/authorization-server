package org.xyyh.authorization.provider;

import org.xyyh.authorization.core.PkceValidator;
import org.xyyh.authorization.exception.TokenRequestValidationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CompositePkceValidator implements PkceValidator {
    private final List<PkceValidator> validators;

    public CompositePkceValidator(PkceValidator... validators) {
        this.validators = Collections.unmodifiableList(Arrays.asList(validators));
    }

    @Override
    public void validate(String codeChallenge, String codeVerifier, String codeChallengeMethod) throws TokenRequestValidationException {
        for (PkceValidator validator : validators) {
            validator.validate(codeChallenge, codeVerifier, codeChallengeMethod);
        }
    }
}
